package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FitCard implements Serializable {
    private Long fitCardId;

    private Integer cardType;
    /** 卡种性质(全新维度,与cardType无关):0普通卡 1权益卡性质(可作为VIP权益卡"可绑定会员卡"候选) */
    private Integer cardNature;
    private String cardName;

    private BigDecimal cardPrice;

    /** 新人专享价（无有效购卡记录时使用） */
    private BigDecimal newUserPrice;

    private BigDecimal costPrice;

    private Integer validity;

    private String cardImgUrl;

    private String cardRule;
    private String storeAddrIds;
    private String storeId;//搜索用，实际也是 storeAddrId
    private String ctName;
    private Integer autoPay;
    private BigDecimal nextPrice;
    private String nextPriceTitle;
    private BigDecimal nextPrice2;
    private String nextPriceTitle2;
    private BigDecimal nextPrice3;
    private String nextPriceTitle3;
    private Integer useCount;

    private Date createdDate;
    private String showStoreAddrIds;

    private static final long serialVersionUID = 1L;

    public Long getFitCardId() {
        return fitCardId;
    }

    public void setFitCardId(Long fitCardId) {
        this.fitCardId = fitCardId;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public Integer getCardNature() {
        return cardNature;
    }

    public void setCardNature(Integer cardNature) {
        this.cardNature = cardNature;
    }

    public BigDecimal getCardPrice() {
        return cardPrice;
    }

    public void setCardPrice(BigDecimal cardPrice) {
        this.cardPrice = cardPrice;
    }

    public BigDecimal getNewUserPrice() {
        return newUserPrice;
    }

    public void setNewUserPrice(BigDecimal newUserPrice) {
        this.newUserPrice = newUserPrice;
    }

    /** 应付单价：新人 newUserPrice → cardPrice → costPrice(原价)；老用户 cardPrice → costPrice */
    public BigDecimal resolveSalePrice(boolean isNewUser) {
        if (isNewUser) {
            return firstPositivePrice(newUserPrice, cardPrice, costPrice);
        }
        return firstPositivePrice(cardPrice, costPrice);
    }

    private static BigDecimal firstPositivePrice(BigDecimal... candidates) {
        if (candidates == null) {
            return BigDecimal.ZERO;
        }
        for (BigDecimal price : candidates) {
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                return price;
            }
        }
        return BigDecimal.ZERO;
    }

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    public String getCardImgUrl() {
        return cardImgUrl;
    }

    public void setCardImgUrl(String cardImgUrl) {
        this.cardImgUrl = cardImgUrl;
    }

    public String getCardRule() {
        return cardRule;
    }

    public void setCardRule(String cardRule) {
        this.cardRule = cardRule;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    @Override
    public String toString() {
        return "FitCard{" +
                "fitCardId=" + fitCardId +
                ", cardType=" + cardType +
                ", cardPrice=" + cardPrice +
                ", costPrice=" + costPrice +
                ", validity=" + validity +
                ", cardImgUrl='" + cardImgUrl + '\'' +
                ", cardRule='" + cardRule + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

	public String getStoreAddrIds() {
		return storeAddrIds;
	}

	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getCtName() {
		return ctName;
	}

	public void setCtName(String ctName) {
		this.ctName = ctName;
	}

	public Integer getAutoPay() {
		return autoPay;
	}

	public void setAutoPay(Integer autoPay) {
		this.autoPay = autoPay;
	}

	public BigDecimal getNextPrice() {
		return nextPrice;
	}

	public void setNextPrice(BigDecimal nextPrice) {
		this.nextPrice = nextPrice;
	}

	public String getNextPriceTitle() {
		return nextPriceTitle;
	}

	public void setNextPriceTitle(String nextPriceTitle) {
		this.nextPriceTitle = nextPriceTitle;
	}

	public BigDecimal getNextPrice2() {
		return nextPrice2;
	}

	public void setNextPrice2(BigDecimal nextPrice2) {
		this.nextPrice2 = nextPrice2;
	}

	public String getNextPriceTitle2() {
		return nextPriceTitle2;
	}

	public void setNextPriceTitle2(String nextPriceTitle2) {
		this.nextPriceTitle2 = nextPriceTitle2;
	}

	public BigDecimal getNextPrice3() {
		return nextPrice3;
	}

	public void setNextPrice3(BigDecimal nextPrice3) {
		this.nextPrice3 = nextPrice3;
	}

	public String getNextPriceTitle3() {
		return nextPriceTitle3;
	}

	public void setNextPriceTitle3(String nextPriceTitle3) {
		this.nextPriceTitle3 = nextPriceTitle3;
	}

	public Integer getUseCount() {
		return useCount;
	}

	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getShowStoreAddrIds() {
		return showStoreAddrIds;
	}

	public void setShowStoreAddrIds(String showStoreAddrIds) {
		this.showStoreAddrIds = showStoreAddrIds;
	}
}