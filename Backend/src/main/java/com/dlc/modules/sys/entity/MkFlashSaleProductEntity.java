package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀活动-商品子表 mk_flash_sale_product。一活动多商品，每商品独立秒杀价/库存/已售。
 * product_id 含义随活动 biz_type：2会员卡→fit_card.fitCardId，3权益卡→vip_benefit_card.vip_card_id。
 *
 * @author claude
 */
public class MkFlashSaleProductEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long activityId;
    private Long productId;
    private BigDecimal flashSalePrice;
    private Integer activityStock;
    private Integer soldCount;

    /* ===== 非持久字段（展示/回显用）===== */
    private String productName;
    private BigDecimal originPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getFlashSalePrice() { return flashSalePrice; }
    public void setFlashSalePrice(BigDecimal flashSalePrice) { this.flashSalePrice = flashSalePrice; }

    public Integer getActivityStock() { return activityStock; }
    public void setActivityStock(Integer activityStock) { this.activityStock = activityStock; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }
}
