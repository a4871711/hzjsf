package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StudentTeamclassShip implements Serializable {
    private Long stuTeamClassId;

    private Long studentId;

    private Long classId;

    private Long storeId;

    private String coachName;

    private String className;

    private String classImgUrl;

    private String classLabel;

    private BigDecimal classPrice;

    private Integer classType;

    private Integer classStatus;

    private String orderNo;

    private String classTime;

    private Double energy;

    private String classroom;

    private String classAddress;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getStuTeamClassId() {
        return stuTeamClassId;
    }

    public void setStuTeamClassId(Long stuTeamClassId) {
        this.stuTeamClassId = stuTeamClassId;
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

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
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

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClassAddress() {
        return classAddress;
    }

    public void setClassAddress(String classAddress) {
        this.classAddress = classAddress;
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

    public String getClassImgUrl() {
        return classImgUrl;
    }

    public void setClassImgUrl(String classImgUrl) {
        this.classImgUrl = classImgUrl;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public BigDecimal getClassPrice() {
        return classPrice;
    }

    public void setClassPrice(BigDecimal classPrice) {
        this.classPrice = classPrice;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "StudentTeamclassShip{" +
                "stuTeamClassId=" + stuTeamClassId +
                ", studentId=" + studentId +
                ", classId=" + classId +
                ", storeId=" + storeId +
                ", coachName='" + coachName + '\'' +
                ", className='" + className + '\'' +
                ", classImgUrl='" + classImgUrl + '\'' +
                ", classLabel='" + classLabel + '\'' +
                ", classPrice=" + classPrice +
                ", classType=" + classType +
                ", classStatus=" + classStatus +
                ", orderNo='" + orderNo + '\'' +
                ", classTime='" + classTime + '\'' +
                ", energy=" + energy +
                ", classroom='" + classroom + '\'' +
                ", classAddress='" + classAddress + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}