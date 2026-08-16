package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教商品权益价配置。一条记录表示某个 VIP 权益卡购买某私教商品时的专属价格。
 */
public class PtProductBenefitPriceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private Long vipCardId;
    private String cardName;
    private BigDecimal benefitPrice;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getVipCardId() { return vipCardId; }
    public void setVipCardId(Long vipCardId) { this.vipCardId = vipCardId; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public BigDecimal getBenefitPrice() { return benefitPrice; }
    public void setBenefitPrice(BigDecimal benefitPrice) { this.benefitPrice = benefitPrice; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
