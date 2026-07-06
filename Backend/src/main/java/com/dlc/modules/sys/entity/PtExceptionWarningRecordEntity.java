package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * 异常预警记录表 pt_exception_warning_record（第22步·运营域·异常预警）。
 * 由 api 侧 ExceptionWarningScanTask 扫描生成；本期仅展示，不做处理态流转（§18.4，无 follow_status）。
 * 去重：uk_exc_warn_dedup(rule_id, member_id, warning_type, period_start, period_end) + ON DUPLICATE KEY UPDATE。
 * 时间/日期字段用 String 承接 SQL DATE_FORMAT 结果。
 *
 * @author claude
 */
public class PtExceptionWarningRecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ruleId;
    /** 预警类型：1频繁取消预约 2课时消耗异常 */
    private Integer warningType;
    private Long memberId;
    private Long coachId;
    private Long storeId;
    private String periodStart;
    private String periodEnd;
    private Integer triggerValue;
    private String triggerDesc;
    private String warningTime;

    /* ===== 非持久字段（列表联表展示） ===== */
    private String memberName;
    private String memberMobile;
    private String coachName;
    private String storeName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }

    public Integer getWarningType() { return warningType; }
    public void setWarningType(Integer warningType) { this.warningType = warningType; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getPeriodStart() { return periodStart; }
    public void setPeriodStart(String periodStart) { this.periodStart = periodStart; }

    public String getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(String periodEnd) { this.periodEnd = periodEnd; }

    public Integer getTriggerValue() { return triggerValue; }
    public void setTriggerValue(Integer triggerValue) { this.triggerValue = triggerValue; }

    public String getTriggerDesc() { return triggerDesc; }
    public void setTriggerDesc(String triggerDesc) { this.triggerDesc = triggerDesc; }

    public String getWarningTime() { return warningTime; }
    public void setWarningTime(String warningTime) { this.warningTime = warningTime; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}
