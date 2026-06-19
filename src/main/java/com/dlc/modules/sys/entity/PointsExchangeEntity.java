package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 积分兑换明细表
 * 
 * @author LINGKANGMING
 * @email 1647595314@qq.com
 * @date 2018-12-30 14:19:37
 */
public class PointsExchangeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//兑换ID
	private Long pointsExchangeId;
	//用户ID
	private Long userId;

	private Long storeId;
	//明细类别（1:运动积分 2：积分兑换）
	private Integer detailType;
	//兑换积分数
	private Integer pointsCount;
	//兑换金额
	private BigDecimal exchangeMoney;
	//兑换时间
	private Date exchangeDate;

	private String nickname;

	private String phone;

	private String storeName;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 设置：兑换ID
	 */
	public void setPointsExchangeId(Long pointsExchangeId) {
		this.pointsExchangeId = pointsExchangeId;
	}
	/**
	 * 获取：兑换ID
	 */
	public Long getPointsExchangeId() {
		return pointsExchangeId;
	}
	/**
	 * 设置：用户ID
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户ID
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：明细类别（1:运动积分 2：积分兑换）
	 */
	public void setDetailType(Integer detailType) {
		this.detailType = detailType;
	}
	/**
	 * 获取：明细类别（1:运动积分 2：积分兑换）
	 */
	public Integer getDetailType() {
		return detailType;
	}
	/**
	 * 设置：兑换积分数
	 */
	public void setPointsCount(Integer pointsCount) {
		this.pointsCount = pointsCount;
	}
	/**
	 * 获取：兑换积分数
	 */
	public Integer getPointsCount() {
		return pointsCount;
	}
	/**
	 * 设置：兑换金额
	 */
	public void setExchangeMoney(BigDecimal exchangeMoney) {
		this.exchangeMoney = exchangeMoney;
	}
	/**
	 * 获取：兑换金额
	 */
	public BigDecimal getExchangeMoney() {
		return exchangeMoney;
	}
	/**
	 * 设置：兑换时间
	 */
	public void setExchangeDate(Date exchangeDate) {
		this.exchangeDate = exchangeDate;
	}
	/**
	 * 获取：兑换时间
	 */
	public Date getExchangeDate() {
		return exchangeDate;
	}

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
}
