package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教商品分期规则(对应表 pt_installment_rule,第20步资金域【只读引用】)。
 * <p>建表归商品域(pt_product_domain.sql),CRUD 随商品保存表单完成(第9步);资金域只读引用本表生成计划与账单,
 * 不建表、不写入。类名与 sys 包同名实体全限定名不同(sys 用于商品编辑写入,api 用于下单只读),互不影响。</p>
 *
 * @author claude
 */
public class PtInstallmentRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 私教商品ID,唯一键 uk_product_id */
    private Long productId;
    /** 是否支持分期:0否 1是 */
    private Integer isEnabled;
    /** 首付金额 */
    private BigDecimal downPaymentAmount;
    /** 分期期数(含首付期,口径见详细文档§5.2) */
    private Integer installmentCount;
    /** 每期间隔月数 */
    private Integer installmentIntervalMonths;
    /** 逾期是否暂停预约:0否 1是(默认) */
    private Integer overduePauseBooking;
    /** 状态:1启用 0停用 */
    private Integer status;
    private Date createdAt;
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

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
