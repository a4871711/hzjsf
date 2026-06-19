package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class CoachAppointment implements Serializable {
    private Long appointId;

    private Long coachId;

    private String appDate;

    private String appTime;

    private Byte appStatus;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public CoachAppointment(Long appointId, Long coachId, String appDate, String appTime, Byte appStatus, Date createdDate) {
        this.appointId = appointId;
        this.coachId = coachId;
        this.appDate = appDate;
        this.appTime = appTime;
        this.appStatus = appStatus;
        this.createdDate = createdDate;
    }

    public CoachAppointment() {
        super();
    }

    public Long getAppointId() {
        return appointId;
    }

    public void setAppointId(Long appointId) {
        this.appointId = appointId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getAppDate() {
        return appDate;
    }

    public void setAppDate(String appDate) {
        this.appDate = appDate == null ? null : appDate.trim();
    }

    public String getAppTime() {
        return appTime;
    }

    public void setAppTime(String appTime) {
        this.appTime = appTime == null ? null : appTime.trim();
    }

    public Byte getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(Byte appStatus) {
        this.appStatus = appStatus;
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
        sb.append(", appointId=").append(appointId);
        sb.append(", coachId=").append(coachId);
        sb.append(", appDate=").append(appDate);
        sb.append(", appTime=").append(appTime);
        sb.append(", appStatus=").append(appStatus);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}