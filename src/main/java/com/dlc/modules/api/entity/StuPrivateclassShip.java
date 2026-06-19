package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StuPrivateclassShip implements Serializable {
    private Long spsId;

    private Long studentId;

    private Long classId;

    private Long coachId;

    private Integer orderType;

    private String orderNo;

    private String className;

    private Double energy;

    private String classTime;

    private String classDate;

    private String classroom;

    private Long classplaceId;

    private Integer askNumber;

    private Integer buyNumber;

    private Integer classStatus;

    private Date createdDate;

    private BigDecimal price;

    private BigDecimal duration;

    private Date validityDate;

    private static final long serialVersionUID = 1L;

    public Long getSpsId() {
        return spsId;
    }

    public void setSpsId(Long spsId) {
        this.spsId = spsId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Long getClassplaceId() {
        return classplaceId;
    }

    public void setClassplaceId(Long classplaceId) {
        this.classplaceId = classplaceId;
    }

    public Integer getAskNumber() {
        return askNumber;
    }

    public void setAskNumber(Integer askNumber) {
        this.askNumber = askNumber;
    }

    public Integer getBuyNumber() {
        return buyNumber;
    }

    public void setBuyNumber(Integer buyNumber) {
        this.buyNumber = buyNumber;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "StuPrivateclassShip{" +
                "spsId=" + spsId +
                ", studentId=" + studentId +
                ", classId=" + classId +
                ", coachId=" + coachId +
                ", orderType=" + orderType +
                ", orderNo='" + orderNo + '\'' +
                ", className='" + className + '\'' +
                ", energy=" + energy +
                ", classTime='" + classTime + '\'' +
                ", classDate='" + classDate + '\'' +
                ", classroom='" + classroom + '\'' +
                ", classplaceId=" + classplaceId +
                ", askNumber=" + askNumber +
                ", buyNumber=" + buyNumber +
                ", classStatus=" + classStatus +
                ", createdDate=" + createdDate +
                ", price=" + price +
                ", duration=" + duration +
                ", validityDate=" + validityDate +
                '}';
    }
}