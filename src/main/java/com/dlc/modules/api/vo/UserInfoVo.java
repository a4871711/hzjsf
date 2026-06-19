package com.dlc.modules.api.vo;

import com.dlc.common.validator.group.AddGroup;
import com.dlc.common.validator.group.LoginGroup;
import com.dlc.common.validator.group.UpdateGroup;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


public class UserInfoVo implements Serializable {
    private Long userId;
    /**用户昵称*/
    private String nickname;
    /**手机号*/
    private String phone;
    @NotNull(message="密码不能为空",groups = {AddGroup.class,UpdateGroup.class, LoginGroup.class})
    private String password;
    /**性别，默认0，未知性别，1男，2女*/
    private Integer sex;
    /**头像*/
    private String headImgUrl;
    /**审核状态*/
    private Integer auditStatus;
    /**公众号或手机端微信openid*/
    private String openId;

    private String wristId;

    private String rongCloudId;

    private String rongCloudToken;

    private String deviceToken;

    private String height;

    private String weight;
    private String contractId;
    /**
     * ios授权用户唯一标识
     */
    private String iosUserId;
    private Integer faceStatus;
    private Integer wtState;
    
    //登录token
    private String token;
    //当前门店id
    private int nowStoreId;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getWristId() {
        return wristId;
    }

    public void setWristId(String wristId) {
        this.wristId = wristId;
    }

    public String getRongCloudId() {
        return rongCloudId;
    }

    public void setRongCloudId(String rongCloudId) {
        this.rongCloudId = rongCloudId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getRongCloudToken() {
        return rongCloudToken;
    }

    public void setRongCloudToken(String rongCloudToken) {
        this.rongCloudToken = rongCloudToken;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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

    public String getIosUserId() {
        return iosUserId;
    }

    public void setIosUserId(String iosUserId) {
        this.iosUserId = iosUserId;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    @Override
    public String toString() {
        return "UserInfoVo{" +
                "userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", auditStatus=" + auditStatus +
                ", openId='" + openId + '\'' +
                ", wristId='" + wristId + '\'' +
                ", rongCloudId='" + rongCloudId + '\'' +
                ", rongCloudToken='" + rongCloudToken + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", faceStatus=" + faceStatus +
                '}';
    }

	public int getNowStoreId() {
		return nowStoreId;
	}

	public void setNowStoreId(int nowStoreId) {
		this.nowStoreId = nowStoreId;
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
}