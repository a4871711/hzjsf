package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Goods implements Serializable {
    private Long goodsId;

    private Integer categoryId;

    private String name;

    private Integer price;

    private String style;

    private String color;

    private String size;

    private Integer total;

    private Integer monthlySales;

    private String imgUrl;

    private Byte status;

    private Integer freight;

    private String remark;

    private String carouselImgUrl;

    private String remarkImgUrl;

    private Byte deleteStatus;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Goods(Long goodsId, Integer categoryId, String name, Integer price, String style, String color, String size, Integer total, Integer monthlySales, String imgUrl, Byte status, Integer freight, String remark, String carouselImgUrl, String remarkImgUrl, Byte deleteStatus, Date createdDate) {
        this.goodsId = goodsId;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.style = style;
        this.color = color;
        this.size = size;
        this.total = total;
        this.monthlySales = monthlySales;
        this.imgUrl = imgUrl;
        this.status = status;
        this.freight = freight;
        this.remark = remark;
        this.carouselImgUrl = carouselImgUrl;
        this.remarkImgUrl = remarkImgUrl;
        this.deleteStatus = deleteStatus;
        this.createdDate = createdDate;
    }

    public Goods() {
        super();
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style == null ? null : style.trim();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(Integer monthlySales) {
        this.monthlySales = monthlySales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getFreight() {
        return freight;
    }

    public void setFreight(Integer freight) {
        this.freight = freight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getCarouselImgUrl() {
        return carouselImgUrl;
    }

    public void setCarouselImgUrl(String carouselImgUrl) {
        this.carouselImgUrl = carouselImgUrl == null ? null : carouselImgUrl.trim();
    }

    public String getRemarkImgUrl() {
        return remarkImgUrl;
    }

    public void setRemarkImgUrl(String remarkImgUrl) {
        this.remarkImgUrl = remarkImgUrl == null ? null : remarkImgUrl.trim();
    }

    public Byte getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Byte deleteStatus) {
        this.deleteStatus = deleteStatus;
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
        sb.append(", categoryId=").append(categoryId);
        sb.append(", name=").append(name);
        sb.append(", price=").append(price);
        sb.append(", style=").append(style);
        sb.append(", color=").append(color);
        sb.append(", size=").append(size);
        sb.append(", total=").append(total);
        sb.append(", monthlySales=").append(monthlySales);
        sb.append(", imgUrl=").append(imgUrl);
        sb.append(", status=").append(status);
        sb.append(", freight=").append(freight);
        sb.append(", remark=").append(remark);
        sb.append(", carouselImgUrl=").append(carouselImgUrl);
        sb.append(", remarkImgUrl=").append(remarkImgUrl);
        sb.append(", deleteStatus=").append(deleteStatus);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}