package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TeamClassOrder implements Serializable {
    private Long teamClassOrderId;

    private String openId;

    private Long userId;

    private Long storeId;

    private Byte orderType;

    private Long teamClassId;

    private String orderNo;

    private String transactionNo;

    private BigDecimal paySum;

    private Byte payType;

    private BigDecimal realPayment;

    private Byte status;

    private String coachName;

    private String classTime;

    private String classRoom;

    private String classAddress;

    private String className;

    private Double energy;

    private BigDecimal price;

    private String imgUrl;

    private String classLabel;

    private Date createdDate;

    private Date payTime;

    private Date finish;

    private static final long serialVersionUID = 1L;

    public Long getTeamClassOrderId() {
        return teamClassOrderId;
    }

    public void setTeamClassOrderId(Long teamClassOrderId) {
        this.teamClassOrderId = teamClassOrderId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    public Long getTeamClassId() {
        return teamClassId;
    }

    public void setTeamClassId(Long teamClassId) {
        this.teamClassId = teamClassId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public BigDecimal getPaySum() {
        return paySum;
    }

    public void setPaySum(BigDecimal paySum) {
        this.paySum = paySum;
    }

    public Byte getPayType() {
        return payType;
    }

    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    public BigDecimal getRealPayment() {
        return realPayment;
    }

    public void setRealPayment(BigDecimal realPayment) {
        this.realPayment = realPayment;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getClassAddress() {
        return classAddress;
    }

    public void setClassAddress(String classAddress) {
        this.classAddress = classAddress;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "TeamClassOrder{" +
                "teamClassOrderId=" + teamClassOrderId +
                ", openId='" + openId + '\'' +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", orderType=" + orderType +
                ", teamClassId=" + teamClassId +
                ", orderNo='" + orderNo + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", paySum=" + paySum +
                ", payType=" + payType +
                ", realPayment=" + realPayment +
                ", status=" + status +
                ", coachName='" + coachName + '\'' +
                ", classTime='" + classTime + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", classAddress='" + classAddress + '\'' +
                ", className='" + className + '\'' +
                ", energy=" + energy +
                ", price=" + price +
                ", imgUrl='" + imgUrl + '\'' +
                ", classLabel='" + classLabel + '\'' +
                ", createdDate=" + createdDate +
                ", payTime=" + payTime +
                ", finish=" + finish +
                '}';
    }
}