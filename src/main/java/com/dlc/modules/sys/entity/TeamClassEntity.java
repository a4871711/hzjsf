package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 10 - 12 14:02
 * @description 团体课
 */
public class TeamClassEntity implements Serializable {
    //团体课ID
    private Long teamClassId;
    //发起课程门店ID
    private Long storeId;
    //团体课名称
    private String teamClassName;
    //团体课类型(0：免费 1：精品)
    private Integer teamClassType;
    //团体课价格
    private BigDecimal classPrice;
    //总名额
    private int totalNum;
    //实际报名人数
    private int actulNum;
    //教练ID（门店指派教练，不关联收益）
    private Long coachId;
    //教练名称（门店指派教练，不关联收益）
    private String coachName;
    //能量
    private Double energy;
    //场所
    private String room;
    //时间（xx年xx月xx日 xx:xx ~ xx:xx）
    private String classTime;
    //地址
    private String place;
    //课程介绍
    private String introduce;
    //课程标签（例：增肌，塑性，减脂...）
    private String classLabel;
    //课程详情
    private String classDetail;
    //封面图
    private String firstImgUrl;
    //详情图片链接
    private String classImgUrl;
    //创建时间
    private Date createdDate;

    //团体课名称
    private String storeName;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

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

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getActulNum() {
        return actulNum;
    }

    public void setActulNum(int actulNum) {
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
}
