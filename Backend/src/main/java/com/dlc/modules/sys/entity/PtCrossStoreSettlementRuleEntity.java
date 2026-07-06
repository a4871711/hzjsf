package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 跨店结算规则表 pt_cross_store_settlement_rule（全系统单套全局规则）。
 * 本期仅做规则配置与口径定义，不自动结算、不出对账单；收入报表不按本规则拆分（第23步 §17.1/§17.3）。
 *
 * @author claude
 */
public class PtCrossStoreSettlementRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 规则名称 */
    private String ruleName;
    /** 是否启用跨店结算：0否 1是 */
    private Integer crossStoreEnabled;
    /** 收入归属方式：1购买门店 2上课门店 3按比例分成 */
    private Integer incomeOwnerType;
    /** 购买门店分成比例 */
    private BigDecimal buyStoreRatio;
    /** 上课门店分成比例 */
    private BigDecimal lessonStoreRatio;
    /** 教练课时费归属：1授课教练（固定） */
    private Integer coachFeeOwnerType;
    /** 规则状态：1启用 0停用 */
    private Integer status;
    /** 备注 */
    private String remark;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getCrossStoreEnabled() { return crossStoreEnabled; }
    public void setCrossStoreEnabled(Integer crossStoreEnabled) { this.crossStoreEnabled = crossStoreEnabled; }

    public Integer getIncomeOwnerType() { return incomeOwnerType; }
    public void setIncomeOwnerType(Integer incomeOwnerType) { this.incomeOwnerType = incomeOwnerType; }

    public BigDecimal getBuyStoreRatio() { return buyStoreRatio; }
    public void setBuyStoreRatio(BigDecimal buyStoreRatio) { this.buyStoreRatio = buyStoreRatio; }

    public BigDecimal getLessonStoreRatio() { return lessonStoreRatio; }
    public void setLessonStoreRatio(BigDecimal lessonStoreRatio) { this.lessonStoreRatio = lessonStoreRatio; }

    public Integer getCoachFeeOwnerType() { return coachFeeOwnerType; }
    public void setCoachFeeOwnerType(Integer coachFeeOwnerType) { this.coachFeeOwnerType = coachFeeOwnerType; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
