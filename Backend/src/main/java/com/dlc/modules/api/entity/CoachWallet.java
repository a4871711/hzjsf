package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CoachWallet implements Serializable {
    private Long coachWalletId;

    private Long coachId;

    private BigDecimal money;

    private String alipay;

    private String realName;

    private Date updateDate;

    private static final long serialVersionUID = 1L;

    public CoachWallet(Long coachWalletId, Long coachId, BigDecimal money, String alipay, String realName, Date updateDate) {
        this.coachWalletId = coachWalletId;
        this.coachId = coachId;
        this.money = money;
        this.alipay = alipay;
        this.realName = realName;
        this.updateDate = updateDate;
    }

    public CoachWallet() {
        super();
    }

    public Long getCoachWalletId() {
        return coachWalletId;
    }

    public void setCoachWalletId(Long coachWalletId) {
        this.coachWalletId = coachWalletId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
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
        this.alipay = alipay == null ? null : alipay.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", coachWalletId=").append(coachWalletId);
        sb.append(", coachId=").append(coachId);
        sb.append(", money=").append(money);
        sb.append(", alipay=").append(alipay);
        sb.append(", realName=").append(realName);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}