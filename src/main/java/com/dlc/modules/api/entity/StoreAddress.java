package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class StoreAddress implements Serializable {
    private Long storeAddrId;

    private Long storeId;

    private String storeName;

    private String phone;

    private String province;

    private String city;

    private String zone;

    private String storeAddrDetail;

    private String longitude;

    private String latitude;

    private Byte status;

    private Date createdDate;

    private String agent5Ids;
    private String agent6Ids;
    
    private Integer goodsIdStoreId;

    private static final long serialVersionUID = 1L;

    public StoreAddress(Long storeAddrId, Long storeId, String storeName, String phone, String province, String city, String zone, String storeAddrDetail, String longitude, String latitude, Byte status, Date createdDate) {
        this.storeAddrId = storeAddrId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.zone = zone;
        this.storeAddrDetail = storeAddrDetail;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.createdDate = createdDate;
    }

    public StoreAddress() {
        super();
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
        this.storeName = storeName == null ? null : storeName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? null : zone.trim();
    }

    public String getStoreAddrDetail() {
        return storeAddrDetail;
    }

    public void setStoreAddrDetail(String storeAddrDetail) {
        this.storeAddrDetail = storeAddrDetail == null ? null : storeAddrDetail.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", storeAddrId=").append(storeAddrId);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", phone=").append(phone);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", zone=").append(zone);
        sb.append(", storeAddrDetail=").append(storeAddrDetail);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", status=").append(status);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
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

	public Integer getGoodsIdStoreId() {
		return goodsIdStoreId;
	}

	public void setGoodsIdStoreId(Integer goodsIdStoreId) {
		this.goodsIdStoreId = goodsIdStoreId;
	}
}