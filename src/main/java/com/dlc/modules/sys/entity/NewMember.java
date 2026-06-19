package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

public class NewMember implements Serializable {
    private Long deviceId;

    private Long userId;

    private Long storeId;
    
    private Long newStoreAddrId;

    private Date validityDate;

    private Date newValidityDate;
    private Integer newValidityDay;
    private Integer newValidityType;

    private static final long serialVersionUID = 1L;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public Date getNewValidityDate() {
        return newValidityDate;
    }

    public void setNewValidityDate(Date newValidityDate) {
        this.newValidityDate = newValidityDate;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "NewMember{" +
                "deviceId=" + deviceId +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", validityDate=" + validityDate +
                ", newValidityDate=" + newValidityDate +
                '}';
    }

	public Integer getNewValidityDay() {
		return newValidityDay;
	}

	public void setNewValidityDay(Integer newValidityDay) {
		this.newValidityDay = newValidityDay;
	}

	public Integer getNewValidityType() {
		return newValidityType;
	}

	public void setNewValidityType(Integer newValidityType) {
		this.newValidityType = newValidityType;
	}

	public Long getNewStoreAddrId() {
		return newStoreAddrId;
	}

	public void setNewStoreAddrId(Long newStoreAddrId) {
		this.newStoreAddrId = newStoreAddrId;
	}
}
