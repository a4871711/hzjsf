package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 私教商品指定教练关联表 pt_product_coach_rel（空=不限教练）
 *
 * @author claude
 */
public class PtProductCoachRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private Long coachId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
