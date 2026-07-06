package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * 续费预警记录表 pt_renewal_warning_record（第22步·运营域·续费预警）。
 * 由 api 侧 RenewalWarningScanTask 扫描生成；后台只做跟进/续费/忽略处理。
 * 去重口径：benefit_id + closed_at IS NULL 应用层查重，命中已存在则刷 last_warning_at，否则 INSERT。
 * 时间字段用 String 承接 SQL DATE_FORMAT 结果（与 MkMemberCouponEntity 同一约定）。
 *
 * @author claude
 */
public class PtRenewalWarningRecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ruleId;
    private Long memberId;
    /** 会员私教权益ID */
    private Long benefitId;
    /** 关联教练ID（最近一次预约的教练，可空） */
    private Long coachId;
    private Long storeId;
    /** 关联私教商品ID */
    private Long productId;
    private Integer remainingLessons;
    private Integer remainingDays;
    /** 预警类型：1课时不足 2有效期不足 3同时命中 */
    private Integer warningType;
    /** 跟进状态：0待跟进 1已跟进 2已续费 3已忽略 */
    private Integer followStatus;
    private String firstWarningAt;
    private String lastWarningAt;
    private String closedAt;
    /** 最近跟进备注 */
    private String followRemark;
    private String createdAt;
    private String updatedAt;

    /* ===== 非持久字段（列表联表展示） ===== */
    private String memberName;
    private String memberMobile;
    private String coachName;
    private String storeName;
    private String productName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getBenefitId() { return benefitId; }
    public void setBenefitId(Long benefitId) { this.benefitId = benefitId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getRemainingLessons() { return remainingLessons; }
    public void setRemainingLessons(Integer remainingLessons) { this.remainingLessons = remainingLessons; }

    public Integer getRemainingDays() { return remainingDays; }
    public void setRemainingDays(Integer remainingDays) { this.remainingDays = remainingDays; }

    public Integer getWarningType() { return warningType; }
    public void setWarningType(Integer warningType) { this.warningType = warningType; }

    public Integer getFollowStatus() { return followStatus; }
    public void setFollowStatus(Integer followStatus) { this.followStatus = followStatus; }

    public String getFirstWarningAt() { return firstWarningAt; }
    public void setFirstWarningAt(String firstWarningAt) { this.firstWarningAt = firstWarningAt; }

    public String getLastWarningAt() { return lastWarningAt; }
    public void setLastWarningAt(String lastWarningAt) { this.lastWarningAt = lastWarningAt; }

    public String getClosedAt() { return closedAt; }
    public void setClosedAt(String closedAt) { this.closedAt = closedAt; }

    public String getFollowRemark() { return followRemark; }
    public void setFollowRemark(String followRemark) { this.followRemark = followRemark; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
