package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StoreGoodsOrder implements Serializable {
    private Long sgoId;

    private Long storeId;

    private String barCode;

    private String orderNo;

    private String name;

    private BigDecimal price;

    private String style;

    private Integer num;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public StoreGoodsOrder(Long sgoId, Long storeId, String barCode, String orderNo, String name, BigDecimal price, String style, Integer num, Date createdDate) {
        this.sgoId = sgoId;
        this.storeId = storeId;
        this.barCode = barCode;
        this.orderNo = orderNo;
        this.name = name;
        this.price = price;
        this.style = style;
        this.num = num;
        this.createdDate = createdDate;
    }

    public StoreGoodsOrder() {
        super();
    }

    public Long getSgoId() {
        return sgoId;
    }

    public void setSgoId(Long sgoId) {
        this.sgoId = sgoId;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
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

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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
        sb.append(", sgoId=").append(sgoId);
        sb.append(", storeId=").append(storeId);
        sb.append(", barCode=").append(barCode);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", name=").append(name);
        sb.append(", price=").append(price);
        sb.append(", style=").append(style);
        sb.append(", num=").append(num);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}