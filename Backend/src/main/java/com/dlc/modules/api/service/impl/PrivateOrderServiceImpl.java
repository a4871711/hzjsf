package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.dao.PtMemberPrivateBenefitDao;
import com.dlc.modules.api.dao.PtInstallmentRuleApiDao;
import com.dlc.modules.api.dao.PtPrivateOrderCouponRelDao;
import com.dlc.modules.api.dao.PtPrivateOrderDao;
import com.dlc.modules.api.entity.PtInstallmentRuleEntity;
import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.entity.PtPrivateOrderCouponRelEntity;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.service.GroupToPrivateScanService;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.MemberPrivateBenefitService;
import com.dlc.modules.api.service.MkCouponService;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.PrivateOrderService;
import com.dlc.modules.api.service.PtInstallmentService;
import com.dlc.modules.api.service.PtMemberWalletService;
import com.dlc.modules.api.service.WxPayService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import com.dlc.modules.sys.entity.SysCoachTradeDetailEntity;
import com.dlc.modules.sys.dao.PtCoachMonthlyCommissionRuleDao;
import com.dlc.modules.sys.service.SysCoachFeeRuleService;
import com.dlc.modules.sys.service.SysCoachTradeDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 私教订单 Service 实现。
 * 落在 api.service.impl,命中 spring-jdbc.xml 事务切面(REQUIRED),create 整体一个事务。
 * quote 与 create 共用同一套校验+定价方法(checkAndPrice),保证"试算所见即下单所付"。
 */
@Service("privateOrderService")
public class PrivateOrderServiceImpl implements PrivateOrderService {

    /** 营销类型:普通购买 */
    private static final int MARKETING_NONE = 0;
    /** 营销类型:拼团 */
    private static final int MARKETING_GROUP_BUY = 1;
    /** 营销类型:限时秒杀 */
    private static final int MARKETING_FLASH_SALE = 2;
    /** 微信 V2 规定统一下单后至少等待5分钟才能关单。 */
    private static final long WECHAT_CLOSE_MIN_INTERVAL_MILLIS = 5L * 60L * 1000L;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private PtInstallmentRuleApiDao ptInstallmentRuleApiDao;
    @Autowired
    private PtPrivateOrderCouponRelDao ptPrivateOrderCouponRelDao;
    @Autowired
    private PtMemberPrivateBenefitDao ptMemberPrivateBenefitDao;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private PayService payService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private MemberPrivateBenefitService memberPrivateBenefitService;
    @Autowired
    private PtMemberWalletService ptMemberWalletService;
    @Autowired
    private MkCouponService mkCouponService;
    @Autowired
    private PtInstallmentService ptInstallmentService;
    @Autowired
    private GroupToPrivateScanService groupToPrivateScanService;
    @Autowired
    private SysCoachFeeRuleService sysCoachFeeRuleService;
    @Autowired
    private SysCoachTradeDetailService sysCoachTradeDetailService;
    @Autowired
    private PtCoachMonthlyCommissionRuleDao ptCoachMonthlyCommissionRuleDao;

    @Override
    public Map<String, Object> quote(UserInfoVo user, Long productId, Long storeId,
                                     Long memberCouponId, Integer marketingType, Long marketingActivityId) {
        PriceResult priced = checkAndPrice(user, productId, storeId, memberCouponId, marketingType, marketingActivityId);
        Map<String, Object> result = priced.toMap();
        result.putAll(buildInstallmentOption(priced.product, priced.payableAmount));
        return result;
    }

