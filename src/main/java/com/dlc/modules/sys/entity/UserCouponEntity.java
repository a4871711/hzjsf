package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserCouponEntity implements Serializable {
    private static  final Long serialVersionUID=1L;
    private Long couponId;
    private Long userId;
    private String nickName;
    private String phone;
    private Long goodsId;
    private Long sysUserId;
    //'优惠券状态：（0：未使用 1：已使用 2：已过期）',
    private Integer couponStatus;

    private Date validityTime;

    private BigDecimal couponPrice;
    private BigDecimal limitPrice;
    private Integer couponType;
    private String storeAddrIds;
    private String couponTitle;
    private String fitCardIds;

    private Date createdDate;
    
    private Date payTime;
    private String storeName;
    private String sysUserName;

    /** 优惠券类型：（0：普通  1新人） */
    private Long couponNew;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Long sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String getSysUserName() {
		return sysUserName;
	}

	public void setSysUserName(String sysUserName) {
		this.sysUserName = sysUserName;
	}

	public Long getCouponNew() {
		return couponNew;
	}

	public void setCouponNew(Long couponNew) {
		this.couponNew = couponNew;
	}
}
