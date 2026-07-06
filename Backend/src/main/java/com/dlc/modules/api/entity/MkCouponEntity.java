package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券主表 mk_coupon 的 api 侧只读投影(第18步移动端领券/抵扣用)。
 * 与 sys.entity.MkCouponEntity 同表不同包:sys 侧管后台 CRUD,api 侧只取抵扣算法需要的模板字段,
 * 走 mapper/api/MkCouponApiDao.xml(改后须重启 Tomcat)。不带 created_at/updated_at 等日期列,
 * 规避 connector8 LocalDateTime 类型差异;门店/指定商品适用性用 count 子查询单独判,不落列表字段。
 *
 * @author claude
 */
public class MkCouponEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String couponName;
    /** 券类型:1满减券 2折扣券 */
    private Integer couponType;
    /** 优惠金额,满减券使用 */
    private BigDecimal discountAmount;
    /** 折扣值,8.50 表示 8.5 折,折扣券使用 */
    private BigDecimal discountRate;
    /** 最高优惠金额(折扣券封顶,非必填) */
    private BigDecimal maxDiscountAmount;
    /** 使用门槛金额,NULL/0=无门槛 */
    private BigDecimal useThresholdAmount;
    /** 领券后有效天数 */
    private Integer validDays;
    /** 适用范围:1全部商品 2指定商品 */
    private Integer scopeType;
    /** 是否新人券:0否 1是 */
    private Integer isNewUserCoupon;
    /** 状态:1上架 0下架 */
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public Integer getCouponType() { return couponType; }
    public void setCouponType(Integer couponType) { this.couponType = couponType; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public BigDecimal getUseThresholdAmount() { return useThresholdAmount; }
    public void setUseThresholdAmount(BigDecimal useThresholdAmount) { this.useThresholdAmount = useThresholdAmount; }

    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }

    public Integer getScopeType() { return scopeType; }
    public void setScopeType(Integer scopeType) { this.scopeType = scopeType; }

    public Integer getIsNewUserCoupon() { return isNewUserCoupon; }
    public void setIsNewUserCoupon(Integer isNewUserCoupon) { this.isNewUserCoupon = isNewUserCoupon; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
