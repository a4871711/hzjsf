package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class CoachPlaceShip implements Serializable {
    private Long cpsId;

    private Long coachId;

    private Long storeId;

    private String storeName;

    private Integer grade;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getCpsId() {
        return cpsId;
    }

    public void setCpsId(Long cpsId) {
        this.cpsId = cpsId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "CoachPlaceShip{" +
                "cpsId=" + cpsId +
                ", coachId=" + coachId +
                ", storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", grade=" + grade +
                ", createdDate=" + createdDate +
                '}';
    }
}