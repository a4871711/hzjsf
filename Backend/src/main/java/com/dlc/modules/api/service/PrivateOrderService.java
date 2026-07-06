package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.vo.UserInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 私教订单 Service(移动端):试算 quote / 下单 create / 我的订单 / 订单详情。
 * 金额一律后端重算(不信前端),BigDecimal HALF_UP;
 * create 只建待支付单+占券+调微信统一下单,不收钱不扣库存(回调推进在第13步 updatePrivateOrder)。
 *
 * @author claude
 */
public interface PrivateOrderService {

    /**
     * 试算:后端按当前商品价/活动价重算金额,券抵扣本步为桩(=0,第18步接真实算法)。
     * 活动单(marketingType 1拼团/2秒杀)不叠加优惠券,传券直接 ERROR_COUPON_INVALID。
     *
     * @return originalAmount/payableAmount/couponDiscountAmount/activityDiscountAmount/discountAmount 等明细
     */
    Map<String, Object> quote(UserInfoVo user, Long productId, Long storeId,
                              Long memberCouponId, Integer marketingType, Long marketingActivityId);

    /**
     * 下单(单事务):校验上架/门店/可见人群/限购/库存 → 金额重算(口径与 quote 同一方法)
     * → 建 pt_private_order(order_status=0,pay_status=0,全量快照) → 券明细+mk_member_coupon 占用 CAS
     * → 调微信统一下单。统一下单失败抛运行时异常整体回滚(订单与券占用一并回退)。
     *
     * @return {orderNo, payableAmount, payParams} payParams 为小程序调起支付参数
     */
    Map<String, Object> create(UserInfoVo user, Long productId, Long storeId,
                               Long memberCouponId, Integer marketingType, Long marketingActivityId,
                               HttpServletRequest request);

    /** 我的订单分页(params 需含 userId/page/limit,可选 orderStatus) */
    PageUtils myOrders(Map<String, Object> params);

    /** 订单详情(含券明细);非本人订单按不存在处理(ERROR_PT_ORDER_NOT_EXIST) */
    Map<String, Object> detail(Long userId, String orderNo);

    /**
     * 支付成功回调核心(第13步,单事务,签名对齐既有回调分支 updateXxxOrder 调用约定)。
     * 流程:FOR UPDATE 锁订单行 → 幂等闸1 order_status=0 前置判断 → 记账 IncomePayDetail
     * → 幂等闸3 扣库存条件 UPDATE(活动单加扣活动表) → 券核销 3使用中→1已使用
     * → 支付方式分支【一次性微信=结清 / 储值·分期=桩,第19/20步接线】
     * → 幂等闸2 按 order_id 建权益(activate) → 附赠团课权益发放 → 团课转私教留桩(第22步)。
     * 微信可能重复回调:重复回调/异常单号返回 0 不抛错,回调链正常应答避免无限重试。
     *
     * @param orderNo       商户订单号(末位后缀 b)
     * @param wallet        回调实收金额(元)
     * @param transactionId 第三方支付流水号
     * @param payType       收支方式(ConfigConstant.WXPAY/ZFBPAY)
     * @return 1=本次完成状态推进;0=幂等跳过(重复回调/单号不存在/桩分支)
     */
    int updatePrivateOrder(String orderNo, BigDecimal wallet, String transactionId, Integer payType);

    /**
     * 后台退款冲减(第15步,sys 后台 /sys/privateOrder/refund 委托入口,单事务)。
     * 流程:FOR UPDATE 锁单 → 校验 order_status∈{1首付已付,2已结清} 且 refundAmount≤paid-refund(锁内上限校验天然防重复提交)
     * → 冲减权益课时(refundLessons 缺省=剩余课时全冲,refundDeduct 只冲 remaining,不可冲到冻结/已用)
     * → 订单落退款(全额退→order_status=4/pay_status=3,部分退保持原状态) → 写退款流水(payType=9)
     * → 渠道退款放事务末:微信(1)走现有 wxRefund 通道,受理失败抛异常整体回滚;
     *   储值(3)/分期(4)【桩】第19/20步接线前直接拒绝。
     * 注意:本类属 api 包,改动须重启 Tomcat 才生效。
     *
     * @param orderId       私教订单ID
     * @param refundAmount  本次退款金额(元,>0 且不超过 paid_amount - refund_amount)
     * @param refundLessons 冲减课时数(null=按权益剩余课时全冲;0=只退钱不冲课时)
     * @param remark        退款备注(透传微信 refund_desc)
     * @param operatorId    操作管理员ID(落 updated_by)
     */
    void refund(Long orderId, BigDecimal refundAmount, Integer refundLessons, String remark, Long operatorId);
}
