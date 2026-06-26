package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Coupon implements Serializable {
    private Long couponId;

    private Long userId;

    private Long goodsId;

    private Integer couponStatus;

    private Date validityTime;

    private BigDecimal couponPrice;
    private BigDecimal limitPrice;
    private Integer couponType;
    private String storeAddrIds;
    private String couponTitle;
    private String fitCardIds;

    private Date createdDate;

    /** 优惠券类型：（0：普通  1新人） */
    private Long couponNew;

    private static final long serialVersionUID = 1L;

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
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

    public Integer getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(Integer couponStatus) {
        this.couponStatus = couponStatus;
    }

    public Date getValidityTime() {
        return validityTime;
    }

    public void setValidityTime(Date validityTime) {
        this.validityTime = validityTime;
    }

    public BigDecimal getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(BigDecimal couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "couponId=" + couponId +
                ", userId=" + userId +
                ", goodsId=" + goodsId +
                ", couponStatus=" + couponStatus +
                ", validityTime=" + validityTime +
                ", couponPrice=" + couponPrice +
                ", createdDate=" + createdDate +
                '}';
    }

	public BigDecimal getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(BigDecimal limitPrice) {
		this.limitPrice = limitPrice;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public String getStoreAddrIds() {
		return storeAddrIds;
	}

	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
	}

	public String getCouponTitle() {
		return couponTitle;
	}

	public void setCouponTitle(String couponTitle) {
		this.couponTitle = couponTitle;
	}

	public String getFitCardIds() {
		return fitCardIds;
	}

	public void setFitCardIds(String fitCardIds) {
		this.fitCardIds = fitCardIds;
	}

	public Long getCouponNew() {
		return couponNew;
	}

	public void setCouponNew(Long couponNew) {
		this.couponNew = couponNew;
	}
}