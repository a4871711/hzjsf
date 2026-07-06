package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.PtPrivateOrderEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 私教订单分期账单执行 Service(第20步,M4 资金域)。
 * <p>只做资金侧的"分期计划/账单生成、首付激活、逐期入账结清";不重复造支付(下单/回调复用现有体系),
 * 不接管交易域订单状态机口径(以交易域为准,本域仅按约定推进 pt_private_order 状态字段)。
 * 落在 api.service.impl,命中 spring-jdbc.xml 事务切面(REQUIRED),单次方法调用即一个事务边界。</p>
 * <p>幂等一律"带状态/金额条件的 UPDATE + 受影响行数判定";并发临界资源(计划行/账单行/订单行)一律
 * 先 SELECT ... FOR UPDATE 上行锁再改。api 目录 Mapper XML 改动须重启 Tomcat。</p>
 *
 * @author claude
 */
public interface PtInstallmentService {

    /**
     * 下单时(首付到账后)生成分期计划 + 全量账单,并首付激活权益。
     * <p>由 {@code PrivateOrderServiceImpl.updatePrivateOrder} 的 pay_method=4 分支在同一事务内调用:
     * 此刻首付款已随主单(后缀 b)到账。本方法:建 plan(uk_order_id 幂等)+ 全量 bill(1..count,
     * 末期吸收尾差保证 Σ=total);首付期(period_no=1)直接落"已支付"(款已到);plan.status=进行中、
     * current_period=2、activated_at=now;订单转"首付已付/部分支付";调 {@code activate} 一次性激活全部课时。</p>
     *
     * @param order      已支付首付的私教订单(pay_method=4)
     * @param paidAmount 首付实收金额(主单回调实收,记入首付账单与已付总额)
     */
    void createInstallmentPlan(PtPrivateOrderEntity order, BigDecimal paidAmount);

    /**
     * 分期首付独立付款回调(订单号后缀 9)。防御性接线:仅当首付走独立"9"单时触发。
     * <p>照详细文档§5.3:按 pay_order_no 行锁定位首付账单→记账→首付账单入账→计划推进(activated_at=now、
     * current_period=2)→订单转"部分支付"→激活全部课时。幂等:账单已支付直接返回。</p>
     */
    void installmentDownCallback(String downOrderNo, BigDecimal amount, String transactionId, Integer payType);

    /**
     * 分期后续期付款逐期入账 + 结清回调(订单号后缀 a)。
     * <p>照详细文档§5.4:按 pay_order_no 行锁定位账单→记账→账单入账(status IN(0,2)→1)→计划推进;
     * 付清则 plan 已结清、订单结清;未付清 current_period+1,若已无逾期账单则从"已逾期"回正"进行中"。</p>
     */
    void installmentBillCallback(String payOrderNo, BigDecimal amount, String transactionId, Integer payType);

    /**
     * 会员发起支付某一期账单(生成后续期微信单,后缀 a)。
     * <p>校验账单属本人、status 为 0待支付/2已逾期、且为"当前应付期";生成本期支付单号写入 bill.pay_order_no,
     * 调微信统一下单返回调起参数;支付成功回调走 {@link #installmentBillCallback}。</p>
     *
     * @return 微信调起参数 + 本期账单信息
     */
    Map<String, Object> payBill(Long userId, Long billId, HttpServletRequest request);

    /**
     * 会员端我的分期计划分页(含各期账单概况入口)。
     */
    com.dlc.common.utils.PageUtils myList(Long userId, Integer page, Integer limit);

    /**
     * 会员端分期计划详情:计划汇总 + 全部账单期明细。越权(非本人)按不存在处理。
     */
    Map<String, Object> detail(Long userId, Long planId);
}
