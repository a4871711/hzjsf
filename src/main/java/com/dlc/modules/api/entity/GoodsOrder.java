package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoodsOrder implements Serializable {
    private Long goodsOrderId;

    private String openId;

    private Long userId;

    private Long goodsId;

    private Integer buyCount;

    private Byte orderType;

    private String orderNo;

    private Long receiveAddrId;

    private String receiveName;

    private String receivePhone;

    private String receiveAddr;

    private Long shopAddrId;

    //private Integer orderNum;

    private String transactionNo;

    private BigDecimal goodsSum;

    private BigDecimal fare;

    private BigDecimal couponMoney;

    private Byte payType;

    //private Boolean deliveType;

    private String company;

    private BigDecimal realPayment;

    private String logisticsNo;

    private String logisticsName;

    private Byte status;

    private Date createdDate;

    private Date payTime;

    private Date deliveryTime;

    private Date finish;

    private Date tradeTime;

    private String imgUrl;

    private String messages;

    //订单详情LXK
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    //优惠卷id
    private Long couponId;

    public BigDecimal getCouponMoney() {
        return couponMoney;
    }

    public void setCouponMoney(BigDecimal couponMoney) {
        this.couponMoney = couponMoney;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    private static final long serialVersionUID = 1L;

    public Long getGoodsOrderId() {
        return goodsOrderId;
    }

    public void setGoodsOrderId(Long goodsOrderId) {
        this.goodsOrderId = goodsOrderId;
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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getReceiveAddrId() {
        return receiveAddrId;
    }

    public void setReceiveAddrId(Long receiveAddrId) {
        this.receiveAddrId = receiveAddrId;
    }

    public Long getShopAddrId() {
        return shopAddrId;
    }

    public void setShopAddrId(Long shopAddrId) {
        this.shopAddrId = shopAddrId;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public Byte getPayType() {
        return payType;
    }

    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public BigDecimal getRealPayment() {
        return realPayment;
    }

    public void setRealPayment(BigDecimal realPayment) {
        this.realPayment = realPayment;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public BigDecimal getGoodsSum() {
        return goodsSum;
    }

    public void setGoodsSum(BigDecimal goodsSum) {
        this.goodsSum = goodsSum;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public String getReceiveAddr() {
        return receiveAddr;
    }

    public void setReceiveAddr(String receiveAddr) {
        this.receiveAddr = receiveAddr;
    }

    @Override
    public String toString() {
        return "GoodsOrder{" +
                "goodsOrderId=" + goodsOrderId +
                ", openId='" + openId + '\'' +
                ", userId=" + userId +
                ", goodsId=" + goodsId +
                ", buyCount=" + buyCount +
                ", orderType=" + orderType +
                ", orderNo='" + orderNo + '\'' +
                ", receiveAddrId=" + receiveAddrId +
                ", receiveName='" + receiveName + '\'' +
                ", receivePhone='" + receivePhone + '\'' +
                ", receiveAddr='" + receiveAddr + '\'' +
                ", shopAddrId=" + shopAddrId +
                ", transactionNo='" + transactionNo + '\'' +
                ", goodsSum=" + goodsSum +
                ", fare=" + fare +
                ", couponMoney=" + couponMoney +
                ", payType=" + payType +
                ", company='" + company + '\'' +
                ", realPayment=" + realPayment +
                ", logisticsNo='" + logisticsNo + '\'' +
                ", logisticsName='" + logisticsName + '\'' +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", payTime=" + payTime +
                ", deliveryTime=" + deliveryTime +
                ", finish=" + finish +
                ", tradeTime=" + tradeTime +
                ", imgUrl='" + imgUrl + '\'' +
                ", messages='" + messages + '\'' +
                ", orderDetailList=" + orderDetailList +
                ", couponId=" + couponId +
                '}';
    }
}