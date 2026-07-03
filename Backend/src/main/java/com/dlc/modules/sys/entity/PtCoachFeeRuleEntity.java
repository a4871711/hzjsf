package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 教练课时费/分成规则表 pt_coach_fee_rule（扩展版，全系统唯一建表口径）
 *
 * @author claude
 */
public class PtCoachFeeRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long coachId;
    /** 私教商品ID，0=不限 */
    private Long productId;
    /** 适用门店ID，0=全部门店 */
    private Long storeId;
    private String ruleName;
    /** 规则类型：1课时费 2销售提成 */
    private Integer ruleType;
    /** 单次课时费（rule_type=1） */
    private BigDecimal lessonFee;
    /** 销售提成比例%（rule_type=2） */
    private BigDecimal commissionRate;
    /** 生效时间，NULL=立即生效 */
    private Date effectiveTime;
    /** 状态：1启用 0停用 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    /** 非持久字段：保存时按门店×课程笛卡尔展开 */
    private List<Long> storeIds;
    /** 非持久字段：保存时按门店×课程笛卡尔展开 */
    private List<Long> productIds;
    /** 非持久字段：列表展示用教练名 */
    private String coachName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getRuleType() { return ruleType; }
    public void setRuleType(Integer ruleType) { this.ruleType = ruleType; }

    public BigDecimal getLessonFee() { return lessonFee; }
    public void setLessonFee(BigDecimal lessonFee) { this.lessonFee = lessonFee; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public Date getEffectiveTime() { return effectiveTime; }
    public void setEffectiveTime(Date effectiveTime) { this.effectiveTime = effectiveTime; }

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

    public List<Long> getStoreIds() { return storeIds; }
    public void setStoreIds(List<Long> storeIds) { this.storeIds = storeIds; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }
}
