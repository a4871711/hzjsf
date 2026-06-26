package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品主分类码表
 */
public class SysGoodsCategoryEntity implements Serializable {
    //商品主分类ID
    private Long goodsCategoryId;
    //主分类图
    private String categoryImg;
    //主分类名称
    private String categoryName;
    //创建时间
    private Date createdDate;

    public Long getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Long goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
