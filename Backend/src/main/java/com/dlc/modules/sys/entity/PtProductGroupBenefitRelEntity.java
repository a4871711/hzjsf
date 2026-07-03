package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 私教商品附赠团课指定商品关联表 pt_product_group_benefit_rel
 *
 * @author claude
 */
public class PtProductGroupBenefitRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long benefitId;
    private Long groupProductId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBenefitId() { return benefitId; }
    public void setBenefitId(Long benefitId) { this.benefitId = benefitId; }

    public Long getGroupProductId() { return groupProductId; }
    public void setGroupProductId(Long groupProductId) { this.groupProductId = groupProductId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
