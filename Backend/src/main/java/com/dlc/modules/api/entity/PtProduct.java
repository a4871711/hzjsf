package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 私教商品（会员端浏览用只读投影，字段为对外展示子集，不含后台管理字段）。
 * 说明：sale_price/member_price/new_user_price 均原样返回，"按身份最终成交价"的权威计算在
 * 交易域下单试算(quote)接口完成，本接口不做身份推断，避免臆造未在需求中明确的识别规则。
 *
 * @author claude
 */
public class PtProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String productNo;
    private String productName;
    private String productSubtitle;
    private Long productTypeId;
    private String typeName;
    /** 服务类型：1一对一 2一对多 */
    private Integer serviceType;
    private String categoryName;
    private String coverUrl;
    private String productIntro;
    /** 富文本详情，仅详情接口返回 */
    private String productDetail;
    private String targetDesc;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private BigDecimal memberPrice;
    private BigDecimal newUserPrice;
    private Integer lessonCount;
    private Integer durationMinutes;
    /** 有效期天数；-1 表示长期 */
    private Integer validityDays;
    /** 退款方式：1支持 2不支持 3人工审核 */
    private Integer refundType;
    private String refundRule;
    private Integer purchaseLimit;
    private Integer saleStock;
    private Integer soldCount;
    private Integer bookingGapMinutes;
    private Integer bookingCapacity;
    private Integer latestBookingHours;
    private Integer latestFreeCancelHours;
    private Integer noShowDeduct;
    private Integer coachConfirmRequired;
    private Integer sortNo;
    private Integer recommendPrivate;
    private Integer recommendHome;
    /** 是否支持分期：0否 1是 */
    private Integer installmentEnabled;
    /** 是否赠送团课权益：0否 1是 */
    private Integer groupBenefitEnabled;
    private Integer groupBenefitGiftCount;
    private Integer groupBenefitValidityDays;

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

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

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

    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }

    public Integer getRecommendPrivate() { return recommendPrivate; }
    public void setRecommendPrivate(Integer recommendPrivate) { this.recommendPrivate = recommendPrivate; }

    public Integer getRecommendHome() { return recommendHome; }
    public void setRecommendHome(Integer recommendHome) { this.recommendHome = recommendHome; }

    public Integer getInstallmentEnabled() { return installmentEnabled; }
    public void setInstallmentEnabled(Integer installmentEnabled) { this.installmentEnabled = installmentEnabled; }

    public Integer getGroupBenefitEnabled() { return groupBenefitEnabled; }
    public void setGroupBenefitEnabled(Integer groupBenefitEnabled) { this.groupBenefitEnabled = groupBenefitEnabled; }

    public Integer getGroupBenefitGiftCount() { return groupBenefitGiftCount; }
    public void setGroupBenefitGiftCount(Integer groupBenefitGiftCount) { this.groupBenefitGiftCount = groupBenefitGiftCount; }

    public Integer getGroupBenefitValidityDays() { return groupBenefitValidityDays; }
    public void setGroupBenefitValidityDays(Integer groupBenefitValidityDays) { this.groupBenefitValidityDays = groupBenefitValidityDays; }
}
