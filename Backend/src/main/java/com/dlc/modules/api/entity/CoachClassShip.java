package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CoachClassShip implements Serializable {
    private Long coachClassShipId;

    private Long coachId;

    private Long classId;

    private BigDecimal classFee;

    private Integer lowestClass;

    private Integer classStatus;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public CoachClassShip(Long coachClassShipId, Long coachId, Long classId, BigDecimal classFee, Integer lowestClass, Integer classStatus, Date createdDate) {
        this.coachClassShipId = coachClassShipId;
        this.coachId = coachId;
        this.classId = classId;
        this.classFee = classFee;
        this.lowestClass = lowestClass;
        this.classStatus = classStatus;
        this.createdDate = createdDate;
    }

    public CoachClassShip() {
        super();
    }

    public Long getCoachClassShipId() {
        return coachClassShipId;
    }

    public void setCoachClassShipId(Long coachClassShipId) {
        this.coachClassShipId = coachClassShipId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public BigDecimal getClassFee() {
        return classFee;
    }

    public void setClassFee(BigDecimal classFee) {
        this.classFee = classFee;
    }

    public Integer getLowestClass() {
        return lowestClass;
    }

    public void setLowestClass(Integer lowestClass) {
        this.lowestClass = lowestClass;
    }

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
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
        sb.append(", coachClassShipId=").append(coachClassShipId);
        sb.append(", coachId=").append(coachId);
        sb.append(", classId=").append(classId);
        sb.append(", classFee=").append(classFee);
        sb.append(", lowestClass=").append(lowestClass);
        sb.append(", classStatus=").append(classStatus);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}