package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ArrangeClass implements Serializable {
    private Long arrangeClassId;

    private Long spsId;

    private Long coachClassId;

    private Long coachId;

    private Long studentId;

    private String className;

    private String classPlace;

    private Long classPlaceId;

    private String classTime;

    private String classDate;

    private Integer classStatus;

    private Date createdDate;

    private BigDecimal price;

    private BigDecimal duration;

    private Date validityDate;

    private static final long serialVersionUID = 1L;

    public Long getArrangeClassId() {
        return arrangeClassId;
    }

    public void setArrangeClassId(Long arrangeClassId) {
        this.arrangeClassId = arrangeClassId;
    }

    public Long getSpsId() {
        return spsId;
    }

    public void setSpsId(Long spsId) {
        this.spsId = spsId;
    }

    public Long getCoachClassId() {
        return coachClassId;
    }

    public void setCoachClassId(Long coachClassId) {
        this.coachClassId = coachClassId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getClassPlace() {
        return classPlace;
    }

    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }

    public Long getClassPlaceId() {
        return classPlaceId;
    }

    public void setClassPlaceId(Long classPlaceId) {
        this.classPlaceId = classPlaceId;
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

    @Override
    public String toString() {
        return "ArrangeClass{" +
                "arrangeClassId=" + arrangeClassId +
                ", spsId=" + spsId +
                ", coachClassId=" + coachClassId +
                ", coachId=" + coachId +
                ", studentId=" + studentId +
                ", className='" + className + '\'' +
                ", classPlace='" + classPlace + '\'' +
                ", classPlaceId=" + classPlaceId +
                ", classTime='" + classTime + '\'' +
                ", classDate='" + classDate + '\'' +
                ", classStatus=" + classStatus +
                ", createdDate=" + createdDate +
                ", price=" + price +
                ", duration=" + duration +
                ", validityDate=" + validityDate +
                '}';
    }
}