package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class OpenDoorRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long storeId;
    private String longitude;
    private String latitude;
    private Integer distance;
    private int result;
    private String remark;
    private Long deviceStoreId;
    private Date createTime;


//    public OpenDoorRecord(Long id, Long userId, Long storeId, Integer distance, int result, String remark, Date createTime) {
//        this.id = id;
//        this.userId = userId;
//        this.storeId = storeId;
//        this.distance = distance;
//        this.result = result;
//        this.remark = remark;
//        this.createTime = createTime;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getDeviceStoreId() {
        return deviceStoreId;
    }

    public void setDeviceStoreId(Long deviceStoreId) {
        this.deviceStoreId = deviceStoreId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }



}
