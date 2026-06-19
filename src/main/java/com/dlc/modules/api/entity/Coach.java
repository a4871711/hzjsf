package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Coach implements Serializable {
    private Long coachId;

    private Long userId;

    private String headImgUrl;

    private Integer grade;

    private String coachName;

    private String phone;

    private Integer sex;

    private String province;

    private String city;

    private String zone;

    private String identity;

    private Integer employTime;

    private String identImgUrl;

    private String identBackImgUrl;

    private String diplomaImgUrl;

    private String storeId;

    private Double level;

    private Integer approveStatus;

    private Date approveTime;

    private String approveResult;

    private BigDecimal minClassMoney;

    private Integer classCount;

    private String introduce;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Integer getEmployTime() {
        return employTime;
    }

    public void setEmployTime(Integer employTime) {
        this.employTime = employTime;
    }

    public String getIdentImgUrl() {
        return identImgUrl;
    }

    public void setIdentImgUrl(String identImgUrl) {
        this.identImgUrl = identImgUrl;
    }

    public String getIdentBackImgUrl() {
        return identBackImgUrl;
    }

    public void setIdentBackImgUrl(String identBackImgUrl) {
        this.identBackImgUrl = identBackImgUrl;
    }

    public String getDiplomaImgUrl() {
        return diplomaImgUrl;
    }

    public void setDiplomaImgUrl(String diplomaImgUrl) {
        this.diplomaImgUrl = diplomaImgUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Double getLevel() {
        return level;
    }

    public void setLevel(Double level) {
        this.level = level;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public BigDecimal getMinClassMoney() {
        return minClassMoney;
    }

    public void setMinClassMoney(BigDecimal minClassMoney) {
        this.minClassMoney = minClassMoney;
    }

    public Integer getClassCount() {
        return classCount;
    }

    public void setClassCount(Integer classCount) {
        this.classCount = classCount;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "coachId=" + coachId +
                ", userId=" + userId +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", grade=" + grade +
                ", coachName='" + coachName + '\'' +
                ", phone='" + phone + '\'' +
                ", sex=" + sex +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", zone='" + zone + '\'' +
                ", identity='" + identity + '\'' +
                ", employTime=" + employTime +
                ", identImgUrl='" + identImgUrl + '\'' +
                ", identBackImgUrl='" + identBackImgUrl + '\'' +
                ", diplomaImgUrl='" + diplomaImgUrl + '\'' +
                ", storeId='" + storeId + '\'' +
                ", level=" + level +
                ", approveStatus=" + approveStatus +
                ", approveTime=" + approveTime +
                ", approveResult='" + approveResult + '\'' +
                ", minClassMoney=" + minClassMoney +
                ", classCount=" + classCount +
                ", introduce='" + introduce + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}