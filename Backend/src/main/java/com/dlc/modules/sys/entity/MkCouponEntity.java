package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 优惠券主表 mk_coupon（营销域，与旧 sys_coupon 并存不迁移，私教链路只认 mk_*）。
 * 注意状态枚举：1上架/0下架（勿照抄旧 sys_coupon 的 2=下架）。
 * 表单一次性提交主表 + 可用门店/指定商品关联，非持久字段直接挂在本实体（与 PtProductEntity 同一约定）。
 *
 * @author claude
 */
public class MkCouponEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String couponName;
    /** 券类型：1满减券 2折扣券 */
    private Integer couponType;
    /** 美团团购ID（外部关联，选填） */
    private String mtGroupBuyId;
    /** 抖音团购ID（外部关联，选填） */
    private String dyGroupBuyId;
    /** 优惠金额，满减券必填 */
    private BigDecimal discountAmount;
    /** 折扣值，8.50 表示 8.5 折，折扣券必填 */
    private BigDecimal discountRate;
    /** 最高优惠金额（折扣券封顶，选填） */
    private BigDecimal maxDiscountAmount;
    /** 使用门槛金额，NULL/0=无门槛 */
    private BigDecimal useThresholdAmount;
    /** 领券后有效天数 */
    private Integer validDays;
    /** 适用范围：1全部商品 2指定商品 */
    private Integer scopeType;
    /** 是否新人券：0否 1是 */
    private Integer isNewUserCoupon;
    /** 状态：1上架 0下架 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    /** 是否删除：0否 1是 */
    private Integer deleted;

    /* ===== 非持久字段 ===== */
    /** 可用门店ID（表单提交/info回填） */
    private List<Long> storeIds;
    /** 指定商品ID（scope_type=2 时，表单提交/info回填） */
    private List<Long> productIds;
    /** 可用门店名（列表展示，group_concat） */
    private String storeNames;
    /** 指定商品数（列表展示） */
    private Integer productCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public Integer getCouponType() { return couponType; }
    public void setCouponType(Integer couponType) { this.couponType = couponType; }

    public String getMtGroupBuyId() { return mtGroupBuyId; }
    public void setMtGroupBuyId(String mtGroupBuyId) { this.mtGroupBuyId = mtGroupBuyId; }

    public String getDyGroupBuyId() { return dyGroupBuyId; }
    public void setDyGroupBuyId(String dyGroupBuyId) { this.dyGroupBuyId = dyGroupBuyId; }

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

    public List<Long> getStoreIds() { return storeIds; }
    public void setStoreIds(List<Long> storeIds) { this.storeIds = storeIds; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public String getStoreNames() { return storeNames; }
    public void setStoreNames(String storeNames) { this.storeNames = storeNames; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }
}
