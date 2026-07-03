package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员附赠团课权益表 pt_member_group_benefit（本域只读；写入分散在交易域回调/团课预约/运营域任务）
 *
 * @author claude
 */
public class PtMemberGroupBenefitEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long memberId;
    private Long sourceOrderId;
    private Long sourceProductId;
    private Integer giftCount;
    private Integer usedCount;
    private Integer remainingCount;
    private Date effectiveTime;
    private Date expireTime;
    /** 状态：1生效中 2已用完 3已过期 4已回收 */
    private Integer status;
    private Date createdAt;
    private Date updatedAt;

    /** 非持久：展示用 */
    private String memberName;
    private String memberMobile;
    private String sourceProductName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getSourceOrderId() { return sourceOrderId; }
    public void setSourceOrderId(Long sourceOrderId) { this.sourceOrderId = sourceOrderId; }

    public Long getSourceProductId() { return sourceProductId; }
    public void setSourceProductId(Long sourceProductId) { this.sourceProductId = sourceProductId; }

    public Integer getGiftCount() { return giftCount; }
    public void setGiftCount(Integer giftCount) { this.giftCount = giftCount; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Integer getRemainingCount() { return remainingCount; }
    public void setRemainingCount(Integer remainingCount) { this.remainingCount = remainingCount; }

    public Date getEffectiveTime() { return effectiveTime; }
    public void setEffectiveTime(Date effectiveTime) { this.effectiveTime = effectiveTime; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public String getSourceProductName() { return sourceProductName; }
    public void setSourceProductName(String sourceProductName) { this.sourceProductName = sourceProductName; }
}
