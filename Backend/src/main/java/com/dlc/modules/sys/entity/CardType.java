package com.dlc.modules.sys.entity;

import java.io.Serializable;

public class CardType implements Serializable {
    private Long ctId;

    private Integer cardType;

    private String ctName;

    private static final long serialVersionUID = 1L;

    public CardType(Long ctId, Integer cardType, String ctName) {
        this.ctId = ctId;
        this.cardType = cardType;
        this.ctName = ctName;
    }

    public CardType() {
        super();
    }

    public Long getCtId() {
        return ctId;
    }

    public void setCtId(Long ctId) {
        this.ctId = ctId;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getCtName() {
        return ctName;
    }

    public void setCtName(String ctName) {
        this.ctName = ctName == null ? null : ctName.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ctId=").append(ctId);
        sb.append(", cardType=").append(cardType);
        sb.append(", ctName=").append(ctName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}