package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券可用门店关联表 mk_coupon_store_rel
 *
 * @author claude
 */
public class MkCouponStoreRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long couponId;
    private Long storeId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
