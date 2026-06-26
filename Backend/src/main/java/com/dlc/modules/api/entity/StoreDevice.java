package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class StoreDevice implements Serializable {
    private Long sdId;

    private Long storeId;

    private String deviceName;

    private String deviceNo;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public StoreDevice(Long sdId, Long storeId, String deviceName, String deviceNo, Date createTime) {
        this.sdId = sdId;
        this.storeId = storeId;
        this.deviceName = deviceName;
        this.deviceNo = deviceNo;
        this.createTime = createTime;
    }

    public StoreDevice() {
        super();
    }

    public Long getSdId() {
        return sdId;
    }

    public void setSdId(Long sdId) {
        this.sdId = sdId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName == null ? null : deviceName.trim();
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo == null ? null : deviceNo.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", sdId=").append(sdId);
        sb.append(", storeId=").append(storeId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", deviceNo=").append(deviceNo);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}