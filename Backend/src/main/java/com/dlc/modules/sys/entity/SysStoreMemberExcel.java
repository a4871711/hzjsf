package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * 门店会员
 */
public class SysStoreMemberExcel implements Serializable {
    //设备ID
    private Long deviceId;
    private Long userId;
    private String nickname;
    private String phone;
    private Integer sex;
    private String birthday;
    private String height;
    private String weight;
    private String remark;
    private Integer faceStatus;
    private Date createdDate;
    private String storeName;
    private Integer status;
    private Long inOutNum;
    private Integer type;
    private String ctName;
    private String cardName;
    private String validityDate;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Integer getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(Integer faceStatus) {
        this.faceStatus = faceStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getInOutNum() {
        return inOutNum;
    }

    public void setInOutNum(Long inOutNum) {
        this.inOutNum = inOutNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCtName() {
        return ctName;
    }

    public void setCtName(String ctName) {
        this.ctName = ctName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(String validityDate) {
        this.validityDate = validityDate;
    }

    @Override
    public String toString() {
        return "SysStoreMemberExcel{" +
                "deviceId=" + deviceId +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", faceStatus=" + faceStatus +
                ", createdDate=" + createdDate +
                ", storeName='" + storeName + '\'' +
                ", status=" + status +
                ", inOutNum=" + inOutNum +
                ", type=" + type +
                ", ctName='" + ctName + '\'' +
                ", cardName='" + cardName + '\'' +
                ", validityDate='" + validityDate + '\'' +
                '}';
    }
}
