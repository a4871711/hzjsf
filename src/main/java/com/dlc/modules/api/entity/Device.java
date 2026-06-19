package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Device implements Serializable {
    private Long deviceId;

    private String deviceNo;

    private String deviceName;

    private Long proxyId;

    private String deviceImgUrl;

    private Long storeId;

    private Long storeAddressId;
    private String storeAddrIds;

    private String proxyName;

    private String phone;

    private BigDecimal devicePrice;

    private BigDecimal nextPrice;
    private String nextPriceTitle;
    private BigDecimal nextPrice2;
    private String nextPriceTitle2;
    private BigDecimal nextPrice3;
    private String nextPriceTitle3;
    private Integer useCount;
    private Integer usedCount;
    private Integer buyCount;
    private Integer validity;

    private String imei;

    private String addressDetail;

    private Long addressId;

    private Long fitCardId;

    private Byte status;

    private Integer inventory;

    private Integer type;

    private Date validityDate;

    private Date onLineTime;

    private Date createTime;

    private Long inOutNum;

    private Integer autoPay;

    private Integer selecteFree;

    private static final long serialVersionUID = 1L;

    public Device() {
        super();
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo == null ? null : deviceNo.trim();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName == null ? null : deviceName.trim();
    }

    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }

    public String getDeviceImgUrl() {
        return deviceImgUrl;
    }

    public void setDeviceImgUrl(String deviceImgUrl) {
        this.deviceImgUrl = deviceImgUrl == null ? null : deviceImgUrl.trim();
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getStoreAddressId() {
        return storeAddressId;
    }

    public void setStoreAddressId(Long storeAddressId) {
        this.storeAddressId = storeAddressId;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName == null ? null : proxyName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public BigDecimal getDevicePrice() {
        return devicePrice;
    }

    public void setDevicePrice(BigDecimal devicePrice) {
        this.devicePrice = devicePrice;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail == null ? null : addressDetail.trim();
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Date getOnLineTime() {
        return onLineTime;
    }

    public void setOnLineTime(Date onLineTime) {
        this.onLineTime = onLineTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public Long getFitCardId() {
        return fitCardId;
    }

    public void setFitCardId(Long fitCardId) {
        this.fitCardId = fitCardId;
    }

    public Long getInOutNum() {
        return inOutNum;
    }

    public void setInOutNum(Long inOutNum) {
        this.inOutNum = inOutNum;
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

    @Override
    public String toString() {
        return "Device{" +
                "deviceId=" + deviceId +
                ", deviceNo='" + deviceNo + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", proxyId=" + proxyId +
                ", deviceImgUrl='" + deviceImgUrl + '\'' +
                ", storeId=" + storeId +
                ", storeAddressId=" + storeAddressId +
                ", proxyName='" + proxyName + '\'' +
                ", phone='" + phone + '\'' +
                ", devicePrice=" + devicePrice +
                ", nextPrice=" + nextPrice +
                ", imei='" + imei + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", addressId=" + addressId +
                ", fitCardId=" + fitCardId +
                ", status=" + status +
                ", inventory=" + inventory +
                ", type=" + type +
                ", validityDate=" + validityDate +
                ", onLineTime=" + onLineTime +
                ", createTime=" + createTime +
                ", inOutNum=" + inOutNum +
                ", autoPay=" + autoPay +
                ", selecteFree=" + selecteFree +
                '}';
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

	public Integer getUseCount() {
		return useCount;
	}

	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}

	public Integer getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(Integer usedCount) {
		this.usedCount = usedCount;
	}

	public Integer getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}

	public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}

	public String getStoreAddrIds() {
		return storeAddrIds;
	}

	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
	}
}