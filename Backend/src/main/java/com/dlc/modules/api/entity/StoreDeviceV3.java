package com.dlc.modules.api.entity;


import java.io.Serializable;
import java.util.Date;

public class StoreDeviceV3 implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceSn;
    private String visKey;
    private String visSecret;
    private int source;
    private int storeId;
    private Date createTime;

//    public StoreDeviceV2(Integer id, String deviceId, String deviceName, String deviceSn, String visKey, String visSecret, int source, int storeId, Date createTime) {
//        this.id = id;
//        this.deviceId = deviceId;
//        this.deviceName = deviceName;
//        this.deviceSn = deviceSn;
//        this.visKey = visKey;
//        this.visSecret = visSecret;
//        this.source = source;
//        this.storeId = storeId;
//        this.createTime = createTime;
//    }


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

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


}
