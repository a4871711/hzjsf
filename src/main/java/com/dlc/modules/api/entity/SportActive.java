package com.dlc.modules.api.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class SportActive implements Serializable {
    private Long sportActiveId;

    private Long userId;

    private Long srId;

    private Integer sportType;

    private Integer countType;

    private Integer cishu;

    private double oxyKcal;

    private double unoxyKcal;

    private double sportStep;

    private double distance;

    private String csPer;

    private String timePer;

    private String energyPer;

    private String sportStepPer;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time sportTime;

    private String sumST;//运动时间总计

    private double energy;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public SportActive() {
        super();
    }

    public SportActive(Long sportActiveId, Long userId, Long srId, Integer sportType, Integer countType, Integer cishu, double oxyKcal, double unoxyKcal, double sportStep, double distance, String csPer, String timePer, String energyPer, String sportStepPer, Time sportTime, String sumST, double energy, Date createdDate) {
        this.sportActiveId = sportActiveId;
        this.userId = userId;
        this.srId = srId;
        this.sportType = sportType;
        this.countType = countType;
        this.cishu = cishu;
        this.oxyKcal = oxyKcal;
        this.unoxyKcal = unoxyKcal;
        this.sportStep = sportStep;
        this.distance = distance;
        this.csPer = csPer;
        this.timePer = timePer;
        this.energyPer = energyPer;
        this.sportStepPer = sportStepPer;
        this.sportTime = sportTime;
        this.sumST = sumST;
        this.energy = energy;
        this.createdDate = createdDate;
    }

    public Long getSportActiveId() {
        return sportActiveId;
    }

    public void setSportActiveId(Long sportActiveId) {
        this.sportActiveId = sportActiveId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSrId() {
        return srId;
    }

    public void setSrId(Long srId) {
        this.srId = srId;
    }

    public Integer getSportType() {
        return sportType;
    }

    public void setSportType(Integer sportType) {
        this.sportType = sportType;
    }

    public Integer getCountType() {
        return countType;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    public Integer getCishu() {
        return cishu;
    }

    public void setCishu(Integer cishu) {
        this.cishu = cishu;
    }

    public double getOxyKcal() {
        return oxyKcal;
    }

    public void setOxyKcal(double oxyKcal) {
        this.oxyKcal = oxyKcal;
    }

    public double getUnoxyKcal() {
        return unoxyKcal;
    }

    public void setUnoxyKcal(double unoxyKcal) {
        this.unoxyKcal = unoxyKcal;
    }

    public double getSportStep() {
        return sportStep;
    }

    public void setSportStep(double sportStep) {
        this.sportStep = sportStep;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCsPer() {
        return csPer;
    }

    public void setCsPer(String csPer) {
        this.csPer = csPer;
    }

    public String getTimePer() {
        return timePer;
    }

    public void setTimePer(String timePer) {
        this.timePer = timePer;
    }

    public String getEnergyPer() {
        return energyPer;
    }

    public void setEnergyPer(String energyPer) {
        this.energyPer = energyPer;
    }

    public String getSportStepPer() {
        return sportStepPer;
    }

    public void setSportStepPer(String sportStepPer) {
        this.sportStepPer = sportStepPer;
    }

    public Time getSportTime() {
        return sportTime;
    }

    public void setSportTime(Time sportTime) {
        this.sportTime = sportTime;
    }

    public String getSumST() {
        return sumST;
    }

    public void setSumST(String sumST) {
        this.sumST = sumST;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}