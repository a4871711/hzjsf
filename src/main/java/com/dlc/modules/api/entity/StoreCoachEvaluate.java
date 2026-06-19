package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StoreCoachEvaluate implements Serializable {
    private Long evaluateId;

    private Long scId;

    private Long userId;

    private String headImgUrl;

    private String nickname;

    private Integer evLevel;

    private String evContent;

    private String evaluatImgUrl;

    private Date evaluatDate;

    private static final long serialVersionUID = 1L;

    public StoreCoachEvaluate(Long evaluateId, Long scId, Long userId, String headImgUrl, String nickname, Integer evLevel, String evContent, String evaluatImgUrl, Date evaluatDate) {
        this.evaluateId = evaluateId;
        this.scId = scId;
        this.userId = userId;
        this.headImgUrl = headImgUrl;
        this.nickname = nickname;
        this.evLevel = evLevel;
        this.evContent = evContent;
        this.evaluatImgUrl = evaluatImgUrl;
        this.evaluatDate = evaluatDate;
    }

    public StoreCoachEvaluate() {
        super();
    }

    public Long getEvaluateId() {
        return evaluateId;
    }

    public void setEvaluateId(Long evaluateId) {
        this.evaluateId = evaluateId;
    }

    public Long getScId() {
        return scId;
    }

    public void setScId(Long scId) {
        this.scId = scId;
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
        this.headImgUrl = headImgUrl == null ? null : headImgUrl.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
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
        this.evContent = evContent == null ? null : evContent.trim();
    }

    public String getEvaluatImgUrl() {
        return evaluatImgUrl;
    }

    public void setEvaluatImgUrl(String evaluatImgUrl) {
        this.evaluatImgUrl = evaluatImgUrl == null ? null : evaluatImgUrl.trim();
    }

    public Date getEvaluatDate() {
        return evaluatDate;
    }

    public void setEvaluatDate(Date evaluatDate) {
        this.evaluatDate = evaluatDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", evaluateId=").append(evaluateId);
        sb.append(", scId=").append(scId);
        sb.append(", userId=").append(userId);
        sb.append(", headImgUrl=").append(headImgUrl);
        sb.append(", nickname=").append(nickname);
        sb.append(", evLevel=").append(evLevel);
        sb.append(", evContent=").append(evContent);
        sb.append(", evaluatImgUrl=").append(evaluatImgUrl);
        sb.append(", evaluatDate=").append(evaluatDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}