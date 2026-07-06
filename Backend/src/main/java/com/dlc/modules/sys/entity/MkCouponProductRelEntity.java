package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券指定商品关联表 mk_coupon_product_rel（仅 scope_type=2 时有记录，product_id → pt_product.id）
 *
 * @author claude
 */
public class MkCouponProductRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long couponId;
    private Long productId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
