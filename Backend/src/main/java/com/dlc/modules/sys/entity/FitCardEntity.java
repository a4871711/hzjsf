package com.dlc.modules.sys.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 健身卡(会员卡)
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-10 10:24:28
 */
public class FitCardEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 卡ID
	 */
	private Long fitCardId;
	/**
	 * 卡片类型（0：月卡1:季卡2：半年卡3：年卡）
	 */
	private Integer cardType;
	
	private String cardName;
	/**
	 * 单价
	 */
	private Double cardPrice;
	/**
	 * 新人专享价
	 */
	private Double newUserPrice;
	/**
	 * 原价
	 */
	private Double costPrice;
	/**
	 * 权益卡价格（权益会员专享价，NULL/0=不启用）
	 */
	private Double benefitPrice;
	/**
	 * 次月起单价
	 */
	private Double nextPrice;
	private String nextPriceTitle;
	private Double nextPrice2;
	private String nextPriceTitle2;
	private Double nextPrice3;
	private String nextPriceTitle3;
	private int useCount;
	private String storeAddrIds;
	/**
	 * 有效期
	 */
	private Integer validity;
	/**
	 * 自动续费类型： 1：是  2：否
	 */
	private Integer autoPay;
	/**
	 * 状态： 1：上架  2：下架
	 */
	private Integer status;
	/**
	 * 卡图
	 */
	private String cardImgUrl;
	/**
	 * 会员规则
	 */
	private String cardRule;

	private String ctName;
	/**
	 * 创建时间
	 */
	private Date createdDate;
	
	private String showStoreAddrIds;

	public FitCardEntity(Long fitCardId, Integer cardType, Double cardPrice, Double costPrice, Double nextPrice, Integer validity, Integer autoPay, Integer status, String cardImgUrl, String cardRule, String ctName, Date createdDate) {
		this.fitCardId = fitCardId;
		this.cardType = cardType;
		this.cardPrice = cardPrice;
		this.costPrice = costPrice;
		this.nextPrice = nextPrice;
		this.validity = validity;
		this.autoPay = autoPay;
		this.status = status;
		this.cardImgUrl = cardImgUrl;
		this.cardRule = cardRule;
		this.ctName = ctName;
		this.createdDate = createdDate;
	}

	public FitCardEntity() {
		super();
	}

	/**
	 * 设置：卡ID
	 */
	public void setFitCardId(Long fitCardId) {
		this.fitCardId = fitCardId;
	}
	/**
	 * 获取：卡ID
	 */
	public Long getFitCardId() {
		return fitCardId;
	}
	/**
	 * 设置：卡片类型（0：月卡1:季卡2：半年卡3：年卡）
	 */
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	/**
	 * 获取：卡片类型（0：月卡1:季卡2：半年卡3：年卡）
	 */
	public Integer getCardType() {
		return cardType;
	}
	/**
	 * 设置：单价
	 */
	public void setCardPrice(Double cardPrice) {
		this.cardPrice = cardPrice;
	}
	/**
	 * 获取：单价
	 */
	public Double getCardPrice() {
		return cardPrice;
	}

	public Double getNewUserPrice() {
		return newUserPrice;
	}

	public void setNewUserPrice(Double newUserPrice) {
		this.newUserPrice = newUserPrice;
	}

	/**
	 * 设置：有效期
	 */
	public void setValidity(Integer validity) {
		this.validity = validity;
	}
	/**
	 * 获取：有效期
	 */
	public Integer getValidity() {
		return validity;
	}
	/**
	 * 设置：卡图
	 */
	public void setCardImgUrl(String cardImgUrl) {
		this.cardImgUrl = cardImgUrl;
	}
	/**
	 * 获取：卡图
	 */
	public String getCardImgUrl() {
		return cardImgUrl;
	}
	/**
	 * 设置：会员规则
	 */
	public void setCardRule(String cardRule) {
		this.cardRule = cardRule;
	}
	/**
	 * 获取：会员规则
	 */
	public String getCardRule() {
		return cardRule;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreatedDate() {
		return createdDate;
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

	public Double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}

    public Double getNextPrice() {
        return nextPrice;
    }

    public void setNextPrice(Double nextPrice) {
        this.nextPrice = nextPrice;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNextPriceTitle() {
		return nextPriceTitle;
	}

	public void setNextPriceTitle(String nextPriceTitle) {
		this.nextPriceTitle = nextPriceTitle;
	}

	public Double getNextPrice2() {
		return nextPrice2;
	}

	public void setNextPrice2(Double nextPrice2) {
		this.nextPrice2 = nextPrice2;
	}

	public String getNextPriceTitle2() {
		return nextPriceTitle2;
	}

	public void setNextPriceTitle2(String nextPriceTitle2) {
		this.nextPriceTitle2 = nextPriceTitle2;
	}

	public Double getNextPrice3() {
		return nextPrice3;
	}

	public void setNextPrice3(Double nextPrice3) {
		this.nextPrice3 = nextPrice3;
	}

	public String getNextPriceTitle3() {
		return nextPriceTitle3;
	}

	public void setNextPriceTitle3(String nextPriceTitle3) {
		this.nextPriceTitle3 = nextPriceTitle3;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public String getStoreAddrIds() {
		return storeAddrIds;
	}

	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
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

	public Double getBenefitPrice() {
		return benefitPrice;
	}

	public void setBenefitPrice(Double benefitPrice) {
		this.benefitPrice = benefitPrice;
	}
}
