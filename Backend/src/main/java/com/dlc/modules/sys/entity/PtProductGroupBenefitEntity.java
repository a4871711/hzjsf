package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 私教商品附赠团课权益规则表 pt_product_group_benefit（1:1 挂商品）
 *
 * @author claude
 */
public class PtProductGroupBenefitEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    /** 是否赠送团课权益：0否 1是 */
    private Integer isEnabled;
    private Integer giftCount;
    private Integer validityDays;
    /** 适用范围：1全部团课 2指定团课商品 */
    private Integer scopeType;
    /** 状态：1启用 0停用 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    /** 非持久：指定团课商品ID集合（scope_type=2 时提交） */
    private List<Long> groupProductIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }

    public Integer getGiftCount() { return giftCount; }
    public void setGiftCount(Integer giftCount) { this.giftCount = giftCount; }

    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }

    public Integer getScopeType() { return scopeType; }
    public void setScopeType(Integer scopeType) { this.scopeType = scopeType; }

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

    public List<Long> getGroupProductIds() { return groupProductIds; }
    public void setGroupProductIds(List<Long> groupProductIds) { this.groupProductIds = groupProductIds; }
}
