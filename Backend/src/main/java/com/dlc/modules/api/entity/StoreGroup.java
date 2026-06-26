package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class StoreGroup implements Serializable {
    private Long storeGroupId;

    private Long storeId;

    private String groupName;

    private String groupAddr;

    private String groupImg;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public StoreGroup(Long storeGroupId, Long storeId, String groupName, String groupAddr, String groupImg, Date createdDate) {
        this.storeGroupId = storeGroupId;
        this.storeId = storeId;
        this.groupName = groupName;
        this.groupAddr = groupAddr;
        this.groupImg = groupImg;
        this.createdDate = createdDate;
    }

    public StoreGroup() {
        super();
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
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr == null ? null : groupAddr.trim();
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg == null ? null : groupImg.trim();
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
        sb.append(", storeGroupId=").append(storeGroupId);
        sb.append(", storeId=").append(storeId);
        sb.append(", groupName=").append(groupName);
        sb.append(", groupAddr=").append(groupAddr);
        sb.append(", groupImg=").append(groupImg);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}