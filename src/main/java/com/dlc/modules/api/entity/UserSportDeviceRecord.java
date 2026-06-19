package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserSportDeviceRecord implements Serializable {
    private Long id;

    private Long userId;

    private String deviceId;

    private String deviceName;

    private BigDecimal calorie;

    private BigDecimal distance;

    private BigDecimal avgSpeed;
    private Integer avgGradient;
    private Integer bpm;
    private BigDecimal stepSize;
    private Integer stepCount;

    private Date startTime;

    private Date createTime;

    private Integer sportTime;

    private static final long serialVersionUID = 1L;

    public UserSportDeviceRecord(Long id, Long userId, String deviceId, String deviceName, BigDecimal calorie, BigDecimal distance,
                                 BigDecimal avgSpeed, Integer avgGradient, Integer bpm, BigDecimal stepSize, Integer stepCount,
                                 Date startTime, Date createTime) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.calorie = calorie;
        this.distance = distance;
        this.avgSpeed = avgSpeed;
        this.avgGradient = avgGradient;
        this.bpm = bpm;
        this.stepSize = stepSize;
        this.stepCount = stepCount;
        this.startTime = startTime;
        this.createTime = createTime;
    }

    public UserSportDeviceRecord() {
        super();
    }

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName == null ? null : deviceName.trim();
    }

    public BigDecimal getCalorie() {
        return calorie;
    }

    public void setCalorie(BigDecimal calorie) {
        this.calorie = calorie;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", calorie=").append(calorie);
        sb.append(", distance=").append(distance);
        sb.append(", startTime=").append(startTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public Integer getSportTime() {
        return sportTime;
    }

    public void setSportTime(Integer sportTime) {
        this.sportTime = sportTime;
    }



    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getAvgGradient() {
        return avgGradient;
    }

    public void setAvgGradient(Integer avgGradient) {
        this.avgGradient = avgGradient;
    }

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public BigDecimal getStepSize() {
        return stepSize;
    }

    public void setStepSize(BigDecimal stepSize) {
        this.stepSize = stepSize;
    }

    public Integer getStepCount() {
        return stepCount;
    }

    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }

}