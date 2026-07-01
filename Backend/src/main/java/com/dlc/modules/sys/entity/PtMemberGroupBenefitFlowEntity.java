package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员附赠团课权益流水表 pt_member_group_benefit_flow（本域只读）
 *
 * @author claude
 */
public class PtMemberGroupBenefitFlowEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long benefitId;
    private Long memberId;
    /** 流水类型：1发放 2使用 3回收 4过期 */
    private Integer flowType;
    private Integer changeCount;
    private Integer beforeCount;
    private Integer afterCount;
    /** 关联业务类型：1私教订单 2团课预约 3退款 */
    private Integer bizType;
    private Long bizId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBenefitId() { return benefitId; }
    public void setBenefitId(Long benefitId) { this.benefitId = benefitId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Integer getFlowType() { return flowType; }
    public void setFlowType(Integer flowType) { this.flowType = flowType; }

    public Integer getChangeCount() { return changeCount; }
    public void setChangeCount(Integer changeCount) { this.changeCount = changeCount; }

    public Integer getBeforeCount() { return beforeCount; }
    public void setBeforeCount(Integer beforeCount) { this.beforeCount = beforeCount; }

    public Integer getAfterCount() { return afterCount; }
    public void setAfterCount(Integer afterCount) { this.afterCount = afterCount; }

    public Integer getBizType() { return bizType; }
    public void setBizType(Integer bizType) { this.bizType = bizType; }

    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
