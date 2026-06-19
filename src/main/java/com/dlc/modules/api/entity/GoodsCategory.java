package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class GoodsCategory implements Serializable {
    private Long goodsCategoryId;

    private String categoryName;

    private String categoryImg;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Long goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public GoodsCategory() {
        super();
    }

    public GoodsCategory(Long goodsCategoryId, String categoryName, String categoryImg, Date createdDate) {
        this.goodsCategoryId = goodsCategoryId;
        this.categoryName = categoryName;
        this.categoryImg = categoryImg;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "GoodsCategory{" +
                "goodsCategoryId=" + goodsCategoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryImg='" + categoryImg + '\'' +
                ", serialVersionUID='" + serialVersionUID + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}