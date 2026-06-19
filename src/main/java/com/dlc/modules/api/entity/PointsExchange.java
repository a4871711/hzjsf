package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class PointsExchange implements Serializable {
    private Long pointsExchangeId;

    private Long userId;

    private Long storeId;

    private Byte detailType;

    private Integer pointsCount;

    private Integer exchangeMoney;

    private Date exchangeDate;

    private static final long serialVersionUID = 1L;

    public PointsExchange(Long pointsExchangeId, Long userId, Long storeId, Byte detailType, Integer pointsCount, Integer exchangeMoney, Date exchangeDate) {
        this.pointsExchangeId = pointsExchangeId;
        this.userId = userId;
        this.storeId = storeId;
        this.detailType = detailType;
        this.pointsCount = pointsCount;
        this.exchangeMoney = exchangeMoney;
        this.exchangeDate = exchangeDate;
    }

    public PointsExchange() {
        super();
    }

    public Long getPointsExchangeId() {
        return pointsExchangeId;
    }

    public void setPointsExchangeId(Long pointsExchangeId) {
        this.pointsExchangeId = pointsExchangeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getDetailType() {
        return detailType;
    }

    public void setDetailType(Byte detailType) {
        this.detailType = detailType;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
    }

    public Integer getExchangeMoney() {
        return exchangeMoney;
    }

    public void setExchangeMoney(Integer exchangeMoney) {
        this.exchangeMoney = exchangeMoney;
    }

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "PointsExchange{" +
                "pointsExchangeId=" + pointsExchangeId +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", detailType=" + detailType +
                ", pointsCount=" + pointsCount +
                ", exchangeMoney=" + exchangeMoney +
                ", exchangeDate=" + exchangeDate +
                '}';
    }
}