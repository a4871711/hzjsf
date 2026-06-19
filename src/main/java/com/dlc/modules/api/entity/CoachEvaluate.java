package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class CoachEvaluate implements Serializable {
    private Long coachEvaluatId;

    private Long coachId;

    private Long userId;

    private Long privateClassId;

    private String orderNo;

    private String className;

    private String headImgUrl;

    private String nickname;

    private Integer evLevel;

    private String evContent;

    private String evaluatImgUrl;

    private Date evaluatDate;

    private static final long serialVersionUID = 1L;

    public Long getCoachEvaluatId() {
        return coachEvaluatId;
    }

    public void setCoachEvaluatId(Long coachEvaluatId) {
        this.coachEvaluatId = coachEvaluatId;
    }

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

    public Long getPrivateClassId() {
        return privateClassId;
    }

    public void setPrivateClassId(Long privateClassId) {
        this.privateClassId = privateClassId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getEvLevel() {
        return evLevel;
    }

    public void setEvLevel(Integer evLevel) {
        this.evLevel = evLevel;
    }

    public String getEvContent() {
        return evContent;
    }

    public void setEvContent(String evContent) {
        this.evContent = evContent;
    }

    public String getEvaluatImgUrl() {
        return evaluatImgUrl;
    }

    public void setEvaluatImgUrl(String evaluatImgUrl) {
        this.evaluatImgUrl = evaluatImgUrl;
    }

    public Date getEvaluatDate() {
        return evaluatDate;
    }

    public void setEvaluatDate(Date evaluatDate) {
        this.evaluatDate = evaluatDate;
    }

    @Override
    public String toString() {
        return "CoachEvaluate{" +
                "coachEvaluatId=" + coachEvaluatId +
                ", coachId=" + coachId +
                ", userId=" + userId +
                ", privateClassId=" + privateClassId +
                ", orderNo='" + orderNo + '\'' +
                ", className='" + className + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", evLevel=" + evLevel +
                ", evContent='" + evContent + '\'' +
                ", evaluatImgUrl='" + evaluatImgUrl + '\'' +
                ", evaluatDate=" + evaluatDate +
                '}';
    }
}