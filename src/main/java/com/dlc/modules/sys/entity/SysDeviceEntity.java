package com.dlc.modules.sys.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 我的设备
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-13 10:26:03
 */
public class SysDeviceEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
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

    private Integer status;

    private Integer inventory;

    private Integer type;

    private Date validityDate;

    private Date onLineTime;

    private Date createTime;

    private Long inOutNum;

    private Integer autoPay;

    private Integer selecteFree;

	/**
	 * 设置：设备ID
	 */
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * 获取：设备ID
	 */
	public Long getDeviceId() {
		return deviceId;
	}
	/**
	 * 设置：设备编号
	 */
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	/**
	 * 获取：设备编号
	 */
	public String getDeviceNo() {
		return deviceNo;
	}
	/**
	 * 设置：设备名
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	/**
	 * 获取：设备名
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * 设置：代理人Id（会员ID）
	 */
	public void setProxyId(Long proxyId) {
		this.proxyId = proxyId;
	}
	/**
	 * 获取：代理人Id（会员ID）
	 */
	public Long getProxyId() {
		return proxyId;
	}
	/**
	 * 设置：设备图
	 */
	public void setDeviceImgUrl(String deviceImgUrl) {
		this.deviceImgUrl = deviceImgUrl;
	}
	/**
	 * 获取：设备图
	 */
	public String getDeviceImgUrl() {
		return deviceImgUrl;
	}
	/**
	 * 设置：
	 */
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	/**
	 * 获取：
	 */
	public Long getStoreId() {
		return storeId;
	}
	/**
	 * 设置：代理人姓名
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	/**
	 * 获取：代理人姓名
	 */
	public String getProxyName() {
		return proxyName;
	}
	/**
	 * 设置：联系电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 获取：联系电话
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 设置：设备价格
	 */
	public void setDevicePrice(BigDecimal devicePrice) {
		this.devicePrice = devicePrice;
	}
	/**
	 * 获取：设备价格
	 */
	public BigDecimal getDevicePrice() {
		return devicePrice;
	}
	/**
	 * 设置：设备IMEI
	 */
	public void setImei(String imei) {
		this.imei = imei;
	}
	/**
	 * 获取：设备IMEI
	 */
	public String getImei() {
		return imei;
	}
	/**
	 * 设置：设备地址详情
	 */
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	/**
	 * 获取：设备地址详情
	 */
	public String getAddressDetail() {
		return addressDetail;
	}
	/**
	 * 设置：设备地址Id
	 */
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	/**
	 * 获取：设备地址Id
	 */
	public Long getAddressId() {
		return addressId;
	}
	/**
	 * 设置：设备数量（清单）
	 */
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
	/**
	 * 获取：设备数量（清单）
	 */
	public Integer getInventory() {
		return inventory;
	}
	/**
	 * 设置：设备在线时间
	 */
	public void setOnLineTime(Date onLineTime) {
		this.onLineTime = onLineTime;
	}
	/**
	 * 获取：设备在线时间
	 */
	public Date getOnLineTime() {
		return onLineTime;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	public Long getStoreAddressId() {
		return storeAddressId;
	}

	public void setStoreAddressId(Long storeAddressId) {
		this.storeAddressId = storeAddressId;
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

	public Integer getAutoPay() {
		return autoPay;
	}

	public void setAutoPay(Integer autoPay) {
		this.autoPay = autoPay;
	}
	public String getStoreAddrIds() {
		return storeAddrIds;
	}
	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
	}
	public Integer getSelecteFree() {
		return selecteFree;
	}
	public void setSelecteFree(Integer selecteFree) {
		this.selecteFree = selecteFree;
	}
	public Long getInOutNum() {
		return inOutNum;
	}
	public void setInOutNum(Long inOutNum) {
		this.inOutNum = inOutNum;
	}
	public Integer getValidity() {
		return validity;
	}
	public void setValidity(Integer validity) {
		this.validity = validity;
	}
	public Integer getBuyCount() {
		return buyCount;
	}
	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}
	public Integer getUsedCount() {
		return usedCount;
	}
	public void setUsedCount(Integer usedCount) {
		this.usedCount = usedCount;
	}
	public Integer getUseCount() {
		return useCount;
	}
	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}
	public String getNextPriceTitle3() {
		return nextPriceTitle3;
	}
	public void setNextPriceTitle3(String nextPriceTitle3) {
		this.nextPriceTitle3 = nextPriceTitle3;
	}
	public BigDecimal getNextPrice3() {
		return nextPrice3;
	}
	public void setNextPrice3(BigDecimal nextPrice3) {
		this.nextPrice3 = nextPrice3;
	}
	public String getNextPriceTitle2() {
		return nextPriceTitle2;
	}
	public void setNextPriceTitle2(String nextPriceTitle2) {
		this.nextPriceTitle2 = nextPriceTitle2;
	}
	public BigDecimal getNextPrice2() {
		return nextPrice2;
	}
	public void setNextPrice2(BigDecimal nextPrice2) {
		this.nextPrice2 = nextPrice2;
	}
	public String getNextPriceTitle() {
		return nextPriceTitle;
	}
	public void setNextPriceTitle(String nextPriceTitle) {
		this.nextPriceTitle = nextPriceTitle;
	}
	public BigDecimal getNextPrice() {
		return nextPrice;
	}
	public void setNextPrice(BigDecimal nextPrice) {
		this.nextPrice = nextPrice;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
