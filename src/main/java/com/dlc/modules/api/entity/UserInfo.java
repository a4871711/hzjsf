package com.dlc.modules.api.entity;

import com.dlc.common.validator.group.AddGroup;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    private Long userId;

    private String openId;

    private String gzhOpenId;

    private String zfbOpenId;

    private String nickname;

    private String headImgUrl;

    private Byte level;

    private String qrCode;

    private String wristId;

    private Integer sex;

    private String height;

    private String weight;
    /**手机号*/
    @NotBlank(message="手机号不能为空",groups = {AddGroup.class})
    private String phone;

    /**生日*/
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**用户密码*/
    //@NotBlank(message="密码不能为空",groups = {AddGroup.class})
    private String password;

    private String interest;

    private String userSign;
    /**
     * ios授权用户唯一标识
     */
    private String iosUserId;

    /**融云ID*/
    private String rongCloudId;
    /**融云token*/
    private String rongCloudToken;

    private String deviceToken;

    private Integer userType;
    /*人脸认证状态0：未购卡 1：已购卡已认证 2：已购卡未认证*/
    private Integer faceStatus;

    private Date createdDate;
    //代扣签约字段、委托代扣用户openid
    private String wtOpenId;
    //签约状态： 0：未签约  1：已签约 2：已解约
    private Integer wtState;
    //委托代扣协议id
    private String contractId;
    //签约、解约时间
    private Date contractTime;
    
    private int nowStoreId;
    private Integer auditStatus;

    private static final long serialVersionUID = 1L;

    public UserInfo() {
        super();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl == null ? null : headImgUrl.trim();
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode == null ? null : qrCode.trim();
    }

    public String getWristId() {
        return wristId;
    }

    public void setWristId(String wristId) {
        this.wristId = wristId == null ? null : wristId.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height == null ? null : height.trim();
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight == null ? null : weight.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest == null ? null : interest.trim();
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign == null ? null : userSign.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRongCloudId() {
        return rongCloudId;
    }

    public void setRongCloudId(String rongCloudId) {
        this.rongCloudId = rongCloudId;
    }

    public String getRongCloudToken() {
        return rongCloudToken;
    }

    public void setRongCloudToken(String rongCloudToken) {
        this.rongCloudToken = rongCloudToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(Integer faceStatus) {
        this.faceStatus = faceStatus;
    }

    public String getGzhOpenId() {
        return gzhOpenId;
    }

    public void setGzhOpenId(String gzhOpenId) {
        this.gzhOpenId = gzhOpenId;
    }

    public String getZfbOpenId() {
        return zfbOpenId;
    }

    public void setZfbOpenId(String zfbOpenId) {
        this.zfbOpenId = zfbOpenId;
    }

    public String getIosUserId() {
        return iosUserId;
    }

    public void setIosUserId(String iosUserId) {
        this.iosUserId = iosUserId;
    }

    public String getWtOpenId() {
        return wtOpenId;
    }

    public void setWtOpenId(String wtOpenId) {
        this.wtOpenId = wtOpenId;
    }

    public Integer getWtState() {
        return wtState;
    }

    public void setWtState(Integer wtState) {
        this.wtState = wtState;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", openId='" + openId + '\'' +
                ", gzhOpenId='" + gzhOpenId + '\'' +
                ", zfbOpenId='" + zfbOpenId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", level=" + level +
                ", qrCode='" + qrCode + '\'' +
                ", wristId='" + wristId + '\'' +
                ", sex=" + sex +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", phone='" + phone + '\'' +
                ", birthday=" + birthday +
                ", password='" + password + '\'' +
                ", interest='" + interest + '\'' +
                ", userSign='" + userSign + '\'' +
                ", iosUserId='" + iosUserId + '\'' +
                ", rongCloudId='" + rongCloudId + '\'' +
                ", rongCloudToken='" + rongCloudToken + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", userType=" + userType +
                ", faceStatus=" + faceStatus +
                ", createdDate=" + createdDate +
                ", wtOpenId='" + wtOpenId + '\'' +
                ", wtState=" + wtState +
                ", contractId='" + contractId + '\'' +
                ", contractTime=" + contractTime +
                '}';
    }

	public int getNowStoreId() {
		return nowStoreId;
	}

	public void setNowStoreId(int nowStoreId) {
		this.nowStoreId = nowStoreId;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
}