package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CardOrder implements Serializable {
    private Long cardOrderId;

    private Long userId;

    private Long cardId;

    private String orderNo;

    private String transactionNo;

    private BigDecimal paySum;

    private Long couponId;

    private Long deviceId;

    private Integer payType;

    private BigDecimal realPayment;

    private Integer wrist;

    private Integer autoPay;

    private Integer selecteFree;

    private BigDecimal wristPrice;

    private BigDecimal nextPrice;
    private String nextPriceTitle;
    private BigDecimal nextPrice2;
    private String nextPriceTitle2;
    private BigDecimal nextPrice3;
    private String nextPriceTitle3;
    private String agent5Ids;
    private String agent6Ids;
    private Integer useCount;

    private Long storeAddressId;
    private String storeAddrIds;

    private Long userAddressId;

    private Integer status;

    private Integer sendType;
    private Integer type;

    private Date validityDate;
    private Date oldValidityDate;

    private Date createdDate;

    private Date payTime;

    private Date finishTime;
    private Integer buyCount;

    private static final long serialVersionUID = 1L;

    public Long getCardOrderId() {
        return cardOrderId;
    }

    public void setCardOrderId(Long cardOrderId) {
        this.cardOrderId = cardOrderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
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

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
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

    public Integer getWrist() {
        return wrist;
    }

    public void setWrist(Integer wrist) {
        this.wrist = wrist;
    }

    public BigDecimal getWristPrice() {
        return wristPrice;
    }

    public void setWristPrice(BigDecimal wristPrice) {
        this.wristPrice = wristPrice;
    }

    public Long getStoreAddressId() {
        return storeAddressId;
    }

    public void setStoreAddressId(Long storeAddressId) {
        this.storeAddressId = storeAddressId;
    }

    public Long getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(Long userAddressId) {
        this.userAddressId = userAddressId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
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

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Integer autoPay) {
        this.autoPay = autoPay;
    }

    public BigDecimal getNextPrice() {
        return nextPrice;
    }

    public void setNextPrice(BigDecimal nextPrice) {
        this.nextPrice = nextPrice;
    }

    public Integer getSelecteFree() {
        return selecteFree;
    }

    public void setSelecteFree(Integer selecteFree) {
        this.selecteFree = selecteFree;
    }

	public String getNextPriceTitle() {
		return nextPriceTitle;
	}

	public void setNextPriceTitle(String nextPriceTitle) {
		this.nextPriceTitle = nextPriceTitle;
	}

	public BigDecimal getNextPrice2() {
		return nextPrice2;
	}

	public void setNextPrice2(BigDecimal nextPrice2) {
		this.nextPrice2 = nextPrice2;
	}

	public String getNextPriceTitle2() {
		return nextPriceTitle2;
	}

	public void setNextPriceTitle2(String nextPriceTitle2) {
		this.nextPriceTitle2 = nextPriceTitle2;
	}

	public BigDecimal getNextPrice3() {
		return nextPrice3;
	}

	public void setNextPrice3(BigDecimal nextPrice3) {
		this.nextPrice3 = nextPrice3;
	}

	public String getNextPriceTitle3() {
		return nextPriceTitle3;
	}

	public void setNextPriceTitle3(String nextPriceTitle3) {
		this.nextPriceTitle3 = nextPriceTitle3;
	}

    @Override
    public String toString() {
        return "CardOrder{" +
                "cardOrderId=" + cardOrderId +
                ", userId=" + userId +
                ", cardId=" + cardId +
                ", orderNo='" + orderNo + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", paySum=" + paySum +
                ", couponId=" + couponId +
                ", payType=" + payType +
                ", realPayment=" + realPayment +
                ", wrist=" + wrist +
                ", autoPay=" + autoPay +
                ", selecteFree=" + selecteFree +
                ", wristPrice=" + wristPrice +
                ", nextPrice=" + nextPrice +
                ", storeAddressId=" + storeAddressId +
                ", userAddressId=" + userAddressId +
                ", status=" + status +
                ", sendType=" + sendType +
                ", validityDate=" + validityDate +
                ", createdDate=" + createdDate +
                ", payTime=" + payTime +
                ", finishTime=" + finishTime +
                '}';
    }

	public Integer getUseCount() {
		return useCount;
	}

	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getStoreAddrIds() {
		return storeAddrIds;
	}

	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
	}

	public Date getOldValidityDate() {
		return oldValidityDate;
	}

	public void setOldValidityDate(Date oldValidityDate) {
		this.oldValidityDate = oldValidityDate;
	}

	public Integer getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}

	public String getAgent5Ids() {
		return agent5Ids;
	}

	public void setAgent5Ids(String agent5Ids) {
		this.agent5Ids = agent5Ids;
	}

	public String getAgent6Ids() {
		return agent6Ids;
	}

	public void setAgent6Ids(String agent6Ids) {
		this.agent6Ids = agent6Ids;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
}