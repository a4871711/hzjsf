package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class UserAddress implements Serializable {
    private Long userAddressId;

    private Long userId;

    private String userName;

    private String phone;

    private String province;

    private String city;

    private String zone;

    private String addr;

    private String address;

    private Byte isDefault;

    private Byte status;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public UserAddress(Long userAddressId, Long userId, String userName, String phone, String province, String city, String zone, String addr, Byte isDefault, Byte status, Date createdDate) {
        this.userAddressId = userAddressId;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.zone = zone;
        this.addr = addr;
        this.isDefault = isDefault;
        this.status = status;
        this.createdDate = createdDate;
    }

    public UserAddress() {
        super();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(Long userAddressId) {
        this.userAddressId = userAddressId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public Byte getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
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
        sb.append(", userAddressId=").append(userAddressId);
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", phone=").append(phone);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", zone=").append(zone);
        sb.append(", addr=").append(addr);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", status=").append(status);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}