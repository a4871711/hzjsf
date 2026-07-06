package com.dlc.modules.api.entity;

import java.io.Serializable;

/**
 * 会员优惠券领取记录 mk_member_coupon 的 api 侧投影(第18步移动端「我的券」/领券/可用券用)。
 * 与 sys.entity.MkMemberCouponEntity 同表不同包,走 mapper/api/MkMemberCouponApiDao.xml(改后须重启)。
 * 快照 coupon_name/coupon_type(券模板改名不回溯);时间列用 String 承接 DATE_FORMAT 结果
 * (与 MkMemberCoupon sys 侧、PtCoachScheduleEntity 同一约定,规避 connector8 LocalDateTime 差异)。
 * use_status:0未使用 1已使用 2已过期 3使用中(下单占用态)。
 *
 * @author claude
 */
public class MkMemberCouponEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long memberId;
    private Long couponId;
    /** 券名称快照 */
    private String couponName;
    /** 券类型快照:1满减券 2折扣券 */
    private Integer couponType;
    private String receiveTime;
    /** 到期时间(领券时 = now + valid_days) */
    private String expireTime;
    /** 使用状态:0未使用 1已使用 2已过期 3使用中 */
    private Integer useStatus;
    private Long usedOrderId;
    private String usedTime;

    /* ===== 非持久字段:券模板信息(myList/usableForOrder 联 mk_coupon 带出,供前端展示与试算) ===== */
    /** 优惠金额(满减券) */
    private java.math.BigDecimal discountAmount;
    /** 折扣值 8.50=8.5折(折扣券) */
    private java.math.BigDecimal discountRate;
    /** 最高优惠金额(折扣券封顶) */
    private java.math.BigDecimal maxDiscountAmount;
    /** 使用门槛金额 */
    private java.math.BigDecimal useThresholdAmount;
    /** 试算抵扣额(usableForOrder 逐张预算,§5.1) */
    private java.math.BigDecimal calcDiscountAmount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public Integer getCouponType() { return couponType; }
    public void setCouponType(Integer couponType) { this.couponType = couponType; }

    public String getReceiveTime() { return receiveTime; }
    public void setReceiveTime(String receiveTime) { this.receiveTime = receiveTime; }

    public String getExpireTime() { return expireTime; }
    public void setExpireTime(String expireTime) { this.expireTime = expireTime; }

    public Integer getUseStatus() { return useStatus; }
    public void setUseStatus(Integer useStatus) { this.useStatus = useStatus; }

    public Long getUsedOrderId() { return usedOrderId; }
    public void setUsedOrderId(Long usedOrderId) { this.usedOrderId = usedOrderId; }

    public String getUsedTime() { return usedTime; }
    public void setUsedTime(String usedTime) { this.usedTime = usedTime; }

    public java.math.BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(java.math.BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public java.math.BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(java.math.BigDecimal discountRate) { this.discountRate = discountRate; }

    public java.math.BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(java.math.BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public java.math.BigDecimal getUseThresholdAmount() { return useThresholdAmount; }
    public void setUseThresholdAmount(java.math.BigDecimal useThresholdAmount) { this.useThresholdAmount = useThresholdAmount; }

    public java.math.BigDecimal getCalcDiscountAmount() { return calcDiscountAmount; }
    public void setCalcDiscountAmount(java.math.BigDecimal calcDiscountAmount) { this.calcDiscountAmount = calcDiscountAmount; }
}
