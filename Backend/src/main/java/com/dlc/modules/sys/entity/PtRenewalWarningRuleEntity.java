package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 续费预警规则表 pt_renewal_warning_rule（第22步·运营域·续费预警）。
 * 提醒对象固定为教练（无字段，扫描时写死）。剩余课时预警 / 剩余天数预警至少启用一个。
 * 表单一次性提交主表 + 适用门店关联（pt_renewal_warning_rule_store_rel，全删全插，空=全部门店）。
 * 非持久字段直接挂本实体（与 MkCouponEntity 同一约定）。
 *
 * @author claude
 */
public class PtRenewalWarningRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruleName;
    /** 是否启用剩余课时预警：0否 1是 */
    private Integer lessonWarningEnabled;
    /** 剩余课时阈值 */
    private Integer lessonThreshold;
    /** 是否启用剩余天数预警：0否 1是 */
    private Integer daysWarningEnabled;
    /** 剩余天数阈值 */
    private Integer daysThreshold;
    /** 规则状态：1启用 0停用 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    /* ===== 非持久字段 ===== */
    /** 适用门店ID（表单提交/info回填；空=全部门店，不插 rel） */
    private List<Long> storeIds;
    /** 适用门店名（列表展示，group_concat；空=全部门店） */
    private String storeNames;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getLessonWarningEnabled() { return lessonWarningEnabled; }
    public void setLessonWarningEnabled(Integer lessonWarningEnabled) { this.lessonWarningEnabled = lessonWarningEnabled; }

    public Integer getLessonThreshold() { return lessonThreshold; }
    public void setLessonThreshold(Integer lessonThreshold) { this.lessonThreshold = lessonThreshold; }

    public Integer getDaysWarningEnabled() { return daysWarningEnabled; }
    public void setDaysWarningEnabled(Integer daysWarningEnabled) { this.daysWarningEnabled = daysWarningEnabled; }

    public Integer getDaysThreshold() { return daysThreshold; }
    public void setDaysThreshold(Integer daysThreshold) { this.daysThreshold = daysThreshold; }

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

    public String getStoreNames() { return storeNames; }
    public void setStoreNames(String storeNames) { this.storeNames = storeNames; }
}
