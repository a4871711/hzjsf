package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员私教购买记录(对应表 pt_private_order),交易域写入主表。
 * 课时/时长/有效期/商品名/类型名/券/活动等均为下单时快照,商品后续改价改名不回写历史;
 * 激活权益(第13步回调)一律取本表快照,禁止 join 当前 pt_product。
 * 金额拆分恒等:payable_amount = original_amount - discount_amount。
 * 订单号末位后缀 ConfigConstant.PT_PRIVATE_ORDER_TYPE("b"),供支付回调按后缀分发,严禁复用旧私教课的 "4"。
 *
 * @author claude
 */
public class PtPrivateOrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 订单编号,PT+yyyyMMddHHmmss+随机+末位后缀b,全局唯一 */
    private String orderNo;
    private Long memberId;
    /** 会员姓名快照 */
    private String memberName;
    /** 会员手机号快照 */
    private String memberMobile;
    private Long productId;
    /** 购买时商品名称快照 */
    private String productName;
    /** 购买时商品类型ID快照 */
    private Long productTypeId;
    /** 购买时商品类型名称快照 */
    private String productTypeName;
    /** 服务类型:1一对一 2一对多 */
    private Integer serviceType;
    /** 购买门店ID(门店隔离锚点) */
    private Long storeId;
    /** 购买课时数(快照) */
    private Integer lessonCount;
    /** 单次服务时长快照(分钟) */
    private Integer durationMinutes;
    /** 有效期天数快照;-1 长期 */
    private Integer validityDays;
    /** 原价金额(本期口径=下单时 sale_price 基础价快照) */
    private BigDecimal originalAmount;
    /** 应付金额(后端重算,不信前端) */
    private BigDecimal payableAmount;
    /** 实付金额(回调置值) */
    private BigDecimal paidAmount;
    /** 累计退款金额 */
    private BigDecimal refundAmount;
    /** 优惠合计=活动优惠+券抵扣 */
    private BigDecimal discountAmount;
    /** 营销类型:0普通购买 1拼团 2限时秒杀 */
    private Integer marketingType;
    private Long marketingActivityId;
    /** 营销活动名称快照 */
    private String marketingActivityName;
    /** 使用优惠券ID(mk_coupon 主数据) */
    private Long couponId;
    /** 会员优惠券ID(mk_member_coupon 领取记录) */
    private Long memberCouponId;
    /** 使用优惠券名称快照 */
    private String couponName;
    /** 优惠券抵扣金额(本步为桩=0,第18步接真实券算法) */
    private BigDecimal couponDiscountAmount;
    /** 支付方式:1微信 2支付宝 3储值 4分期 9其他(与订单号后缀是两码事) */
    private Integer payMethod;
    /** 支付状态:0待支付 1部分支付 2已支付 3已退款 */
    private Integer payStatus;
    /** 订单状态:0待支付 1首付已付 2已结清 3已取消 4已退款 */
    private Integer orderStatus;
    private Date paidAt;
    private Date settledAt;
    private Date cancelAt;
    private Date refundAt;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    /** 是否删除:0否 1是 */
    private Integer deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getProductTypeId() { return productTypeId; }
    public void setProductTypeId(Long productTypeId) { this.productTypeId = productTypeId; }

    public String getProductTypeName() { return productTypeName; }
    public void setProductTypeName(String productTypeName) { this.productTypeName = productTypeName; }

    public Integer getServiceType() { return serviceType; }
    public void setServiceType(Integer serviceType) { this.serviceType = serviceType; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Integer getLessonCount() { return lessonCount; }
    public void setLessonCount(Integer lessonCount) { this.lessonCount = lessonCount; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }

    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }

    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal payableAmount) { this.payableAmount = payableAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public Integer getMarketingType() { return marketingType; }
    public void setMarketingType(Integer marketingType) { this.marketingType = marketingType; }

    public Long getMarketingActivityId() { return marketingActivityId; }
    public void setMarketingActivityId(Long marketingActivityId) { this.marketingActivityId = marketingActivityId; }

    public String getMarketingActivityName() { return marketingActivityName; }
    public void setMarketingActivityName(String marketingActivityName) { this.marketingActivityName = marketingActivityName; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public Long getMemberCouponId() { return memberCouponId; }
    public void setMemberCouponId(Long memberCouponId) { this.memberCouponId = memberCouponId; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public BigDecimal getCouponDiscountAmount() { return couponDiscountAmount; }
    public void setCouponDiscountAmount(BigDecimal couponDiscountAmount) { this.couponDiscountAmount = couponDiscountAmount; }

    public Integer getPayMethod() { return payMethod; }
    public void setPayMethod(Integer payMethod) { this.payMethod = payMethod; }

    public Integer getPayStatus() { return payStatus; }
    public void setPayStatus(Integer payStatus) { this.payStatus = payStatus; }

    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }

    public Date getPaidAt() { return paidAt; }
    public void setPaidAt(Date paidAt) { this.paidAt = paidAt; }

    public Date getSettledAt() { return settledAt; }
    public void setSettledAt(Date settledAt) { this.settledAt = settledAt; }

    public Date getCancelAt() { return cancelAt; }
    public void setCancelAt(Date cancelAt) { this.cancelAt = cancelAt; }

    public Date getRefundAt() { return refundAt; }
    public void setRefundAt(Date refundAt) { this.refundAt = refundAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
