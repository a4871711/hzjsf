package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class GoodsCategoryDetail implements Serializable {
    private Long id;

    private String nextCategoryName;

    private String nextCategoryImg;

    private Date createdDate;

    private Long goodsCategoryId;

    private static final long serialVersionUID = 1L;

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

    public Long getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Long goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    @Override
    public String toString() {
        return "GoodsCategoryDetail{" +
                "id=" + id +
                ", nextCategoryName='" + nextCategoryName + '\'' +
                ", nextCategoryImg='" + nextCategoryImg + '\'' +
                ", createdDate=" + createdDate +
                ", goodsCategoryId=" + goodsCategoryId +
                '}';
    }


}