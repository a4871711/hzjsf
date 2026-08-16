package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 教练包月课程提成配置。
 * 一名教练可配置多个包月商品，每个商品独立设置标准课节、提成比例和未达标单节提成。
 */
public class PtCoachMonthlyCommissionRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long coachId;
    private Long productId;
    private Integer standardLessonCount;
    private BigDecimal commissionRate;
    private BigDecimal belowStandardLessonFee;
    private Date createdAt;
    private Date updatedAt;

    /** 非持久字段：教练表单回显课程名称。 */
    private String productName;
    /** 非持久字段：课程当前售价，仅用于配置时辅助识别。 */
    private BigDecimal salePrice;
    /** 非持久字段：课程总课时。 */
    private Integer productLessonCount;
    /** 非持久字段：课程上架状态。 */
    private Integer listingStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getStandardLessonCount() { return standardLessonCount; }
    public void setStandardLessonCount(Integer standardLessonCount) { this.standardLessonCount = standardLessonCount; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getBelowStandardLessonFee() { return belowStandardLessonFee; }
    public void setBelowStandardLessonFee(BigDecimal belowStandardLessonFee) { this.belowStandardLessonFee = belowStandardLessonFee; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getProductLessonCount() { return productLessonCount; }
    public void setProductLessonCount(Integer productLessonCount) { this.productLessonCount = productLessonCount; }

    public Integer getListingStatus() { return listingStatus; }
    public void setListingStatus(Integer listingStatus) { this.listingStatus = listingStatus; }
}
