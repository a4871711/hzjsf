package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.dao.PtInstallmentRuleApiDao;
import com.dlc.modules.api.dao.PtOrderInstallmentBillDao;
import com.dlc.modules.api.dao.PtOrderInstallmentPlanDao;
import com.dlc.modules.api.dao.PtPrivateOrderDao;
import com.dlc.modules.api.entity.PtInstallmentRuleEntity;
import com.dlc.modules.api.entity.PtOrderInstallmentBillEntity;
import com.dlc.modules.api.entity.PtOrderInstallmentPlanEntity;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.MemberPrivateBenefitService;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.PtInstallmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * 私教订单分期账单执行 Service 实现(第20步资金域)。落在 api.service.impl,命中事务切面(REQUIRED)。
 * <p>期数口径:installment_count=含首付期总期数,period_no=1 即首付期(详细文档§5.2);金额末期吸收尾差
 * 保证 Σdue=total(BigDecimal HALF_UP,前 n-1 期均分、末期=total-已分)。api 目录 XML 改动须重启 Tomcat。</p>
 *
 * @author claude
 */
@Service("ptInstallmentService")
public class PtInstallmentServiceImpl implements PtInstallmentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** 计划状态:1进行中 */
    private static final int PLAN_RUNNING = 1;
    /** 计划状态:2已结清 */
    private static final int PLAN_SETTLED = 2;
    /** 账单状态:0待支付 */
    private static final int BILL_UNPAID = 0;
    /** 账单状态:1已支付 */
    private static final int BILL_PAID = 1;
    /** 账单状态:2已逾期 */
    private static final int BILL_OVERDUE = 2;

    @Autowired
    private PtOrderInstallmentPlanDao ptOrderInstallmentPlanDao;
    @Autowired
    private PtOrderInstallmentBillDao ptOrderInstallmentBillDao;
    @Autowired
    private PtInstallmentRuleApiDao ptInstallmentRuleApiDao;
    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private MemberPrivateBenefitService memberPrivateBenefitService;
    @Autowired
    private PayService payService;

    /* ==================== 5.2 计划与账单生成(首付到账后,下单事务内) ==================== */

    @Override
    public void createInstallmentPlan(PtPrivateOrderEntity order, BigDecimal paidAmount) {
        if (order == null || order.getId() == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 幂等:一订单一计划,已建则跳过(重复回调/并发)
        if (ptOrderInstallmentPlanDao.selectByOrderId(order.getId()) != null) {
            log.info("分期计划已存在,幂等跳过 orderId={}", order.getId());
            return;
        }
        // 读商品分期规则(只读引用),未启用/规则停用则不生成(异常配置,记日志不抛,避免卡死首付回调)
        PtInstallmentRuleEntity rule = ptInstallmentRuleApiDao.selectByProductId(order.getProductId());
        if (rule == null || rule.getIsEnabled() == null || rule.getIsEnabled() != 1
                || rule.getStatus() == null || rule.getStatus() != 1
                || rule.getInstallmentCount() == null || rule.getInstallmentCount() < 1) {
            log.warn("分期规则缺失/未启用,跳过建计划 orderId={},productId={}", order.getId(), order.getProductId());
            return;
        }

        BigDecimal total = scale(order.getPayableAmount());
        BigDecimal down = scale(rule.getDownPaymentAmount());
        int cnt = rule.getInstallmentCount();
        int intervalMonths = rule.getInstallmentIntervalMonths() == null ? 1 : rule.getInstallmentIntervalMonths();
        BigDecimal firstPaid = scale(paidAmount);

        // 1) 建计划(uk_order_id 兜底并发):首付即入账,已付=首付,当前期=2,激活时间=now,进行中
        Date now = new Date();
        PtOrderInstallmentPlanEntity plan = new PtOrderInstallmentPlanEntity();
        plan.setOrderId(order.getId());
        plan.setMemberId(order.getMemberId());
        plan.setProductId(order.getProductId());
        plan.setTotalAmount(total);
        plan.setDownPaymentAmount(down);
        plan.setPaidAmount(firstPaid);
        plan.setUnpaidAmount(scale(total.subtract(firstPaid)));
        plan.setInstallmentCount(cnt);
        plan.setCurrentPeriod(cnt > 1 ? 2 : cnt);
        plan.setStatus(cnt > 1 ? PLAN_RUNNING : PLAN_SETTLED);
        plan.setActivatedAt(now);
        ptOrderInstallmentPlanDao.insertOnDuplicate(plan);
        if (plan.getId() == null) {
            // 并发下 ON DUPLICATE 命中,重取既有计划 id
            PtOrderInstallmentPlanEntity exist = ptOrderInstallmentPlanDao.selectByOrderId(order.getId());
            if (exist == null) {
                throw new RRException("分期计划创建失败 orderId=" + order.getId());
            }
            plan.setId(exist.getId());
        }
        Long planId = plan.getId();
        Date today = truncateToDay(now);

        // 2) 全量建账单(1..cnt),末期吸收尾差保证 Σdue=total;首付期直接落"已支付"(款已随主单到账)
        BigDecimal perRest = cnt > 1
                ? scale((total.subtract(down)).divide(new BigDecimal(cnt - 1), 2, RoundingMode.HALF_UP))
                : BigDecimal.ZERO;
        BigDecimal sumChk = down;
        for (int p = 1; p <= cnt; p++) {
            BigDecimal due;
            Date dueDate;
            if (p == 1) {
                due = down;
                dueDate = today;
            } else if (p == cnt) {
                due = scale(total.subtract(sumChk));
                dueDate = addMonths(today, (p - 1) * intervalMonths);
            } else {
                due = perRest;
                dueDate = addMonths(today, (p - 1) * intervalMonths);
                sumChk = sumChk.add(due);
            }
            PtOrderInstallmentBillEntity bill = new PtOrderInstallmentBillEntity();
            bill.setPlanId(planId);
            bill.setPeriodNo(p);
            bill.setDueAmount(due);
            bill.setDueDate(dueDate);
            if (p == 1) {
                // 首付期:款已到,直接已支付;pay_order_no=主单号(后缀 b)留痕,免与后续期"a"单撞唯一键
                bill.setPaidAmount(firstPaid);
                bill.setPaidTime(now);
                bill.setStatus(BILL_PAID);
                bill.setPayOrderNo(order.getOrderNo());
            } else {
                bill.setPaidAmount(BigDecimal.ZERO);
                bill.setStatus(BILL_UNPAID);
                bill.setPayOrderNo(null);
            }
            ptOrderInstallmentBillDao.insertOnDuplicate(bill);
        }

        // 3) 订单转"首付已付/部分支付"(order_status=1/pay_status=1);单期(cnt=1)理论等同结清,
        //    但下单侧不产 cnt=1 的分期,这里仍按首付口径推进,由后续无待付账单自然结清
        int rows = ptPrivateOrderDao.markInstallmentPartPaid(order.getId(), firstPaid);
        if (rows == 0) {
            log.info("分期首付订单状态推进0行(可能已推进),orderId={}", order.getId());
        }

        // 4) 首付激活权益(一次性激活全部课时,课时/有效期取订单快照);activate 内按 order_id 查重幂等
        memberPrivateBenefitService.activate(order.getId(), order.getMemberId(), order.getProductId(),
                order.getStoreId(), order.getLessonCount(), order.getValidityDays());

        log.info("分期计划建立并首付激活 orderId={},planId={},total={},down={},cnt={}",
                order.getId(), planId, total, down, cnt);
    }

    /* ==================== 5.3 首付独立付款回调(后缀 9,防御性) ==================== */

    @Override
    public void installmentDownCallback(String downOrderNo, BigDecimal amount, String transactionId, Integer payType) {
        if (isBlank(downOrderNo) || amount == null) {
            log.warn("分期首付回调:参数缺失 orderNo={}", downOrderNo);
            return;
        }
        // 行锁定位首付账单
        PtOrderInstallmentBillEntity bill = ptOrderInstallmentBillDao.selectByPayOrderNoForUpdate(downOrderNo);
        if (bill == null) {
            log.warn("分期首付回调:未找到账单,跳过 orderNo={}", downOrderNo);
            return;
        }
        if (bill.getStatus() != null && bill.getStatus() == BILL_PAID) {
            log.info("分期首付回调:已入账,幂等跳过 orderNo={}", downOrderNo);
            return;
        }
        PtOrderInstallmentPlanEntity plan = ptOrderInstallmentPlanDao.selectByIdForUpdate(bill.getPlanId());
        if (plan == null) {
            log.warn("分期首付回调:计划不存在 planId={}", bill.getPlanId());
            return;
        }
        // 行锁订单(按计划冗余的 order_id 直接锁行)
        PtPrivateOrderEntity order = plan.getOrderId() == null ? null
                : ptPrivateOrderDao.selectByIdForUpdate(plan.getOrderId());
        // 记账(真实收款)
        incomePayDetailService.saveIncomePayDetail(downOrderNo, transactionId, scale(amount), payType);
        // 首付账单入账(带 status IN(0,2) 条件幂等)
        int paidRows = ptOrderInstallmentBillDao.markPaid(bill.getId(), scale(amount), new Date());
        if (paidRows == 0) {
            log.info("分期首付回调:账单入账0行(并发已处理),orderNo={}", downOrderNo);
            return;
        }
        // 计划推进:已付+=首付,未付重算,当前期=2,激活时间=now,进行中
        BigDecimal newPaid = scale(nz(plan.getPaidAmount()).add(scale(amount)));
        BigDecimal newUnpaid = scale(nz(plan.getTotalAmount()).subtract(newPaid));
        int nextPeriod = plan.getInstallmentCount() != null && plan.getInstallmentCount() > 1 ? 2 : plan.getInstallmentCount();
        ptOrderInstallmentPlanDao.updateProgress(plan.getId(), newPaid, newUnpaid, nextPeriod, PLAN_RUNNING, new Date());
        // 订单转部分支付 + 激活权益
        if (order != null) {
            ptPrivateOrderDao.markInstallmentPartPaid(order.getId(), scale(amount));
            memberPrivateBenefitService.activate(order.getId(), order.getMemberId(), order.getProductId(),
                    order.getStoreId(), order.getLessonCount(), order.getValidityDays());
        }
        log.info("分期首付独立回调入账并激活 orderNo={},planId={}", downOrderNo, plan.getId());
    }

    /* ==================== 5.4 后续期逐期入账 + 结清(后缀 a) ==================== */

    @Override
    public void installmentBillCallback(String payOrderNo, BigDecimal amount, String transactionId, Integer payType) {
        if (isBlank(payOrderNo) || amount == null) {
            log.warn("分期后续期回调:参数缺失 orderNo={}", payOrderNo);
            return;
        }
        PtOrderInstallmentBillEntity bill = ptOrderInstallmentBillDao.selectByPayOrderNoForUpdate(payOrderNo);
        if (bill == null) {
            log.warn("分期后续期回调:未找到账单,跳过 orderNo={}", payOrderNo);
            return;
        }
        if (bill.getStatus() != null && bill.getStatus() == BILL_PAID) {
            log.info("分期后续期回调:已入账,幂等跳过 orderNo={}", payOrderNo);
            return;
        }
        PtOrderInstallmentPlanEntity plan = ptOrderInstallmentPlanDao.selectByIdForUpdate(bill.getPlanId());
        if (plan == null) {
            log.warn("分期后续期回调:计划不存在 planId={}", bill.getPlanId());
            return;
        }
        // 记账(真实收款)
        incomePayDetailService.saveIncomePayDetail(payOrderNo, transactionId, scale(amount), payType);
        // 账单入账(逾期 2→已支付 1 也允许;带 status IN(0,2) 条件幂等)
        int paidRows = ptOrderInstallmentBillDao.markPaid(bill.getId(), scale(amount), new Date());
        if (paidRows == 0) {
            log.info("分期后续期回调:账单入账0行(并发已处理),orderNo={}", payOrderNo);
            return;
        }
        // 计划状态机
        BigDecimal newPaid = scale(nz(plan.getPaidAmount()).add(scale(amount)));
        BigDecimal newUnpaid = scale(nz(plan.getTotalAmount()).subtract(newPaid));
        if (newUnpaid.compareTo(BigDecimal.ZERO) <= 0) {
            // 全部付清 → 计划已结清、当前期=总期、订单结清
            ptOrderInstallmentPlanDao.updateProgress(plan.getId(), newPaid, BigDecimal.ZERO.setScale(2),
                    plan.getInstallmentCount(), PLAN_SETTLED, null);
            Long orderId = plan.getOrderId();
            if (orderId != null) {
                ptPrivateOrderDao.markInstallmentSettled(orderId, newPaid);
            }
            log.info("分期全部付清结清 planId={},orderNo={}", plan.getId(), payOrderNo);
        } else {
            // 未付清:当前期+1;若该计划已无逾期账单,则从"已逾期"回正"进行中"(恢复约课见暂停桩)
            int nextPeriod = nz(plan.getCurrentPeriod()).intValue() + 1;
            int overdueLeft = ptOrderInstallmentBillDao.countOverdueByPlan(plan.getId());
            int newStatus = overdueLeft == 0 ? PLAN_RUNNING : plan.getStatus();
            ptOrderInstallmentPlanDao.updateProgress(plan.getId(), newPaid, newUnpaid, nextPeriod, newStatus, null);
            if (overdueLeft == 0) {
                // 无逾期账单 → 恢复该会员约课(交易域权益/会员维度)
                resumeBookingForPlan(plan);
            }
            log.info("分期后续期入账 planId={},orderNo={},nextPeriod={},overdueLeft={}",
                    plan.getId(), payOrderNo, nextPeriod, overdueLeft);
        }
    }

    /* ==================== 4.4 会员付某期 ==================== */

    @Override
    public Map<String, Object> payBill(Long userId, Long billId, HttpServletRequest request) {
        if (userId == null || billId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 行锁账单:并发/重复发起串行化
        PtOrderInstallmentBillEntity bill = ptOrderInstallmentBillDao.selectByIdForUpdate(billId);
        if (bill == null) {
            throw new RRException(CodeAndMsg.ERROR_INSTALLMENT_BILL_NOT_EXIST);
        }
        // 计划归属校验:按 plan_id 取计划,校验属本人(计划表冗余 member_id)
        PtOrderInstallmentPlanEntity thePlan = selectPlanById(bill.getPlanId());
        if (thePlan == null || thePlan.getMemberId() == null || !thePlan.getMemberId().equals(userId)) {
            // 越权按不存在处理,不暴露他人账单
            throw new RRException(CodeAndMsg.ERROR_INSTALLMENT_BILL_NOT_EXIST);
        }
        // 已支付/已关闭不可付
        if (bill.getStatus() != null && bill.getStatus() == BILL_PAID) {
            throw new RRException(CodeAndMsg.ERROR_INSTALLMENT_BILL_PAID);
        }
        if (bill.getStatus() == null || (bill.getStatus() != BILL_UNPAID && bill.getStatus() != BILL_OVERDUE)) {
            throw new RRException(CodeAndMsg.ERROR_INSTALLMENT_BILL_NOT_EXIST);
        }
        // 必须为"当前应付期"(按序付):bill.period_no == plan.current_period
        if (thePlan.getCurrentPeriod() == null || !thePlan.getCurrentPeriod().equals(bill.getPeriodNo())) {
            throw new RRException(CodeAndMsg.ERROR_INSTALLMENT_NOT_CURRENT_PERIOD);
        }

        // 生成本期支付单号(末位拼分期后续期后缀 a),写入 bill.pay_order_no(回调按 pay_order_no 反查入账)
        String payOrderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.INSTALLMENT_BILL_TYPE;
        ptOrderInstallmentBillDao.updatePayOrderNo(bill.getId(), payOrderNo);

        BigDecimal due = scale(bill.getDueAmount());
        try {
            // 复用现有微信统一下单(充值通道),notifyType=null 走 NOTIFY_WX_URL(/api/pay/wxNotify),后缀"a"分发到本 service
            SortedMap<String, String> payParams = payService.wxRechargePay(due, payOrderNo, null);
            if (payParams == null) {
                throw new RRException(CodeAndMsg.ERROR_PAY_ERROR);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("payOrderNo", payOrderNo);
            result.put("periodNo", bill.getPeriodNo());
            result.put("dueAmount", due);
            result.put("payParams", payParams);
            return result;
        } catch (RRException e) {
            throw e;
        } catch (Exception e) {
            log.error("分期付某期下单失败 billId={},payOrderNo={}", billId, payOrderNo, e);
            throw new RRException(CodeAndMsg.ERROR_PAY_ERROR);
        }
    }

    /* ==================== 会员端查询 ==================== */

    @Override
    public PageUtils myList(Long userId, Integer page, Integer limit) {
        if (userId == null) {
            throw new RRException(CodeAndMsg.ERROR_USER_NOT_LOGIN);
        }
        int p = page == null || page < 1 ? 1 : page;
        int l = limit == null || limit < 1 ? 10 : limit;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", userId);
        params.put("offset", (p - 1) * l);
        params.put("limit", l);
        List<PtOrderInstallmentPlanEntity> list = ptOrderInstallmentPlanDao.queryMyPlans(params);
        int total = ptOrderInstallmentPlanDao.countMyPlans(params);
        if (list == null) {
            list = new ArrayList<PtOrderInstallmentPlanEntity>();
        }
        return new PageUtils(list, total, l, p);
    }

    @Override
    public Map<String, Object> detail(Long userId, Long planId) {
        if (userId == null) {
            throw new RRException(CodeAndMsg.ERROR_USER_NOT_LOGIN);
        }
        PtOrderInstallmentPlanEntity plan = selectPlanById(planId);
        if (plan == null || plan.getMemberId() == null || !plan.getMemberId().equals(userId)) {
            // 越权按不存在处理
            throw new RRException(CodeAndMsg.ERROR_INSTALLMENT_PLAN_NOT_EXIST);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("plan", plan);
        result.put("bills", ptOrderInstallmentBillDao.selectByPlanId(planId));
        return result;
    }

    /* ==================== 交易域对接:逾期暂停/付清恢复约课(桩) ==================== */

    /**
     * 恢复该计划会员的约课(付清逾期后)。落点在交易域权益/会员维度的可约状态字段,
     * 第14步交易域暂无对应"暂停/恢复约课"开关,本步留桩标注,待交易域暴露方法后接线,
     * 切勿在本域私自实现约课拦截以免与交易域双写冲突(详见详细文档§9.7)。
     */
    private void resumeBookingForPlan(PtOrderInstallmentPlanEntity plan) {
        // TODO(第20步/待交易域) 调交易域"恢复约课"方法(按会员/权益维度)。当前交易域无暂停开关,留桩。
        log.info("分期计划无逾期账单,应恢复约课(交易域暂无开关,留桩) planId={},memberId={}",
                plan.getId(), plan.getMemberId());
    }

    /* ==================== 内部工具 ==================== */

    /**
     * 按主键取计划(payBill/detail 归属校验用)。计划行在 payBill 事务里做前置校验,
     * 复用 selectByIdForUpdate 上行锁串行化同计划并发发起(事务提交即释放),读值一致且天然防并发。
     */
    private PtOrderInstallmentPlanEntity selectPlanById(Long planId) {
        if (planId == null) {
            return null;
        }
        return ptOrderInstallmentPlanDao.selectByIdForUpdate(planId);
    }

    private BigDecimal scale(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Integer nz(Integer v) {
        return v == null ? Integer.valueOf(0) : v;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** 截断到日(00:00:00),账单 due_date 为 DATE 类型 */
    private Date truncateToDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /** 应付日期按间隔月数顺延 */
    private Date addMonths(Date base, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }
}
