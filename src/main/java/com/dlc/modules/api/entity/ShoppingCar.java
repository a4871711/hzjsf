package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ShoppingCar implements Serializable {
    private Long shoppingCarId;

    private Long pkId;

    private Long userId;

    private Long goodsId;

    private Integer goodsNum;

    private Integer categoryId;

    private String categoryName;

    private String goodsName;

    private Long addressId;//地址Id

    private String goodsImgUrl;

    private BigDecimal price;

    private BigDecimal freight;

    private Date createdDate;

    private String size;//商品尺寸

    private String color;//商品颜色

    private String style;//商品型号

    private static final long serialVersionUID = 1L;

    public ShoppingCar() {
        super();
    }


    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getShoppingCarId() {
        return shoppingCarId;
    }

    public void setShoppingCarId(Long shoppingCarId) {
        this.shoppingCarId = shoppingCarId;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImgUrl() {
        return goodsImgUrl;
    }

    public void setGoodsImgUrl(String goodsImgUrl) {
        this.goodsImgUrl = goodsImgUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    @Override
    public String toString() {
        return "ShoppingCar{" +
                "shoppingCarId=" + shoppingCarId +
                ", pkId=" + pkId +
                ", userId=" + userId +
                ", goodsId=" + goodsId +
                ", goodsNum=" + goodsNum +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", addressId=" + addressId +
                ", goodsImgUrl='" + goodsImgUrl + '\'' +
                ", price=" + price +
                ", freight=" + freight +
                ", createdDate=" + createdDate +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", style='" + style + '\'' +
                '}';
    }
}