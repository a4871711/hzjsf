package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PrivateClass implements Serializable {
    private Long privateClassId;

    private String className;

    private Integer classType;

    private Double energy;

    private BigDecimal classPrice;

    private BigDecimal classTime;

    private String classDetail;

    private Integer validityDay;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getPrivateClassId() {
        return privateClassId;
    }

    public void setPrivateClassId(Long privateClassId) {
        this.privateClassId = privateClassId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public BigDecimal getClassPrice() {
        return classPrice;
    }

    public void setClassPrice(BigDecimal classPrice) {
        this.classPrice = classPrice;
    }

    public BigDecimal getClassTime() {
        return classTime;
    }

    public void setClassTime(BigDecimal classTime) {
        this.classTime = classTime;
    }

    public String getClassDetail() {
        return classDetail;
    }

    public void setClassDetail(String classDetail) {
        this.classDetail = classDetail;
    }

    public Integer getValidityDay() {
        return validityDay;
    }

    public void setValidityDay(Integer validityDay) {
        this.validityDay = validityDay;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "PrivateClass{" +
                "privateClassId=" + privateClassId +
                ", className='" + className + '\'' +
                ", classType=" + classType +
                ", energy=" + energy +
                ", classPrice=" + classPrice +
                ", classTime=" + classTime +
                ", classDetail='" + classDetail + '\'' +
                ", validityDay=" + validityDay +
                ", createdDate=" + createdDate +
                '}';
    }
}