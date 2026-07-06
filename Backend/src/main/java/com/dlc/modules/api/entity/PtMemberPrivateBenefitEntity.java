package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员私教权益(对应表 pt_member_private_benefit),预约的唯一权益来源与课时账本。
 * 课时四态恒等式:total_lessons = used_lessons + frozen_lessons + remaining_lessons,任何路径不可破。
 *
 * @author claude
 */
public class PtMemberPrivateBenefitEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 权益编号,PE+yyyyMMddHHmmss+随机,唯一 */
    private String benefitNo;
    /** 来源私教订单ID(一单一权益,幂等键) */
    private Long orderId;
    private Long memberId;
    private Long productId;
    /** 购买门店ID */
    private Long storeId;
    /** 总课时 */
    private Integer totalLessons;
    /** 已用课时 */
    private Integer usedLessons;
    /** 冻结课时(已预约未完成) */
    private Integer frozenLessons;
    /** 剩余可用课时 */
    private Integer remainingLessons;
    /** 生效时间(支付成功) */
    private Date effectiveAt;
    /** 到期时间,长期(validity_days=-1)为 NULL */
    private Date expireAt;
    /** 权益状态:1生效中 2已用完 3已过期 4已退款 */
    private Integer status;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBenefitNo() { return benefitNo; }
    public void setBenefitNo(String benefitNo) { this.benefitNo = benefitNo; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Integer getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }

    public Integer getUsedLessons() { return usedLessons; }
    public void setUsedLessons(Integer usedLessons) { this.usedLessons = usedLessons; }

    public Integer getFrozenLessons() { return frozenLessons; }
    public void setFrozenLessons(Integer frozenLessons) { this.frozenLessons = frozenLessons; }

    public Integer getRemainingLessons() { return remainingLessons; }
    public void setRemainingLessons(Integer remainingLessons) { this.remainingLessons = remainingLessons; }

    public Date getEffectiveAt() { return effectiveAt; }
    public void setEffectiveAt(Date effectiveAt) { this.effectiveAt = effectiveAt; }

    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
