package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * lingkangming
 * 门店地址表
 */
public class SysStoreAddressEntity implements Serializable {
    //门店地址ID
    private Long storeAddrId;
    //门店ID
    private Long storeId;
    //门店名称
    private String storeName;
    //门店电话
    private String phone;
    //省
    private String province;
    //市
    private String city;
    //区
    private String zone;
    //门店详细地址
    private String storeAddrDetail;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    //地址有效状态0为无效，1有效
    private Integer status;
    //创建时间
    private Date createdDate;

    private String storePhone;
    
    private Integer goodsIdStoreId;

    /** 抖音核销门店 poi_id */
    private String douyinPoiId;

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public Long getStoreAddrId() {
        return storeAddrId;
    }

    public void setStoreAddrId(Long storeAddrId) {
        this.storeAddrId = storeAddrId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getStoreAddrDetail() {
        return storeAddrDetail;
    }

    public void setStoreAddrDetail(String storeAddrDetail) {
        this.storeAddrDetail = storeAddrDetail;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

	public Integer getGoodsIdStoreId() {
		return goodsIdStoreId;
	}

	public void setGoodsIdStoreId(Integer goodsIdStoreId) {
		this.goodsIdStoreId = goodsIdStoreId;
	}

	public String getDouyinPoiId() {
		return douyinPoiId;
	}

	public void setDouyinPoiId(String douyinPoiId) {
		this.douyinPoiId = douyinPoiId;
	}
}
