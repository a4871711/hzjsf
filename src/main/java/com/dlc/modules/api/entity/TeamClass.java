package com.dlc.modules.api.entity;

import javax.xml.soap.Text;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TeamClass implements Serializable {
    private Long teamClassId;

    private Long storeId;

    private String teamClassName;

    private Integer teamClassType;

    private BigDecimal classPrice;

    private Integer totalNum;

    private Integer actulNum;

    private Long coachId;

    private String coachName;

    private Double energy;

    private String room;

    private String classTime;

    private String place;

    private String introduce;

    private String classLabel;

    private String classDetail;

    private String firstImgUrl;

    private String classImgUrl;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getTeamClassId() {
        return teamClassId;
    }

    public void setTeamClassId(Long teamClassId) {
        this.teamClassId = teamClassId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getTeamClassName() {
        return teamClassName;
    }

    public void setTeamClassName(String teamClassName) {
        this.teamClassName = teamClassName;
    }

    public Integer getTeamClassType() {
        return teamClassType;
    }

    public void setTeamClassType(Integer teamClassType) {
        this.teamClassType = teamClassType;
    }

    public BigDecimal getClassPrice() {
        return classPrice;
    }

    public void setClassPrice(BigDecimal classPrice) {
        this.classPrice = classPrice;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getActulNum() {
        return actulNum;
    }

    public void setActulNum(Integer actulNum) {
        this.actulNum = actulNum;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public String getClassDetail() {
        return classDetail;
    }

    public void setClassDetail(String classDetail) {
        this.classDetail = classDetail;
    }

    public String getFirstImgUrl() {
        return firstImgUrl;
    }

    public void setFirstImgUrl(String firstImgUrl) {
        this.firstImgUrl = firstImgUrl;
    }

    public String getClassImgUrl() {
        return classImgUrl;
    }

    public void setClassImgUrl(String classImgUrl) {
        this.classImgUrl = classImgUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "TeamClass{" +
                "teamClassId=" + teamClassId +
                ", storeId=" + storeId +
                ", teamClassName='" + teamClassName + '\'' +
                ", teamClassType=" + teamClassType +
                ", classPrice=" + classPrice +
                ", totalNum=" + totalNum +
                ", actulNum=" + actulNum +
                ", coachId=" + coachId +
                ", coachName='" + coachName + '\'' +
                ", energy='" + energy + '\'' +
                ", room='" + room + '\'' +
                ", classTime='" + classTime + '\'' +
                ", place='" + place + '\'' +
                ", introduce='" + introduce + '\'' +
                ", classLabel='" + classLabel + '\'' +
                ", classDetail='" + classDetail + '\'' +
                ", firstImgUrl='" + firstImgUrl + '\'' +
                ", classImgUrl='" + classImgUrl + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

}