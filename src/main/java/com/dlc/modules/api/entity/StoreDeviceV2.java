package com.dlc.modules.api.entity;


import java.io.Serializable;
import java.util.Date;

public class StoreDeviceV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceSn;
    private String visKey;
    private String visSecret;
    private int source;
    private Long storeId;
    private int state;
    private Long userId;
    private Date createTime;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getVisKey() {
        return visKey;
    }

    public void setVisKey(String visKey) {
        this.visKey = visKey;
    }

    public String getVisSecret() {
        return visSecret;
    }

    public void setVisSecret(String visSecret) {
        this.visSecret = visSecret;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}
