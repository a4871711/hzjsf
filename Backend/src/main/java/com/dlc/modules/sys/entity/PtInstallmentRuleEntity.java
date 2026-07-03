package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教商品分期规则表 pt_installment_rule（1:1 挂商品，建表归商品域，账单执行归资金域只读引用）
 *
 * @author claude
 */
public class PtInstallmentRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    /** 是否支持分期：0否 1是 */
    private Integer isEnabled;
    private BigDecimal downPaymentAmount;
    private Integer installmentCount;
    private Integer installmentIntervalMonths;
    /** 逾期是否暂停预约：0否 1是 */
    private Integer overduePauseBooking;
    /** 状态：1启用 0停用 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }

    public BigDecimal getDownPaymentAmount() { return downPaymentAmount; }
    public void setDownPaymentAmount(BigDecimal downPaymentAmount) { this.downPaymentAmount = downPaymentAmount; }

    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }

    public Integer getInstallmentIntervalMonths() { return installmentIntervalMonths; }
    public void setInstallmentIntervalMonths(Integer installmentIntervalMonths) { this.installmentIntervalMonths = installmentIntervalMonths; }

    public Integer getOverduePauseBooking() { return overduePauseBooking; }
    public void setOverduePauseBooking(Integer overduePauseBooking) { this.overduePauseBooking = overduePauseBooking; }

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
}
