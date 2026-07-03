package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 私教商品主表 pt_product。
 * 表单一次性提交主表 + 适用门店/指定教练 + 分期规则 + 附赠团课权益，故非持久字段直接
 * 挂在本实体（与 PtCoachEntity.storeIds 同一约定），不另建 VO。
 *
 * @author claude
 */
public class PtProductEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 商品编号 SJ+yyyyMMddHHmmss+随机 */
    private String productNo;
    private String productName;
    private String productSubtitle;
    private Long productTypeId;
    /** 服务类型：1一对一 2一对多 */
    private Integer serviceType;
    private String categoryName;
    private String coverUrl;
    private String productIntro;
    private String productDetail;
    private String targetDesc;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private BigDecimal memberPrice;
    private BigDecimal newUserPrice;
    private Integer lessonCount;
    private Integer durationMinutes;
    /** 有效期天数；长期=-1 */
    private Integer validityDays;
    /** 退款方式：1支持 2不支持 3人工审核 */
    private Integer refundType;
    private String refundRule;
    /** 可见人群 JSON 字符串，例如 ["member","new_user","student"] */
    private String visibleGroups;
    private Integer purchaseLimit;
    /** 可售库存，NULL=不限量 */
    private Integer saleStock;
    private Integer soldCount;
    private Integer bookingGapMinutes;
    private Integer bookingCapacity;
    private Integer latestBookingHours;
    private Integer latestFreeCancelHours;
    /** 爽约是否扣课：0否 1是 */
    private Integer noShowDeduct;
    /** 预约是否需教练确认：0否 1是 */
    private Integer coachConfirmRequired;
    /** 上架状态：0未上架 1已上架 */
    private Integer listingStatus;
    private Date listingAt;
    private Date unlistingAt;
    private Integer sortNo;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    private Integer deleted;
    /** 是否私教入口推荐：0否 1是 */
    private Integer recommendPrivate;
    /** 是否首页推荐：0否 1是 */
    private Integer recommendHome;

    /* ============ 非持久字段：一次性提交/展示用 ============ */
    /** 适用门店ID集合 */
    private List<Long> storeIds;
    /** 指定教练ID集合，空表示不限教练 */
    private List<Long> coachIds;
    /** 展示用：商品类型名称 */
    private String typeName;

    /** 分期规则（pt_installment_rule，1:1，随商品表单提交） */
    private Integer installmentEnabled;
    private BigDecimal installmentDownPaymentAmount;
    private Integer installmentCount;
    private Integer installmentIntervalMonths;
    private Integer installmentOverduePauseBooking;

    /** 附赠团课权益规则（pt_product_group_benefit，1:1，随商品表单提交） */
    private Integer groupBenefitEnabled;
    private Integer groupBenefitGiftCount;
    private Integer groupBenefitValidityDays;
    /** 适用范围：1全部团课 2指定团课商品 */
    private Integer groupBenefitScopeType;
    /** 指定团课商品ID集合（scope_type=2 时必填） */
    private List<Long> groupProductIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductNo() { return productNo; }
    public void setProductNo(String productNo) { this.productNo = productNo; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSubtitle() { return productSubtitle; }
    public void setProductSubtitle(String productSubtitle) { this.productSubtitle = productSubtitle; }

    public Long getProductTypeId() { return productTypeId; }
    public void setProductTypeId(Long productTypeId) { this.productTypeId = productTypeId; }

    public Integer getServiceType() { return serviceType; }
    public void setServiceType(Integer serviceType) { this.serviceType = serviceType; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getProductIntro() { return productIntro; }
    public void setProductIntro(String productIntro) { this.productIntro = productIntro; }

    public String getProductDetail() { return productDetail; }
    public void setProductDetail(String productDetail) { this.productDetail = productDetail; }

    public String getTargetDesc() { return targetDesc; }
    public void setTargetDesc(String targetDesc) { this.targetDesc = targetDesc; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public BigDecimal getMemberPrice() { return memberPrice; }
    public void setMemberPrice(BigDecimal memberPrice) { this.memberPrice = memberPrice; }

    public BigDecimal getNewUserPrice() { return newUserPrice; }
    public void setNewUserPrice(BigDecimal newUserPrice) { this.newUserPrice = newUserPrice; }

    public Integer getLessonCount() { return lessonCount; }
    public void setLessonCount(Integer lessonCount) { this.lessonCount = lessonCount; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }

    public Integer getRefundType() { return refundType; }
    public void setRefundType(Integer refundType) { this.refundType = refundType; }

    public String getRefundRule() { return refundRule; }
    public void setRefundRule(String refundRule) { this.refundRule = refundRule; }

    public String getVisibleGroups() { return visibleGroups; }
    public void setVisibleGroups(String visibleGroups) { this.visibleGroups = visibleGroups; }

    public Integer getPurchaseLimit() { return purchaseLimit; }
    public void setPurchaseLimit(Integer purchaseLimit) { this.purchaseLimit = purchaseLimit; }

    public Integer getSaleStock() { return saleStock; }
    public void setSaleStock(Integer saleStock) { this.saleStock = saleStock; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public Integer getBookingGapMinutes() { return bookingGapMinutes; }
    public void setBookingGapMinutes(Integer bookingGapMinutes) { this.bookingGapMinutes = bookingGapMinutes; }

    public Integer getBookingCapacity() { return bookingCapacity; }
    public void setBookingCapacity(Integer bookingCapacity) { this.bookingCapacity = bookingCapacity; }

    public Integer getLatestBookingHours() { return latestBookingHours; }
    public void setLatestBookingHours(Integer latestBookingHours) { this.latestBookingHours = latestBookingHours; }

    public Integer getLatestFreeCancelHours() { return latestFreeCancelHours; }
    public void setLatestFreeCancelHours(Integer latestFreeCancelHours) { this.latestFreeCancelHours = latestFreeCancelHours; }

    public Integer getNoShowDeduct() { return noShowDeduct; }
    public void setNoShowDeduct(Integer noShowDeduct) { this.noShowDeduct = noShowDeduct; }

    public Integer getCoachConfirmRequired() { return coachConfirmRequired; }
    public void setCoachConfirmRequired(Integer coachConfirmRequired) { this.coachConfirmRequired = coachConfirmRequired; }

    public Integer getListingStatus() { return listingStatus; }
    public void setListingStatus(Integer listingStatus) { this.listingStatus = listingStatus; }

    public Date getListingAt() { return listingAt; }
    public void setListingAt(Date listingAt) { this.listingAt = listingAt; }

    public Date getUnlistingAt() { return unlistingAt; }
    public void setUnlistingAt(Date unlistingAt) { this.unlistingAt = unlistingAt; }

    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }

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

    public Integer getRecommendPrivate() { return recommendPrivate; }
    public void setRecommendPrivate(Integer recommendPrivate) { this.recommendPrivate = recommendPrivate; }

    public Integer getRecommendHome() { return recommendHome; }
    public void setRecommendHome(Integer recommendHome) { this.recommendHome = recommendHome; }

    public List<Long> getStoreIds() { return storeIds; }
    public void setStoreIds(List<Long> storeIds) { this.storeIds = storeIds; }

    public List<Long> getCoachIds() { return coachIds; }
    public void setCoachIds(List<Long> coachIds) { this.coachIds = coachIds; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public Integer getInstallmentEnabled() { return installmentEnabled; }
    public void setInstallmentEnabled(Integer installmentEnabled) { this.installmentEnabled = installmentEnabled; }

    public BigDecimal getInstallmentDownPaymentAmount() { return installmentDownPaymentAmount; }
    public void setInstallmentDownPaymentAmount(BigDecimal installmentDownPaymentAmount) { this.installmentDownPaymentAmount = installmentDownPaymentAmount; }

    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }

    public Integer getInstallmentIntervalMonths() { return installmentIntervalMonths; }
    public void setInstallmentIntervalMonths(Integer installmentIntervalMonths) { this.installmentIntervalMonths = installmentIntervalMonths; }

    public Integer getInstallmentOverduePauseBooking() { return installmentOverduePauseBooking; }
    public void setInstallmentOverduePauseBooking(Integer installmentOverduePauseBooking) { this.installmentOverduePauseBooking = installmentOverduePauseBooking; }

    public Integer getGroupBenefitEnabled() { return groupBenefitEnabled; }
    public void setGroupBenefitEnabled(Integer groupBenefitEnabled) { this.groupBenefitEnabled = groupBenefitEnabled; }

    public Integer getGroupBenefitGiftCount() { return groupBenefitGiftCount; }
    public void setGroupBenefitGiftCount(Integer groupBenefitGiftCount) { this.groupBenefitGiftCount = groupBenefitGiftCount; }

    public Integer getGroupBenefitValidityDays() { return groupBenefitValidityDays; }
    public void setGroupBenefitValidityDays(Integer groupBenefitValidityDays) { this.groupBenefitValidityDays = groupBenefitValidityDays; }

    public Integer getGroupBenefitScopeType() { return groupBenefitScopeType; }
    public void setGroupBenefitScopeType(Integer groupBenefitScopeType) { this.groupBenefitScopeType = groupBenefitScopeType; }

    public List<Long> getGroupProductIds() { return groupProductIds; }
    public void setGroupProductIds(List<Long> groupProductIds) { this.groupProductIds = groupProductIds; }
}
