package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class BodysInfo implements Serializable {
    private Long id;

    private String scanid;

    private Long userId;

    private Long vid;

    private String weight;

    private String bodyFat;

    private String percentBodyFat;

    private String bmi;

    private String kcal;

    private String waistToHip;

    private String fluid;

    private String muscle;

    private String result;

    private Long gymEngineId;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public BodysInfo(Long id, String scanid, String weight, String bodyFat, String percentBodyFat, String bmi, String kcal, String waistToHip, String fluid, String muscle, String result, Long gymEngineId, Date createTime) {
        this.id = id;
        this.scanid = scanid;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.percentBodyFat = percentBodyFat;
        this.bmi = bmi;
        this.kcal = kcal;
        this.waistToHip = waistToHip;
        this.fluid = fluid;
        this.muscle = muscle;
        this.result = result;
        this.gymEngineId = gymEngineId;
        this.createTime = createTime;
    }

    public BodysInfo() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScanid() {
        return scanid;
    }

    public void setScanid(String scanid) {
        this.scanid = scanid;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(String bodyFat) {
        this.bodyFat = bodyFat;
    }

    public String getPercentBodyFat() {
        return percentBodyFat;
    }

    public void setPercentBodyFat(String percentBodyFat) {
        this.percentBodyFat = percentBodyFat;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getWaistToHip() {
        return waistToHip;
    }

    public void setWaistToHip(String waistToHip) {
        this.waistToHip = waistToHip;
    }

    public String getFluid() {
        return fluid;
    }

    public void setFluid(String fluid) {
        this.fluid = fluid;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getGymEngineId() {
        return gymEngineId;
    }

    public void setGymEngineId(Long gymEngineId) {
        this.gymEngineId = gymEngineId;
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
        sb.append(", scanid=").append(scanid);
        sb.append(", weight=").append(weight);
        sb.append(", bodyFat=").append(bodyFat);
        sb.append(", percentBodyFat=").append(percentBodyFat);
        sb.append(", bmi=").append(bmi);
        sb.append(", kcal=").append(kcal);
        sb.append(", waistToHip=").append(waistToHip);
        sb.append(", fluid=").append(fluid);
        sb.append(", muscle=").append(muscle);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVid() {
        return vid;
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }
}