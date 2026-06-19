package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PrivateClassOrder implements Serializable {
    private Long privateClassOrderId;

    private String openId;

    private Long userId;

    private Long privateClassId;

    private Long coachId;

    private String coachName;

    private Integer buyCount;

    private Double energy;

    private Integer orderType;

    private String imgUrl;

    private String orderNo;

    private String transactionNo;

    private BigDecimal paySum;

    private Integer payType;

    private BigDecimal realPayment;

    private Integer status;

    private String classTime;

    private Date createdDate;

    private Date payTime;

    private Date finish;

    private Date validityDate;

    private String className;

    private BigDecimal price;

    private BigDecimal duration;

    private Long storeAddrId;

    private String storeName;

    private String classDate;

    private static final long serialVersionUID = 1L;

    public Long getPrivateClassOrderId() {
        return privateClassOrderId;
    }

    public void setPrivateClassOrderId(Long privateClassOrderId) {
        this.privateClassOrderId = privateClassOrderId;
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

    public Long getPrivateClassId() {
        return privateClassId;
    }

    public void setPrivateClassId(Long privateClassId) {
        this.privateClassId = privateClassId;
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

    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigDecimal getRealPayment() {
        return realPayment;
    }

    public void setRealPayment(BigDecimal realPayment) {
        this.realPayment = realPayment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
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

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
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

    public Long getStoreAddrId() {
        return storeAddrId;
    }

    public void setStoreAddrId(Long storeAddrId) {
        this.storeAddrId = storeAddrId;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "PrivateClassOrder{" +
                "privateClassOrderId=" + privateClassOrderId +
                ", openId='" + openId + '\'' +
                ", userId=" + userId +
                ", privateClassId=" + privateClassId +
                ", coachId=" + coachId +
                ", coachName='" + coachName + '\'' +
                ", buyCount=" + buyCount +
                ", energy=" + energy +
                ", orderType=" + orderType +
                ", imgUrl='" + imgUrl + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", paySum=" + paySum +
                ", payType=" + payType +
                ", realPayment=" + realPayment +
                ", status=" + status +
                ", classTime='" + classTime + '\'' +
                ", createdDate=" + createdDate +
                ", payTime=" + payTime +
                ", finish=" + finish +
                ", validityDate=" + validityDate +
                ", className='" + className + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", storeAddrId=" + storeAddrId +
                ", storeName='" + storeName + '\'' +
                ", classDate='" + classDate + '\'' +
                '}';
    }
}