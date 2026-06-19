package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserWallet implements Serializable {
    private Long userWalletId;

    private Long userId;

    private BigDecimal money;

    private String alipay;

    private String realName;

    private Date updateDate;

    private static final long serialVersionUID = 1L;

    public Long getUserWalletId() {
        return userWalletId;
    }

    public void setUserWalletId(Long userWalletId) {
        this.userWalletId = userWalletId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "UserWallet{" +
                "userWalletId=" + userWalletId +
                ", userId=" + userId +
                ", money=" + money +
                ", alipay='" + alipay + '\'' +
                ", realName='" + realName + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}