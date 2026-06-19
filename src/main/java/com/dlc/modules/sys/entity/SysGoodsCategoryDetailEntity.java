package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 二级分类
 */
public class SysGoodsCategoryDetailEntity implements Serializable{
    //二级分类ID
    private Long id;
    //二级分类名称
    private String nextCategoryName;
    //二级分类图
    private String nextCategoryImg;
    //创建时间
    private Date createdDate;
    //主分类id
    private String goodsCategoryId;
    //一级分类名称
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNextCategoryName() {
        return nextCategoryName;
    }

    public void setNextCategoryName(String nextCategoryName) {
        this.nextCategoryName = nextCategoryName;
    }

    public String getNextCategoryImg() {
        return nextCategoryImg;
    }

    public void setNextCategoryImg(String nextCategoryImg) {
        this.nextCategoryImg = nextCategoryImg;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(String goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }
}
