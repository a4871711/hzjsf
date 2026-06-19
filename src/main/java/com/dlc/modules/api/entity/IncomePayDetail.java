package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class IncomePayDetail implements Serializable {
    private Long incomePayDetailId;

    private Long userId;

    private Long storeId;

    private String orderNo;

    private String transactionNumber;

    private Integer payType;

    private BigDecimal money;

    private Date tradeDate;

    private Integer tradeType;

    private String openId;

    private Long anotherId;

    private Integer tradeStatus;

    private Date checkedTime;

    private Date transactionTime;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public IncomePayDetail() {
        super();
    }

    public Long getIncomePayDetailId() {
        return incomePayDetailId;
    }

    public void setIncomePayDetailId(Long incomePayDetailId) {
        this.incomePayDetailId = incomePayDetailId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber == null ? null : transactionNumber.trim();
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public Integer getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Date getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(Date checkedTime) {
        this.checkedTime = checkedTime;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getAnotherId() {
        return anotherId;
    }

    public void setAnotherId(Long anotherId) {
        this.anotherId = anotherId;
    }

    @Override
    public String toString() {
        return "IncomePayDetail{" +
                "incomePayDetailId=" + incomePayDetailId +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", orderNo='" + orderNo + '\'' +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", payType=" + payType +
                ", money=" + money +
                ", tradeDate=" + tradeDate +
                ", tradeType=" + tradeType +
                ", openId='" + openId + '\'' +
                ", anotherId=" + anotherId +
                ", tradeStatus=" + tradeStatus +
                ", checkedTime=" + checkedTime +
                ", transactionTime=" + transactionTime +
                ", createdDate=" + createdDate +
                '}';
    }
}