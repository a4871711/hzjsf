package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * 团课转私教跟进记录表 pt_group_to_private_follow（第22步·运营域·团课转私教）。
 * 名单的多条跟进流水（lead 1:N follow）。
 * 时间字段用 String 承接 SQL DATE_FORMAT 结果。
 *
 * @author claude
 */
public class PtGroupToPrivateFollowEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long leadId;
    private Long memberId;
    /** 跟进状态：0待跟进 1已跟进 2已转化 3已放弃 */
    private Integer followStatus;
    private String followRemark;
    private Long operatorId;
    private String createdAt;

    /* ===== 非持久字段（列表展示） ===== */
    private String operatorName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Integer getFollowStatus() { return followStatus; }
    public void setFollowStatus(Integer followStatus) { this.followStatus = followStatus; }

    public String getFollowRemark() { return followRemark; }
    public void setFollowRemark(String followRemark) { this.followRemark = followRemark; }

    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
