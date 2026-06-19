package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class SportRecord implements Serializable {
    private Long srId;

    private Long userId;

    private Integer activity;

    private Integer teamClassCount;

    private Integer privateClassCount;

    private Boolean sportType;

    private Integer oxyKcal;

    private Integer unoxyKcal;

    private Integer teamClassKcal;

    private Integer privateClassKcal;

    private String startDate;

    private String sportTime;

    private Integer sportStep;

    private Integer energy;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public SportRecord(Long srId, Long userId, Integer activity, Integer teamClassCount, Integer privateClassCount, Boolean sportType, Integer oxyKcal, Integer unoxyKcal, Integer teamClassKcal, Integer privateClassKcal, String startDate, String sportTime, Integer sportStep, Integer energy, Date createdDate) {
        this.srId = srId;
        this.userId = userId;
        this.activity = activity;
        this.teamClassCount = teamClassCount;
        this.privateClassCount = privateClassCount;
        this.sportType = sportType;
        this.oxyKcal = oxyKcal;
        this.unoxyKcal = unoxyKcal;
        this.teamClassKcal = teamClassKcal;
        this.privateClassKcal = privateClassKcal;
        this.startDate = startDate;
        this.sportTime = sportTime;
        this.sportStep = sportStep;
        this.energy = energy;
        this.createdDate = createdDate;
    }

    public SportRecord() {
        super();
    }

    public Long getSrId() {
        return srId;
    }

    public void setSrId(Long srId) {
        this.srId = srId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    public Integer getTeamClassCount() {
        return teamClassCount;
    }

    public void setTeamClassCount(Integer teamClassCount) {
        this.teamClassCount = teamClassCount;
    }

    public Integer getPrivateClassCount() {
        return privateClassCount;
    }

    public void setPrivateClassCount(Integer privateClassCount) {
        this.privateClassCount = privateClassCount;
    }

    public Boolean getSportType() {
        return sportType;
    }

    public void setSportType(Boolean sportType) {
        this.sportType = sportType;
    }

    public Integer getOxyKcal() {
        return oxyKcal;
    }

    public void setOxyKcal(Integer oxyKcal) {
        this.oxyKcal = oxyKcal;
    }

    public Integer getUnoxyKcal() {
        return unoxyKcal;
    }

    public void setUnoxyKcal(Integer unoxyKcal) {
        this.unoxyKcal = unoxyKcal;
    }

    public Integer getTeamClassKcal() {
        return teamClassKcal;
    }

    public void setTeamClassKcal(Integer teamClassKcal) {
        this.teamClassKcal = teamClassKcal;
    }

    public Integer getPrivateClassKcal() {
        return privateClassKcal;
    }

    public void setPrivateClassKcal(Integer privateClassKcal) {
        this.privateClassKcal = privateClassKcal;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate == null ? null : startDate.trim();
    }

    public String getSportTime() {
        return sportTime;
    }

    public void setSportTime(String sportTime) {
        this.sportTime = sportTime == null ? null : sportTime.trim();
    }

    public Integer getSportStep() {
        return sportStep;
    }

    public void setSportStep(Integer sportStep) {
        this.sportStep = sportStep;
    }

    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
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
        sb.append(", srId=").append(srId);
        sb.append(", userId=").append(userId);
        sb.append(", activity=").append(activity);
        sb.append(", teamClassCount=").append(teamClassCount);
        sb.append(", privateClassCount=").append(privateClassCount);
        sb.append(", sportType=").append(sportType);
        sb.append(", oxyKcal=").append(oxyKcal);
        sb.append(", unoxyKcal=").append(unoxyKcal);
        sb.append(", teamClassKcal=").append(teamClassKcal);
        sb.append(", privateClassKcal=").append(privateClassKcal);
        sb.append(", startDate=").append(startDate);
        sb.append(", sportTime=").append(sportTime);
        sb.append(", sportStep=").append(sportStep);
        sb.append(", energy=").append(energy);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}