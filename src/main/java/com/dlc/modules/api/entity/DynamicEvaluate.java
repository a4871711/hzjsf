package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class DynamicEvaluate implements Serializable {
    private Long dyEvaluatId;

    private Long dynamicId;

    private Long reviewerId;

    private String headImgUrl;

    private String nickname;

    private String evContent;

    private String evaluatImgUrl;

    private Date evaluatDate;

    private static final long serialVersionUID = 1L;

    public Long getDyEvaluatId() {
        return dyEvaluatId;
    }

    public void setDyEvaluatId(Long dyEvaluatId) {
        this.dyEvaluatId = dyEvaluatId;
    }

    public Long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
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
        return "DynamicEvaluate{" +
                "dyEvaluatId=" + dyEvaluatId +
                ", dynamicId=" + dynamicId +
                ", reviewerId=" + reviewerId +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", evContent='" + evContent + '\'' +
                ", evaluatImgUrl='" + evaluatImgUrl + '\'' +
                ", evaluatDate=" + evaluatDate +
                '}';
    }
}