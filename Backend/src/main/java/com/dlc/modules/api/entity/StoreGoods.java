package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StoreGoods implements Serializable {
    private Long goodsId;

    private Long storeId;

    private String barCode;

    private String name;

    private BigDecimal price;

    private String style;

    private Integer total;

    private String remark;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public StoreGoods(Long goodsId, Long storeId, String barCode, String name, BigDecimal price, String style, Integer total, String remark, Date createdDate) {
        this.goodsId = goodsId;
        this.storeId = storeId;
        this.barCode = barCode;
        this.name = name;
        this.price = price;
        this.style = style;
        this.total = total;
        this.remark = remark;
        this.createdDate = createdDate;
    }

    public StoreGoods() {
        super();
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style == null ? null : style.trim();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", goodsId=").append(goodsId);
        sb.append(", storeId=").append(storeId);
        sb.append(", barCode=").append(barCode);
        sb.append(", name=").append(name);
        sb.append(", price=").append(price);
        sb.append(", style=").append(style);
        sb.append(", total=").append(total);
        sb.append(", remark=").append(remark);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}