package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 异常预警规则表 pt_exception_warning_rule（第22步·运营域·异常预警）。
 * 两类：warning_type 1频繁取消预约 / 2课时消耗异常。
 * applicable_store_ids 为 JSON 存（NULL=全部门店）；非持久 applicableStoreIds 数组承接前端多选/回显。
 *
 * @author claude
 */
public class PtExceptionWarningRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruleName;
    /** 预警类型：1频繁取消预约 2课时消耗异常 */
    private Integer warningType;
    /** 统计周期天数 */
    private Integer periodDays;
    /** 触发阈值 */
    private Integer triggerThreshold;
    /** 适用门店JSON串，NULL=全部门店（持久列，与 applicableStoreIds 二者转换） */
    private String applicableStoreIds;
    /** 规则状态：1启用 0停用 */
    private Integer status;
    private String remark;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    /* ===== 非持久字段 ===== */
    /** 适用门店ID数组（表单提交/info回填；空=全部门店存 NULL） */
    private List<Long> applicableStoreIdList;
    /** 适用门店名（列表展示，group_concat；空=全部门店） */
    private String storeNames;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getWarningType() { return warningType; }
    public void setWarningType(Integer warningType) { this.warningType = warningType; }

    public Integer getPeriodDays() { return periodDays; }
    public void setPeriodDays(Integer periodDays) { this.periodDays = periodDays; }

    public Integer getTriggerThreshold() { return triggerThreshold; }
    public void setTriggerThreshold(Integer triggerThreshold) { this.triggerThreshold = triggerThreshold; }

    public String getApplicableStoreIds() { return applicableStoreIds; }
    public void setApplicableStoreIds(String applicableStoreIds) { this.applicableStoreIds = applicableStoreIds; }

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

    public List<Long> getApplicableStoreIdList() { return applicableStoreIdList; }
    public void setApplicableStoreIdList(List<Long> applicableStoreIdList) { this.applicableStoreIdList = applicableStoreIdList; }

    public String getStoreNames() { return storeNames; }
    public void setStoreNames(String storeNames) { this.storeNames = storeNames; }
}