    @Override
    public Map<String, Object> create(UserInfoVo user, Long productId, Long storeId, Long coachId, Integer payMethod,
                                      Long memberCouponId, Integer marketingType, Long marketingActivityId,
                                      HttpServletRequest request) {
        int selectedPayMethod = payMethod == null ? 1 : payMethod;
        if (selectedPayMethod != 1 && selectedPayMethod != 3 && selectedPayMethod != 4) {
            throw new RRException("不支持的支付方式");
        }
        // 1. 校验 + 金额重算(与 quote 完全同一口径,不信前端金额)
        PriceResult priced = checkAndPrice(user, productId, storeId, memberCouponId, marketingType, marketingActivityId);
        if (priced.payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException("应付金额异常,无法发起支付");
        }
        validateCoachSelection(productId, storeId, coachId);
        Map<String, Object> installmentOption = buildInstallmentOption(priced.product, priced.payableAmount);
        if (selectedPayMethod == 4 && !Boolean.TRUE.equals(installmentOption.get("installmentAvailable"))) {
            throw new RRException("当前商品不支持分期付款");
        }

        // 2. 订单号:PT + yyyyMMddHHmmss+随机 + 末位后缀 b(§0.6.1 仲裁,严禁复用旧私教课的"4")
        PtPrivateOrderEntity order = buildOrder(user, storeId, coachId, selectedPayMethod, priced);
        order.setOrderNo(genOrderNo());
        try {
            ptPrivateOrderDao.save(order);
        } catch (DuplicateKeyException e) {
            // 唯一键仅 order_no:撞键重试 1 次(全局单号约定 §0.6.2)
            order.setOrderNo(genOrderNo());
            ptPrivateOrderDao.save(order);
        }

        // 3. 用券:占用 CAS(0未使用→3使用中)防同券并发下两单,失败整单回滚;再写券明细(order_id 唯一键=一单一券)
        if (priced.memberCoupon != null) {
            int rows = ptPrivateOrderCouponRelDao.occupyMemberCoupon(memberCouponId, user.getUserId(), order.getId());
            if (rows == 0) {
                throw new RRException(CodeAndMsg.ERROR_COUPON_INVALID);
            }
            PtPrivateOrderCouponRelEntity rel = new PtPrivateOrderCouponRelEntity();
            rel.setOrderId(order.getId());
            rel.setCouponId(order.getCouponId());
            rel.setMemberCouponId(memberCouponId);
            rel.setCouponName(order.getCouponName());
            rel.setCouponType(toInt(priced.memberCoupon.get("couponType")));
            rel.setDiscountAmount(priced.couponDiscountAmount);
            ptPrivateOrderCouponRelDao.save(rel);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orderNo", order.getOrderNo());
        result.put("payableAmount", priced.payableAmount);
        result.put("payMethod", selectedPayMethod);

        // 4a. 储值支付不经过第三方收银台；余额扣减与订单结算在同一事务内完成。
        if (selectedPayMethod == 3) {
            updatePrivateOrder(order.getOrderNo(), priced.payableAmount, null, ConfigConstant.BLPAY);
            result.put("paymentAmount", priced.payableAmount);
            result.put("paid", true);
            return result;
        }

        // 4b. 微信支付全款；分期支付仅收商品配置的首付款，回调后生成分期计划。
        BigDecimal paymentAmount = selectedPayMethod == 4
                ? (BigDecimal) installmentOption.get("installmentDownPaymentAmount")
                : priced.payableAmount;
        Map<String, Object> payParams;
        try {
            Map<String, Object> product = new HashMap<String, Object>();
            product.put("orderNo", order.getOrderNo());
            product.put("body", "矢历运动");
            product.put("totalFee", paymentAmount.toPlainString());
            product.put("openId", user.getOpenId());
            payParams = wxPayService.doPay(product, request);
        } catch (Exception e) {
            // doPay 抛受检异常不会触发事务回滚(tx advice 未配 rollback-for),
            // 统一转运行时异常让订单与券占用一并回退,避免留下无法支付的挂单
            throw new RRException(e.getMessage() == null ? "微信统一下单失败" : e.getMessage());
        }

        result.put("paymentAmount", paymentAmount);
        result.put("payParams", payParams);
        result.put("paid", false);
        return result;
    }

    /**
     * 教练可以在购买时暂不指定；如果会员已选择教练，仍必须校验其当前可服务商品和门店。
     */
    void validateCoachSelection(Long productId, Long storeId, Long coachId) {
        if (coachId == null) {
            return;
        }
        if (coachApiDao.countBookableCoach(productId, storeId, coachId) == 0) {
            throw new RRException("所选教练当前无法在该门店服务此课程");
        }
    }

    @Override
    public Map<String, Object> repay(UserInfoVo user, String orderNo, HttpServletRequest request) {
        if (user == null || orderNo == null || orderNo.trim().isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtPrivateOrderEntity order = ptPrivateOrderDao.selectByOrderNo(orderNo.trim());
        if (order == null || !user.getUserId().equals(order.getMemberId())) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_NOT_EXIST);
        }
        if (!Integer.valueOf(0).equals(order.getOrderStatus())) {
            throw new RRException("仅待支付订单可继续支付");
        }
        int payMethod = order.getPayMethod() == null ? 1 : order.getPayMethod();
        if (payMethod != 1 && payMethod != 4) {
            throw new RRException("该支付方式不支持继续支付");
        }

        // 回调可能延迟或丢失：重新拉起收银台前先主动查单，微信已支付则直接补做本地幂等结算。
        Map<String, Object> paymentState = confirmWechatPay(user.getUserId(), order.getOrderNo());
        if (Boolean.TRUE.equals(paymentState.get("paid"))) {
            Map<String, Object> paidResult = new HashMap<String, Object>();
            paidResult.put("orderNo", order.getOrderNo());
            paidResult.put("payableAmount", order.getPayableAmount());
            paidResult.put("paid", true);
            return paidResult;
        }

        BigDecimal paymentAmount = order.getPayableAmount();
        if (payMethod == 4) {
            Map<String, Object> option = buildInstallmentOptionForOrder(order);
            if (!Boolean.TRUE.equals(option.get("installmentAvailable"))) {
                throw new RRException("该订单的分期配置已失效，请联系客服");
            }
            paymentAmount = (BigDecimal) option.get("installmentDownPaymentAmount");
        }
        Map<String, Object> product = new HashMap<String, Object>();
        product.put("orderNo", order.getOrderNo());
        product.put("body", "矢历运动");
        product.put("totalFee", paymentAmount.toPlainString());
        product.put("openId", user.getOpenId());
        Map<String, Object> payParams;
        try {
            payParams = wxPayService.doPay(product, request);
        } catch (Exception e) {
            throw new RRException(e.getMessage() == null ? "微信统一下单失败" : e.getMessage());
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orderNo", order.getOrderNo());
        result.put("payableAmount", order.getPayableAmount());
        result.put("paymentAmount", paymentAmount);
        result.put("payMethod", payMethod);
        result.put("payParams", payParams);
        result.put("paid", false);
        return result;
    }

    @Override
    public boolean cancelUnpaid(Long userId, String orderNo) {
        if (userId == null || orderNo == null || orderNo.trim().isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        String normalizedOrderNo = orderNo.trim();
        PtPrivateOrderEntity snapshot = ptPrivateOrderDao.selectByOrderNo(normalizedOrderNo);
        if (snapshot == null || !userId.equals(snapshot.getMemberId())) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_NOT_EXIST);
        }
        if (Integer.valueOf(3).equals(snapshot.getOrderStatus())) {
            return true;
        }
        if (!Integer.valueOf(0).equals(snapshot.getOrderStatus())) {
            throw new RRException("仅待支付订单可取消");
        }

        // 先查微信真实状态。若已支付则补结算并拒绝取消；若未支付则必须先关微信订单，杜绝关单后的继续扣款。
        Map<String, String> queryResult = wxPayService.queryPayOrder(normalizedOrderNo);
        if ("SUCCESS".equals(queryResult.get("result_code"))) {
            String tradeState = queryResult.get("trade_state");
            if ("SUCCESS".equals(tradeState)) {
                settleWechatPayment(queryResult);
                return false;
            }
            if ("USERPAYING".equals(tradeState)) {
                throw new RRException("微信正在处理支付，请稍后刷新订单再取消");
            }
            if (!"CLOSED".equals(tradeState) && !"REVOKED".equals(tradeState)
                    && !"REFUND".equals(tradeState)) {
                if (snapshot.getCreatedAt() != null
                        && System.currentTimeMillis() - snapshot.getCreatedAt().getTime()
                        < WECHAT_CLOSE_MIN_INTERVAL_MILLIS) {
                    throw new RRException("订单创建未满5分钟，微信暂不允许关单，请稍后再取消");
                }
                Map<String, String> closeResult = wxPayService.closePayOrder(normalizedOrderNo);
                if (!"SUCCESS".equals(closeResult.get("result_code"))) {
                    String errCode = closeResult.get("err_code");
                    if ("ORDERPAID".equals(errCode)) {
                        Map<String, String> paidResult = wxPayService.queryPayOrder(normalizedOrderNo);
                        if ("SUCCESS".equals(paidResult.get("result_code"))
                                && "SUCCESS".equals(paidResult.get("trade_state"))) {
                            settleWechatPayment(paidResult);
                            return false;
                        }
                        throw new RRException("微信支付结果确认中，请稍后刷新订单");
                    }
                    if (!"ORDERCLOSED".equals(errCode)) {
                        throw new RRException("微信订单关闭失败" + errorCodeSuffix(errCode));
                    }
                }
            }
        } else if (!"ORDERNOTEXIST".equals(queryResult.get("err_code"))) {
            throw new RRException("微信支付状态查询失败" + errorCodeSuffix(queryResult.get("err_code")));
        }

        // 微信已确认未支付且已关闭，再锁本地订单并取消；与异步回调持有同一订单行锁。
        PtPrivateOrderEntity order = ptPrivateOrderDao.selectByOrderNoForUpdate(normalizedOrderNo);
        if (order == null || !userId.equals(order.getMemberId())) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_NOT_EXIST);
        }
        if (Integer.valueOf(3).equals(order.getOrderStatus())) {
            return true;
        }
        if (!Integer.valueOf(0).equals(order.getOrderStatus())) {
            throw new RRException("仅待支付订单可取消");
        }
        if (ptPrivateOrderDao.cancelTimeoutOrder(order.getId()) == 0) {
            throw new RRException("订单状态已变更，请刷新后重试");
        }
        if (order.getMemberCouponId() != null) {
            // 券释放自带 used_order_id 条件，只会释放本单占用的券。
            ptPrivateOrderCouponRelDao.releaseMemberCoupon(order.getMemberCouponId(), order.getId());
        }
        return true;
    }

    @Override
    public Map<String, Object> confirmWechatPay(Long userId, String orderNo) {
        if (userId == null || orderNo == null || orderNo.trim().isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        String normalizedOrderNo = orderNo.trim();
        PtPrivateOrderEntity order = ptPrivateOrderDao.selectByOrderNo(normalizedOrderNo);
        if (order == null || !userId.equals(order.getMemberId())) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_NOT_EXIST);
        }
        if (!Integer.valueOf(0).equals(order.getOrderStatus())) {
            return buildPaymentState(order, null);
        }

        Map<String, String> queryResult = wxPayService.queryPayOrder(normalizedOrderNo);
        if (!"SUCCESS".equals(queryResult.get("result_code"))) {
            String errCode = queryResult.get("err_code");
            if ("ORDERNOTEXIST".equals(errCode)) {
                return buildPaymentState(order, "NOTPAY");
            }
            throw new RRException("微信支付状态查询失败" + errorCodeSuffix(errCode));
        }

        String tradeState = queryResult.get("trade_state");
        if ("SUCCESS".equals(tradeState)) {
            settleWechatPayment(queryResult);
            PtPrivateOrderEntity refreshed = ptPrivateOrderDao.selectByOrderNo(normalizedOrderNo);
            if (refreshed == null || !isPaid(refreshed)) {
                throw new RRException("微信已支付，但本地订单确认失败，请联系客服");
            }
            return buildPaymentState(refreshed, tradeState);
        }
        return buildPaymentState(order, tradeState);
    }

    @Override
    public PageUtils myOrders(Map<String, Object> params) {
        Query query = new Query(params);
        List<PtPrivateOrderEntity> list = ptPrivateOrderDao.queryMyOrders(query);
        int total = ptPrivateOrderDao.countMyOrders(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public PageUtils myBenefits(Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> list = ptMemberPrivateBenefitDao.queryMyBenefits(query);
        int total = ptMemberPrivateBenefitDao.countMyBenefits(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public Map<String, Object> detail(Long userId, String orderNo) {
        PtPrivateOrderEntity order = ptPrivateOrderDao.selectByOrderNo(orderNo);
        // 越权(非本人)按不存在处理,不向外暴露他人订单号是否存在
        if (order == null || !order.getMemberId().equals(userId)) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_NOT_EXIST);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("order", order);
        result.put("couponRel", ptPrivateOrderCouponRelDao.selectByOrderId(order.getId()));
        return result;
    }

    /* ==================== 第13步 支付成功回调(单事务,幂等三道闸) ==================== */

    @Override
    public int updatePrivateOrder(String orderNo, BigDecimal wallet, String transactionId, Integer payType) {
        // 锁订单行:对同一单的并发/重复回调串行化(闸2 activate 的 order_id 查重依赖本锁)
        PtPrivateOrderEntity order = ptPrivateOrderDao.selectByOrderNoForUpdate(orderNo);
        if (order == null) {
            // 异常单号:照 VIP activateByOrderNo 口径幂等返回不报错,回调链正常应答避免微信无限重试
            log.warn("私教订单回调:单号不存在,跳过 orderNo={}", orderNo);
            return 0;
        }
        // 幂等闸1:仅 order_status=0 待支付才处理;重复回调直接返回,不再记账/扣库存/建权益
        if (order.getOrderStatus() == null || order.getOrderStatus() != 0) {
            log.info("私教订单回调:已处理过,幂等跳过 orderNo={},orderStatus={}", orderNo, order.getOrderStatus());
            return 0;
        }

        int payMethod = order.getPayMethod() == null ? 1 : order.getPayMethod();
        if (payMethod == 3) {
            // 储值支付(第19步接线):同事务先扣余额+写消费流水(资金侧),再 fall through 复用下方一次性结算
            // (扣库存/券核销/结清/建权益),不写第二套结算口径。余额不足由 payByWallet 抛 -102 整单回滚。
            // 储值不下微信单,故无 transaction_id;金额以订单应付快照为准,收支方式记 BLPAY(余额支付)。
            ptMemberWalletService.payByWallet(orderNo, order.getId(), order.getMemberId(), order.getPayableAmount());
            wallet = order.getPayableAmount();
            transactionId = null;
            payType = ConfigConstant.BLPAY;
            // 不 return:继续走下方一次性结算块(记账后缀b→用途14、扣库存、券核销、settleOrder、建权益)
        } else if (payMethod == 4) {
            // 分期支付(第20步接线):首付款已随主单(后缀b)到账,此刻在同事务内生成分期计划+全量账单,
            // 首付期直接入账、首付激活全部课时,订单转"首付已付(order_status=1)/部分支付(pay_status=1)"。
            // 记账放在闸1之后同事务(重复回调闸1已挡);后续期由 payBill→installmentBillCallback(后缀a)逐期入账。
            // 不 fall through 一次性结算块(避免误把分期单结清 order_status=2 并二次激活)。
            if (!ConfigConstant.WXPAY.equals(payType) || transactionId == null || transactionId.trim().isEmpty()
                    || wallet == null) {
                throw new RRException("私教分期首付渠道参数异常,orderNo=" + orderNo);
            }
            BigDecimal downPaid = wallet;
            incomePayDetailService.saveIncomePayDetail(orderNo, transactionId, scale(downPaid), payType);
            ptInstallmentService.createInstallmentPlan(order, downPaid);
            wallet = scale(downPaid);
        }

        // ---- 一次性支付(本期 create 只产 pay_method=1 微信) ----
        if (payMethod == 1) {
            if (!ConfigConstant.WXPAY.equals(payType) || transactionId == null || transactionId.trim().isEmpty()) {
                throw new RRException("私教订单微信支付渠道参数异常,orderNo=" + orderNo);
            }
            if (wallet == null || order.getPayableAmount() == null
                    || scale(wallet).compareTo(scale(order.getPayableAmount())) != 0) {
                // 金额不一致绝不能发权益或记账；抛异常让微信重试并保留人工核查入口。
                log.error("私教订单微信实收与应付不一致 orderNo={},actual={},expected={}",
                        orderNo, wallet, order.getPayableAmount());
                throw new RRException("微信支付金额与订单应付金额不一致,orderNo=" + orderNo);
            }
            wallet = scale(wallet);
        }
        // 记账:放在闸1之后同事务,重复回调不会重复写流水(回调链对后缀b跳过统一记账,照VIP后缀6/7现状)
        if (payMethod != 4) {
            incomePayDetailService.saveIncomePayDetail(orderNo, transactionId, wallet, payType);
        }

        // 幂等闸3:扣库存累计已售,条件 UPDATE 终态护栏;影响0行=售罄,抛异常整体回滚(极少见,人工退款处理)
        if (ptPrivateOrderDao.increaseProductSoldCount(order.getProductId()) == 0) {
            throw new RRException("私教商品已售罄,回调扣库存失败,orderNo=" + orderNo);
        }
        // 活动单:同口径 CAS 扣活动表已售(拼团 activity_stock 可为 NULL 不限量)
        if (order.getMarketingType() != null && order.getMarketingActivityId() != null) {
            if (order.getMarketingType() == MARKETING_GROUP_BUY
                    && ptPrivateOrderDao.increaseGroupBuySoldCount(order.getMarketingActivityId()) == 0) {
                throw new RRException("拼团活动已售罄,回调扣活动库存失败,orderNo=" + orderNo);
            }
            if (order.getMarketingType() == MARKETING_FLASH_SALE
                    && ptPrivateOrderDao.increaseFlashSaleSoldCount(order.getMarketingActivityId()) == 0) {
                throw new RRException("秒杀活动已售罄,回调扣活动库存失败,orderNo=" + orderNo);
            }
        }

        // 券核销:占用态 3使用中→1已使用+used_time,条件 CAS 校验占用单=本单;
        // 命中0行=占用已被超时取消任务(第16步)释放等竞态,记日志不阻断收款主流程
        if (order.getMemberCouponId() != null) {
            int used = ptPrivateOrderCouponRelDao.useMemberCoupon(order.getMemberCouponId(), order.getId());
            if (used == 0) {
                log.warn("私教订单回调:券核销未命中(占用可能已被释放) memberCouponId={},orderNo={}",
                        order.getMemberCouponId(), orderNo);
            }
        }

        if (payMethod != 4) {
            // 一次性支付推进到已结清；分期订单已由 createInstallmentPlan 推进到首付已付/部分支付。
            if (ptPrivateOrderDao.settleOrder(order.getId(), scale(wallet)) == 0) {
                throw new RRException("私教订单状态推进失败,orderNo=" + orderNo);
            }

            // 整单提成由规则类型决定：一次性支付成功后按订单实收金额结算给订单归属教练。
            settleWholeOrderCommission(order, scale(wallet));

            // 分期首付的权益已在 createInstallmentPlan 内激活；一次性支付在此激活。
            memberPrivateBenefitService.activate(order.getId(), order.getMemberId(), order.getProductId(),
                    order.getStoreId(), order.getLessonCount(), order.getValidityDays());
        }

        // 附赠团课权益发放(商品配置 pt_product_group_benefit 启用时,一单只发一次)
        grantGroupBenefit(order);

        // 第22步 团课转私教自动转化:会员购买私教后,若其在高意向名单(follow_status<2)则标记已转化+追加自动跟进流水。
        // try/catch 包裹不回滚主支付流程(转化标记是运营辅助,失败仅记日志,幂等由 autoMarkConverted 条件UPDATE兜底)。
        try {
            groupToPrivateScanService.autoConvertOnPaid(order);
        } catch (Exception e) {
            log.error("私教订单回调:团课转私教自动转化失败(不影响主流程) orderNo={}", orderNo, e);
        }
        return 1;
    }

    /* ==================== 第15步 后台退款冲减(单事务) ==================== */

    @Override
    public void refund(Long orderId, BigDecimal refundAmount, Integer refundLessons, String remark, Long operatorId) {
        if (orderId == null || refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0
                || (refundLessons != null && refundLessons < 0)) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        BigDecimal amount = scale(refundAmount);

        // 1. 行锁串行化并发/重复退款提交:锁内做"金额上限"校验,重复提交第二次必然超上限被拒,天然幂等
        PtPrivateOrderEntity order = ptPrivateOrderDao.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_NOT_EXIST);
        }
        if (order.getOrderStatus() == null || (order.getOrderStatus() != 1 && order.getOrderStatus() != 2)) {
            // 仅 1首付已付 / 2已结清 可退;待支付/已取消/已退款一律拒绝
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_STATUS);
        }
        // 2. 渠道支持前置判断(失败要趁早,不做任何落库动作)
        int payMethod = order.getPayMethod() == null ? 1 : order.getPayMethod();
        if (payMethod == 4) {
            // 【桩·第20步评估后一期留桩】分期退款复杂(需关闭未到期账单、按已收净额分渠道原路退、
            // 计划置已关闭、权益冲减),按详细文档§9一期留桩不接;后续单独接 installmentService.refund。
            throw new RRException("分期支付退款通道未接入,暂不支持退款");
        }
        if (payMethod != 1 && payMethod != 3) {
            // 支持:1微信(原路微信退)、3储值(原路回储值余额,第19步接线);支付宝(2)/其他(9)通道接入后在此扩展
            throw new RRException("该支付方式退款通道未接入,暂不支持退款");
        }
        BigDecimal paid = scale(order.getPaidAmount());
        BigDecimal refunded = scale(order.getRefundAmount());
        BigDecimal refundable = paid.subtract(refunded);
        if (amount.compareTo(refundable) > 0) {
            throw new RRException("退款金额超出可退上限" + refundable.toPlainString() + "元");
        }

        // 3. 冲减权益课时:缺省=剩余课时全冲;refundDeduct 只冲 remaining(冻结中/已用课时不可冲,恒等式护栏在账本内)
        PtMemberPrivateBenefitEntity benefit = ptMemberPrivateBenefitDao.selectByOrderId(orderId);
        int lessons = refundLessons != null ? refundLessons
                : (benefit == null ? 0 : nvl(benefit.getRemainingLessons()));
        if (lessons > 0) {
            if (benefit == null) {
                throw new RRException(CodeAndMsg.ERROR_BENEFIT_NOT_EXIST);
            }
            memberPrivateBenefitService.refundDeduct(benefit.getId(), lessons);
        }

        // 4. 订单落退款:CAS(order_status IN (1,2) AND refund_amount=旧值);全额退→4已退款/pay_status=3
        BigDecimal newRefunded = scale(refunded.add(amount));
        boolean fullRefund = newRefunded.compareTo(paid) == 0;
        int rows = ptPrivateOrderDao.updateRefund(orderId, newRefunded, refunded,
                fullRefund ? Integer.valueOf(4) : null, fullRefund ? Integer.valueOf(3) : null, operatorId);
        if (rows == 0) {
            throw new RRException(CodeAndMsg.ERROR_PT_ORDER_STATUS);
        }

        // 5. 退款流水:口径对齐 VIP 转让退款(payType=9,金额存正数,正负号由前台 moneyType CASE 决定);
        //    收支方式按原支付渠道:微信→WXPAY,储值→BLPAY(余额支付)
        int refundTradeType = payMethod == 3 ? ConfigConstant.BLPAY : ConfigConstant.WXPAY;
        incomePayDetailService.savePtOrderRefund(order.getOrderNo(), amount,
                order.getMemberId(), order.getStoreId(), refundTradeType);

        // 6. 渠道退款放事务末:受理失败抛异常,账本/订单/流水整体回滚可重试。
        if (payMethod == 3) {
            // 储值支付原路回退(第19步接线):同事务 changeBalance(+退款额,flowType=3退款)回补余额,
            // 不冲减 total_consume(口径=历史发生额);out_order_no=R+订单号做幂等,不下微信单
            ptMemberWalletService.walletRefund(order.getMemberId(), amount, order.getOrderNo(), orderId, operatorId);
            log.info("私教订单储值退款受理成功 orderNo={},amount={},lessons={},operatorId={}",
                    order.getOrderNo(), amount, lessons, operatorId);
            return;
        }
        //    微信原路退:out_refund_no=订单号R+累计退款分:同金额重试幂等(微信同退款单号只退一笔),且支持一单多次部分退款
        Map<String, Object> refundParams = new HashMap<String, Object>();
        refundParams.put("orderNo", order.getOrderNo());
        refundParams.put("refundNo", order.getOrderNo() + "R" + newRefunded.movePointRight(2).intValue());
        refundParams.put("totalFee", paid);
        refundParams.put("realPayment", amount);
        refundParams.put("refundDesc", remark == null || remark.trim().isEmpty() ? "私教订单退款" : remark.trim());
        String res;
        try {
            res = payService.wxRefund(refundParams);
        } catch (Exception e) {
            log.error("私教订单退款调用异常 orderId={},orderNo={}", orderId, order.getOrderNo(), e);
            throw new RRException("微信退款受理失败,请稍后重试");
        }
        if (res == null) {
            throw new RRException("微信退款受理失败,请稍后重试");
        }
        log.info("私教订单退款受理成功 orderNo={},amount={},lessons={},operatorId={}",
                order.getOrderNo(), amount, lessons, operatorId);
    }

    /**
     * 附赠团课权益发放:pt_product_group_benefit(is_enabled=1 且 status=1)配置时,
     * 按 gift_count/validity_days 插会员权益实例 pt_member_group_benefit + 发放流水(flow_type=1,biz_type=1)。
     * 闸1已挡重复回调,再按 source_order_id 判存兜一道(一单只发一次);
     * scope_type 适用范围留在规则表,团课预约核销侧按 source_product_id 回查(依赖团课模块后续接入)。
     */
    private void grantGroupBenefit(PtPrivateOrderEntity order) {
        Map<String, Object> rule = ptPrivateOrderDao.selectGroupBenefitRule(order.getProductId());
        if (rule == null) {
            return; // 未配置/未开启赠送/规则停用
        }
        Object giftCountObj = rule.get("giftCount");
        Object validityDaysObj = rule.get("validityDays");
        int giftCount = giftCountObj == null ? 0 : ((Number) giftCountObj).intValue();
        int validityDays = validityDaysObj == null ? 0 : ((Number) validityDaysObj).intValue();
        if (giftCount <= 0 || validityDays <= 0) {
            log.warn("附赠团课权益规则配置不完整,跳过发放 productId={},giftCount={},validityDays={}",
                    order.getProductId(), giftCountObj, validityDaysObj);
            return;
        }
        if (ptPrivateOrderDao.countMemberGroupBenefitByOrder(order.getId()) > 0) {
            return; // 已发放过,一单只发一次
        }
        ptPrivateOrderDao.insertMemberGroupBenefit(order.getMemberId(), order.getId(),
                order.getProductId(), giftCount, validityDays);
        Long benefitId = ptPrivateOrderDao.selectMemberGroupBenefitIdByOrder(order.getId());
        ptPrivateOrderDao.insertMemberGroupBenefitFlow(benefitId, order.getMemberId(), giftCount, order.getId());
    }

    /* ==================== 校验 + 定价(quote/create 共用,口径唯一) ==================== */

    /**
     * 校验商品上架/门店适用/可见人群/库存/限购 + 活动校验 + 券校验,并重算金额。
     * 任一校验不过抛 RRException(交易域 CodeAndMsg),quote/create 行为一致。
     */
    private PriceResult checkAndPrice(UserInfoVo user, Long productId, Long storeId,
                                      Long memberCouponId, Integer marketingType, Long marketingActivityId) {
        if (user == null || productId == null || storeId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        int mkType = marketingType == null ? MARKETING_NONE : marketingType;
        if (mkType != MARKETING_NONE && mkType != MARKETING_GROUP_BUY && mkType != MARKETING_FLASH_SALE) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }

        // ① 商品上架校验:listing_status=1 且 deleted=0 且未到 unlisting_at(SQL 内收口)
        PtProduct product = ptPrivateOrderDao.selectProductForOrder(productId);
        if (product == null) {
            throw new RRException(CodeAndMsg.ERROR_PRODUCT_OFF);
        }
        // ② 门店适用:购买门店必须在 pt_product_store_rel
        if (ptPrivateOrderDao.countProductStore(productId, storeId) == 0) {
            throw new RRException(CodeAndMsg.ERROR_PRODUCT_OFF);
        }
        // ③ 可见人群:visible_groups 的身份判定规则(member/new_user/student)需求未定义,
        //    与第10步商品域同口径不臆造,本期不拦截;规则明确后在此补齐校验
        // ④ 商品库存:sale_stock 为 NULL 不限量;sold_count 由回调累计(第13步),此处只做前置拦截,
        //    终态护栏是回调侧"sold_count<sale_stock 条件 UPDATE"
        if (product.getSaleStock() != null
                && nvl(product.getSoldCount()) >= product.getSaleStock()) {
            throw new RRException(CodeAndMsg.ERROR_STOCK_NOT_ENOUGH);
        }
        // ⑤ 商品限购:按该会员已支付+待支付单数(0/1/2)计
        if (product.getPurchaseLimit() != null
                && ptPrivateOrderDao.countMemberProductOrders(user.getUserId(), productId) >= product.getPurchaseLimit()) {
            throw new RRException(CodeAndMsg.ERROR_PURCHASE_LIMIT);
        }

        PriceResult priced = new PriceResult();
        priced.product = product;
        // 活动价不叠加权益价；普通购买按会员有效 VIP 权益匹配，命中多张时 SQL 取最低权益价。
        priced.originalAmount = scale(product.getSalePrice());
        if (mkType == MARKETING_NONE) {
            Map<String, Object> benefitPrice = ptPrivateOrderDao.selectBestBenefitPrice(user.getUserId(), productId);
            if (benefitPrice != null) {
                priced.benefitVipCardId = ((Number) benefitPrice.get("vipCardId")).longValue();
                priced.benefitPrice = scale((BigDecimal) benefitPrice.get("benefitPrice"));
                priced.originalAmount = priced.benefitPrice;
            }
        }
        priced.marketingType = mkType;
        priced.activityDiscountAmount = BigDecimal.ZERO;
        priced.couponDiscountAmount = BigDecimal.ZERO;

        BigDecimal payable = priced.originalAmount;

        if (mkType != MARKETING_NONE) {
            // 活动单不叠加优惠券(需求7.2/详细文档§5.4),传券直接拒绝
            if (memberCouponId != null) {
                throw new RRException(CodeAndMsg.ERROR_COUPON_INVALID);
            }
            payable = applyActivity(priced, user, productId, mkType, marketingActivityId);
        } else if (memberCouponId != null) {
            // 普通单用券:券属本人/未使用/未过期在 SQL 内收口(占用 CAS 在 create 再兜一道)
            Map<String, Object> coupon = ptPrivateOrderCouponRelDao.selectMemberCoupon(memberCouponId, user.getUserId());
            if (coupon == null) {
                throw new RRException(CodeAndMsg.ERROR_COUPON_INVALID);
            }
            priced.memberCoupon = coupon;
            // 第18步:接入营销域真实抵扣算法(满减/折扣封顶/门槛/门店&指定商品/新人 §5.1)。
            // 新人券口径需求未定义,按任务裁定「先不强拦」传 isNewUser=true(与本文件 member/new_user 取价
            // "规则明确后在此替换"同步);规则明确后仅需替换此实参。券不适用/未达门槛时服务返回 0 抵扣,
            // 券仍会被占用并落 0 抵扣明细(占用 CAS 在下方 create 收口,四状态口径统一)。
            priced.couponDiscountAmount = mkCouponService.calcCouponDiscount(
                    memberCouponId, user.getUserId(), productId, storeId, priced.originalAmount, true);
            payable = payable.subtract(priced.couponDiscountAmount);
        }

        if (payable.compareTo(BigDecimal.ZERO) < 0) {
            payable = BigDecimal.ZERO;
        }
        priced.payableAmount = scale(payable);
        return priced;
    }

    /** 活动(拼团/秒杀)校验+取价:时间窗/status 在 SQL 内收口,此处校验商品匹配/活动库存/活动限购 */
    private BigDecimal applyActivity(PriceResult priced, UserInfoVo user, Long productId,
                                     int mkType, Long marketingActivityId) {
        if (marketingActivityId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        Map<String, Object> activity = mkType == MARKETING_GROUP_BUY
                ? ptPrivateOrderDao.selectGroupBuyActivity(marketingActivityId)
                : ptPrivateOrderDao.selectFlashSaleActivity(marketingActivityId);
        String activityLabel = mkType == MARKETING_GROUP_BUY ? "拼团" : "秒杀";
        if (activity == null) {
            // 活动不存在/已下架/不在时间窗:错误码收口表暂无活动专用码,复用通用"对象不存在"
            throw new RRException(activityLabel + "活动不存在或已结束", CodeAndMsg.ERROR_NOT_EXIS.getCode());
        }
        // 活动必须挂在当前商品上
        if (((Number) activity.get("productId")).longValue() != productId.longValue()) {
            throw new RRException(activityLabel + "活动与商品不匹配", CodeAndMsg.ERROR_NOT_EXIS.getCode());
        }
        // 活动库存:拼团 activity_stock 可为 NULL(不限量),秒杀必填
        Object stock = activity.get("activityStock");
        if (stock != null && toInt(activity.get("soldCount")) >= ((Number) stock).intValue()) {
            throw new RRException(CodeAndMsg.ERROR_STOCK_NOT_ENOUGH);
        }
        // 活动限购:该会员在该活动下已支付+待支付单数
        Object limit = activity.get("purchaseLimit");
        if (limit != null && ptPrivateOrderDao.countMemberActivityOrders(user.getUserId(), mkType, marketingActivityId)
                >= ((Number) limit).intValue()) {
            throw new RRException(CodeAndMsg.ERROR_PURCHASE_LIMIT);
        }

        BigDecimal activityPrice = scale((BigDecimal) activity.get("activityPrice"));
        priced.marketingActivityId = marketingActivityId;
        priced.marketingActivityName = (String) activity.get("activityName");
        // 活动优惠=基础价-活动价(活动价高于基础价的异常配置按0优惠,仍按活动价成交)
        BigDecimal discount = priced.originalAmount.subtract(activityPrice);
        priced.activityDiscountAmount = discount.compareTo(BigDecimal.ZERO) > 0 ? discount : BigDecimal.ZERO;
        return activityPrice;
    }

    /* ==================== 内部工具 ==================== */

    /** 使用已经验签的微信查单结果补做本地结算，回调与主动查单复用同一幂等入口。 */
    private void settleWechatPayment(Map<String, String> queryResult) {
        String orderNo = queryResult.get("out_trade_no");
        String transactionId = queryResult.get("transaction_id");
        String totalFee = queryResult.get("total_fee");
        if (orderNo == null || orderNo.trim().isEmpty()
                || transactionId == null || transactionId.trim().isEmpty()
                || totalFee == null || totalFee.trim().isEmpty()) {
            throw new RRException("微信支付查询结果缺少结算参数");
        }
        BigDecimal totalFeeFen;
        try {
            totalFeeFen = new BigDecimal(totalFee);
        } catch (NumberFormatException e) {
            throw new RRException("微信支付查询金额格式错误", e);
        }
        if (totalFeeFen.scale() > 0 || totalFeeFen.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException("微信支付查询金额格式错误");
        }
        BigDecimal paidAmount = totalFeeFen.movePointLeft(2).setScale(2);
        updatePrivateOrder(orderNo, paidAmount, transactionId, ConfigConstant.WXPAY);
    }

    /** 前端只消费本地最终状态；tradeState 用于展示微信侧仍在处理/未支付等中间态。 */
    private Map<String, Object> buildPaymentState(PtPrivateOrderEntity order, String tradeState) {
        Map<String, Object> result = new HashMap<String, Object>();
        boolean paid = isPaid(order);
        result.put("orderNo", order.getOrderNo());
        result.put("paid", paid);
        result.put("orderStatus", order.getOrderStatus());
        result.put("payStatus", order.getPayStatus());
        result.put("tradeState", tradeState == null ? (paid ? "SUCCESS" : "LOCAL") : tradeState);
        return result;
    }

    private boolean isPaid(PtPrivateOrderEntity order) {
        return order != null
                && (Integer.valueOf(1).equals(order.getOrderStatus()) || Integer.valueOf(2).equals(order.getOrderStatus()))
                && (Integer.valueOf(1).equals(order.getPayStatus()) || Integer.valueOf(2).equals(order.getPayStatus()));
    }

    private String errorCodeSuffix(String errCode) {
        return errCode == null || errCode.trim().isEmpty() ? "" : "（" + errCode + "）";
    }

    /** 组装待支付订单实体：教练与支付方式均按确认订单页选择写入快照。 */
    private PtPrivateOrderEntity buildOrder(UserInfoVo user, Long storeId, Long coachId,
                                            Integer payMethod, PriceResult priced) {
        PtProduct product = priced.product;
        PtPrivateOrderEntity order = new PtPrivateOrderEntity();
        order.setMemberId(user.getUserId());
        order.setMemberName(user.getNickname());
        order.setMemberMobile(user.getPhone());
        order.setProductId(product.getId());
        order.setProductName(product.getProductName());
        order.setProductTypeId(product.getProductTypeId());
        order.setProductTypeName(product.getTypeName());
        order.setServiceType(product.getServiceType());
        order.setStoreId(storeId);
        order.setCoachId(coachId);
        // 课时/时长/有效期快照:激活权益(第13步)一律取订单快照,不回查商品
        order.setLessonCount(product.getLessonCount());
        order.setDurationMinutes(product.getDurationMinutes());
        order.setValidityDays(product.getValidityDays());
        order.setBenefitVipCardId(priced.benefitVipCardId);
        order.setBenefitPrice(priced.benefitPrice);
        order.setOriginalAmount(priced.originalAmount);
        order.setPayableAmount(priced.payableAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setRefundAmount(BigDecimal.ZERO);
        // 恒等式:payable = original - discount
        order.setDiscountAmount(priced.originalAmount.subtract(priced.payableAmount));
        order.setMarketingType(priced.marketingType);
        order.setMarketingActivityId(priced.marketingActivityId);
        order.setMarketingActivityName(priced.marketingActivityName);
        if (priced.memberCoupon != null) {
            order.setCouponId(((Number) priced.memberCoupon.get("couponId")).longValue());
            order.setMemberCouponId(((Number) priced.memberCoupon.get("id")).longValue());
            order.setCouponName((String) priced.memberCoupon.get("couponName"));
        }
        order.setCouponDiscountAmount(priced.couponDiscountAmount);
        order.setPayMethod(payMethod);
        order.setPayStatus(0);
        order.setOrderStatus(0);
        order.setCreatedBy(user.getUserId());
        return order;
    }

    /** 返回确认订单页可直接展示的分期配置；配置不完整时只关闭分期选项，不影响其他支付方式。 */
    private Map<String, Object> buildInstallmentOption(PtProduct product, BigDecimal totalAmount) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("installmentAvailable", false);
        if (product == null || product.getId() == null) {
            return result;
        }
        PtInstallmentRuleEntity rule = ptInstallmentRuleApiDao.selectByProductId(product.getId());
        BigDecimal total = scale(totalAmount);
        BigDecimal down = rule == null ? BigDecimal.ZERO : scale(rule.getDownPaymentAmount());
        if (rule == null || !Integer.valueOf(1).equals(rule.getIsEnabled())
                || !Integer.valueOf(1).equals(rule.getStatus())
                || rule.getInstallmentCount() == null || rule.getInstallmentCount() < 2
                || rule.getInstallmentIntervalMonths() == null || rule.getInstallmentIntervalMonths() < 1
                || down.compareTo(BigDecimal.ZERO) <= 0 || down.compareTo(total) >= 0) {
            return result;
        }
        result.put("installmentAvailable", true);
        result.put("installmentDownPaymentAmount", down);
        result.put("installmentCount", rule.getInstallmentCount());
        result.put("installmentIntervalMonths", rule.getInstallmentIntervalMonths());
        return result;
    }

    /** 历史待支付订单继续支付时按订单总额重新读取同一商品分期规则。 */
    private Map<String, Object> buildInstallmentOptionForOrder(PtPrivateOrderEntity order) {
        PtProduct product = ptPrivateOrderDao.selectProductForOrder(order.getProductId());
        return buildInstallmentOption(product, order.getPayableAmount());
    }

    /** 订单号:PT + yyyyMMddHHmmss+随机 + 后缀 b(回调按末位分发,§0.6.1) */
    private String genOrderNo() {
        return "PT" + OrderNoGenerator.getOrderIdByTime() + ConfigConstant.PT_PRIVATE_ORDER_TYPE;
    }

    /** 金额统一两位小数,HALF_UP */
    private BigDecimal scale(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP);
    }

    /** 支付成功后匹配整单提成规则；没有唯一订单归属教练或规则时不产生提成明细。 */
    private void settleWholeOrderCommission(PtPrivateOrderEntity order, BigDecimal paidAmount) {
        // 包月课程按实际授课教练逐节结算，不使用普通整单/课时分成规则。
        if (ptCoachMonthlyCommissionRuleDao.countMonthlyProduct(order.getProductId()) > 0) {
            return;
        }
        if (order.getCoachId() == null) {
            log.warn("私教订单未绑定唯一归属教练，跳过整单提成 orderNo={},productId={}",
                    order.getOrderNo(), order.getProductId());
            return;
        }
        PtCoachFeeRuleEntity rule = sysCoachFeeRuleService.matchFeeRule(
                order.getCoachId(), order.getProductId(), order.getStoreId(), 2);
        if (rule == null || rule.getCommissionRate() == null) {
            return;
        }
        BigDecimal commission = paidAmount.multiply(rule.getCommissionRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        SysCoachTradeDetailEntity detail = new SysCoachTradeDetailEntity();
        detail.setCoachId(order.getCoachId());
        detail.setTradeType(1);
        detail.setMoney(commission);
        detail.setOrigMoney(paidAmount);
        detail.setPercent(rule.getCommissionRate().doubleValue());
        detail.setTransactionNumber("PT_SALE_" + order.getId());
        detail.setOrderNo(order.getOrderNo());
        detail.setStatus(1);
        detail.setTransactionTime(new Date());
        detail.setCreateTime(new Date());
        sysCoachTradeDetailService.save(detail);
    }

    private int nvl(Integer val) {
        return val == null ? 0 : val;
    }

    /** Map 取整数(兼容 Integer/Long/BigInteger 等 JDBC 数值类型) */
    private int toInt(Object val) {
        if (val == null) {
            throw new RRException(CodeAndMsg.ERROR_NOT_EXIS);
        }
        return ((Number) val).intValue();
    }

    /** quote/create 共用的定价结果 */
    private static class PriceResult {
        PtProduct product;
        /** 基础价快照(本期口径=sale_price) */
        BigDecimal originalAmount;
        Long benefitVipCardId;
        BigDecimal benefitPrice;
        BigDecimal payableAmount;
        BigDecimal couponDiscountAmount;
        BigDecimal activityDiscountAmount;
        int marketingType;
        Long marketingActivityId;
        String marketingActivityName;
        /** 校验通过的会员券行(selectMemberCoupon 结果),null=未用券 */
        Map<String, Object> memberCoupon;

        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("productId", product.getId());
            map.put("productName", product.getProductName());
            map.put("originalAmount", originalAmount);
            map.put("benefitVipCardId", benefitVipCardId);
            map.put("benefitPrice", benefitPrice);
            map.put("payableAmount", payableAmount);
            map.put("couponDiscountAmount", couponDiscountAmount);
            map.put("activityDiscountAmount", activityDiscountAmount);
            map.put("discountAmount", originalAmount.subtract(payableAmount));
            map.put("marketingType", marketingType);
            map.put("marketingActivityId", marketingActivityId);
            map.put("marketingActivityName", marketingActivityName);
            return map;
        }
    }
}
