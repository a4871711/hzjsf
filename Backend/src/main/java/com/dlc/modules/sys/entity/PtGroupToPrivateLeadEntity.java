package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 团课转私教转化名单表 pt_group_to_private_lead（第22步·运营域·团课转私教）。
 * 命中规则的高意向会员名单；一会员一条（uk_pt_group_to_private_lead_member_id）。
 * 扫描 upsert 不覆盖人工 follow_status / experience_coupon_status。
 * 时间字段用 String 承接 SQL DATE_FORMAT 结果。
 *
 * @author claude
 */
public class PtGroupToPrivateLeadEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long memberId;
    private Long storeId;
    /** 命中规则ID */
    private Long ruleId;
    private Integer attendanceCount;
    private Integer purchaseCount;
    private String intentionReason;
    /** 体验券状态：0未发放 1已发放 2已使用 */
    private Integer experienceCouponStatus;
    /** 跟进状态：0待跟进 1已跟进 2已转化 3已放弃 */
    private Integer followStatus;
    private Long followBy;
    private String lastFollowTime;
    private String createdAt;
    private String updatedAt;

    /* ===== 非持久字段（列表联表展示） ===== */
    private String memberName;
    private String memberMobile;
    private String storeName;
    /** 跟进人名（sys_user） */
    private String followByName;
    /** 跟进流水（详情用，lead 1:N follow） */
    private List<PtGroupToPrivateFollowEntity> followList;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }

    public Integer getAttendanceCount() { return attendanceCount; }
    public void setAttendanceCount(Integer attendanceCount) { this.attendanceCount = attendanceCount; }

    public Integer getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Integer purchaseCount) { this.purchaseCount = purchaseCount; }

    public String getIntentionReason() { return intentionReason; }
    public void setIntentionReason(String intentionReason) { this.intentionReason = intentionReason; }

    public Integer getExperienceCouponStatus() { return experienceCouponStatus; }
    public void setExperienceCouponStatus(Integer experienceCouponStatus) { this.experienceCouponStatus = experienceCouponStatus; }

    public Integer getFollowStatus() { return followStatus; }
    public void setFollowStatus(Integer followStatus) { this.followStatus = followStatus; }

    public Long getFollowBy() { return followBy; }
    public void setFollowBy(Long followBy) { this.followBy = followBy; }

    public String getLastFollowTime() { return lastFollowTime; }
    public void setLastFollowTime(String lastFollowTime) { this.lastFollowTime = lastFollowTime; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getFollowByName() { return followByName; }
    public void setFollowByName(String followByName) { this.followByName = followByName; }

    public List<PtGroupToPrivateFollowEntity> getFollowList() { return followList; }
    public void setFollowList(List<PtGroupToPrivateFollowEntity> followList) { this.followList = followList; }
}
