package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * lingkangming
 * 门店-社群表
 */
public class SysStoreGroupEntity implements Serializable {
    //社群ID
    private Long storeGroupId;
    //门店ID
    private Long storeId;
    //社群名称
    private String groupName;
    //社群详情图
    private String groupImg;
    //社群地址ID（相当于和门店地址一样）
    private Long storeAddrId;
    //社群位置
    private String groupAddr;
    //创建时间
    private Date createdDate;
    //门店名称
    private String storeName;
    //门店电话
    private String storePhone;

    public SysStoreGroupEntity() {
        super();
    }

    public SysStoreGroupEntity(Long storeGroupId, Long storeId, String groupName, String groupImg, Long storeAddrId, String groupAddr, Date createdDate, String storeName, String storePhone) {
        this.storeGroupId = storeGroupId;
        this.storeId = storeId;
        this.groupName = groupName;
        this.groupImg = groupImg;
        this.storeAddrId = storeAddrId;
        this.groupAddr = groupAddr;
        this.createdDate = createdDate;
        this.storeName = storeName;
        this.storePhone = storePhone;
    }

    public Long getStoreGroupId() {
        return storeGroupId;
    }

    public void setStoreGroupId(Long storeGroupId) {
        this.storeGroupId = storeGroupId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public Long getStoreAddrId() {
        return storeAddrId;
    }

    public void setStoreAddrId(Long storeAddrId) {
        this.storeAddrId = storeAddrId;
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }
}
