package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Store implements Serializable {
    private Long storeId;

    private String storeName;

    private Long storeAddrId;

    private String storeImgUrl;

    private String storeLinkUrl;

    private Integer currentNum;

    private String storeArea;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getStoreAddrId() {
        return storeAddrId;
    }

    public void setStoreAddrId(Long storeAddrId) {
        this.storeAddrId = storeAddrId;
    }

    public String getStoreImgUrl() {
        return storeImgUrl;
    }

    public void setStoreImgUrl(String storeImgUrl) {
        this.storeImgUrl = storeImgUrl;
    }

    public String getStoreLinkUrl() {
        return storeLinkUrl;
    }

    public void setStoreLinkUrl(String storeLinkUrl) {
        this.storeLinkUrl = storeLinkUrl;
    }

    public Integer getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(Integer currentNum) {
        this.currentNum = currentNum;
    }

    public String getStoreArea() {
        return storeArea;
    }

    public void setStoreArea(String storeArea) {
        this.storeArea = storeArea;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", storeAddrId=" + storeAddrId +
                ", storeImgUrl='" + storeImgUrl + '\'' +
                ", storeLinkUrl='" + storeLinkUrl + '\'' +
                ", currentNum=" + currentNum +
                ", storeArea='" + storeArea + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

    public Store(Long storeId, String storeName, Long storeAddrId, String storeImgUrl, String storeLinkUrl, Integer currentNum, String storeArea, Date createdDate) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeAddrId = storeAddrId;
        this.storeImgUrl = storeImgUrl;
        this.storeLinkUrl = storeLinkUrl;
        this.currentNum = currentNum;
        this.storeArea = storeArea;
        this.createdDate = createdDate;
    }
}