package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教订单优惠券使用记录(对应表 pt_private_order_coupon_rel)。
 * 一期一单一券(uk order_id 唯一),是订单上券快照字段的明细补充,与订单在下单事务里同写;
 * 用于对账与退款时回滚判断。
 *
 * @author claude
 */
public class PtPrivateOrderCouponRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 私教订单ID(唯一键,一单一券) */
    private Long orderId;
    /** 优惠券ID(mk_coupon 主数据) */
    private Long couponId;
    /** 会员优惠券ID(mk_member_coupon 领取记录) */
    private Long memberCouponId;
    /** 优惠券名称快照 */
    private String couponName;
    /** 券类型:1满减券 2折扣券 */
    private Integer couponType;
    /** 本次抵扣金额(本步为桩=0,第18步接真实券算法) */
    private BigDecimal discountAmount;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public Long getMemberCouponId() { return memberCouponId; }
    public void setMemberCouponId(Long memberCouponId) { this.memberCouponId = memberCouponId; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public Integer getCouponType() { return couponType; }
    public void setCouponType(Integer couponType) { this.couponType = couponType; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
