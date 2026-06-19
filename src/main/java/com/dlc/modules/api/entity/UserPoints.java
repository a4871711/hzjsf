package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class UserPoints implements Serializable {
    private Long userPointsId;

    private Long userId;

    private Integer pointsCount;

    private Integer sumPoints;

    private String pointsRule;

    private Date updateDate;

    private static final long serialVersionUID = 1L;

    public UserPoints(Long userPointsId, Long userId, Integer pointsCount, Integer sumPoints, String pointsRule, Date updateDate) {
        this.userPointsId = userPointsId;
        this.userId = userId;
        this.pointsCount = pointsCount;
        this.sumPoints = sumPoints;
        this.pointsRule = pointsRule;
        this.updateDate = updateDate;
    }

    public UserPoints() {
        super();
    }

    public Long getUserPointsId() {
        return userPointsId;
    }

    public void setUserPointsId(Long userPointsId) {
        this.userPointsId = userPointsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
    }

    public String getPointsRule() {
        return pointsRule;
    }

    public void setPointsRule(String pointsRule) {
        this.pointsRule = pointsRule == null ? null : pointsRule.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getSumPoints() {
        return sumPoints;
    }

    public void setSumPoints(Integer sumPoints) {
        this.sumPoints = sumPoints;
    }

    @Override
    public String toString() {
        return "UserPoints{" +
                "userPointsId=" + userPointsId +
                ", userId=" + userId +
                ", pointsCount=" + pointsCount +
                ", sumPoints=" + sumPoints +
                ", pointsRule='" + pointsRule + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}