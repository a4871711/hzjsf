package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 收支明细表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:19
 */
@TableName("income_pay_detail")
public class SysIncomePayDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 明细ID
	 */
	@TableId
	private Long incomePayDetailId;
	/**
	 * 用户ID
	 */
	private Long userId;
	//门店id
	private Long storeId;

	private String userName;

	private String phone;
	
	private Date oldValidityDate;
	private Date validityDate;
	private int autoPay;
	private int buyCount;
	private int cardType;
	private BigDecimal paySum;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	private String storeName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 流水号
	 */
	private String transactionNumber;
	/**
	 * 支付用途（1 提现 2.会员卡购买 3.商城购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.商城退款 10.其他）
	 */
	private Integer payType;
	/**
	 * 金额数
	 */
	private BigDecimal money;
	/**
	 * 收支时间
	 */

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date tradeDate;
	/**
	 * 收支方式(1:钱包 2：微信支付 3：支付宝支付)
	 */
	private Integer tradeType;
	/**
	 * 微信openId
	 */
	private String openId;
	/**
	 * 状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
	 */
	private Integer tradeStatus;
	/**
	 * 审核通过时间
	 */

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date checkedTime;
	/**
	 * 交易完成时间
	 */

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date transactionTime;
	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdDate;
	
	@TableField(exist = false)
	private String payTypeDesc;
	
	@TableField(exist = false)
	private String ctName;
	
	@TableField(exist = false)
	private BigDecimal couponMoney;

	/** 购卡/续费来源：自动、用户、后台 */
	@TableField(exist = false)
	private String renewSourceDesc;

	/** 卡片分类：0普通卡 1权益卡（关联 fit_card.cardNature，非卡订单为 null） */
	@TableField(exist = false)
	private Integer cardNature;

	/**
	 * 设置：明细ID
	 */
	public void setIncomePayDetailId(Long incomePayDetailId) {
		this.incomePayDetailId = incomePayDetailId;
	}
	/**
	 * 获取：明细ID
	 */
	public Long getIncomePayDetailId() {
		return incomePayDetailId;
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
	 * 设置：订单号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取：订单号
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置：流水号
	 */
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	/**
	 * 获取：流水号
	 */
	public String getTransactionNumber() {
		return transactionNumber;
	}
	/**
	 * 设置：支付用途（1 提现 2.会员卡购买 3.商城购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.商城退款 10.其他）
	 */
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	/**
	 * 获取：支付用途（1 提现 2.会员卡购买 3.商城购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.商城退款 10.其他）
	 */
	public Integer getPayType() {
		return payType;
	}
	/**
	 * 设置：金额数
	 */
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	/**
	 * 获取：金额数
	 */
	public BigDecimal getMoney() {
		return money;
	}
	/**
	 * 设置：收支时间
	 */
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	/**
	 * 获取：收支时间
	 */
	public Date getTradeDate() {
		return tradeDate;
	}
	/**
	 * 设置：收支方式(1:钱包 2：微信支付 3：支付宝支付)
	 */
	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}
	/**
	 * 获取：收支方式(1:钱包 2：微信支付 3：支付宝支付)
	 */
	public Integer getTradeType() {
		return tradeType;
	}
	/**
	 * 设置：微信openId
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * 获取：微信openId
	 */
	public String getOpenId() {
		return openId;
	}
	/**
	 * 设置：状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
	 */
	public void setTradeStatus(Integer tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	/**
	 * 获取：状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
	 */
	public Integer getTradeStatus() {
		return tradeStatus;
	}
	/**
	 * 设置：审核通过时间
	 */
	public void setCheckedTime(Date checkedTime) {
		this.checkedTime = checkedTime;
	}
	/**
	 * 获取：审核通过时间
	 */
	public Date getCheckedTime() {
		return checkedTime;
	}
	/**
	 * 设置：交易完成时间
	 */
	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}
	/**
	 * 获取：交易完成时间
	 */
	public Date getTransactionTime() {
		return transactionTime;
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

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

	public Date getOldValidityDate() {
		return oldValidityDate;
	}

	public void setOldValidityDate(Date oldValidityDate) {
		this.oldValidityDate = oldValidityDate;
	}

	public Date getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(Date validityDate) {
		this.validityDate = validityDate;
	}

	public int getAutoPay() {
		return autoPay;
	}

	public void setAutoPay(int autoPay) {
		this.autoPay = autoPay;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public BigDecimal getPaySum() {
		return paySum;
	}

	public void setPaySum(BigDecimal paySum) {
		this.paySum = paySum;
	}

	public void setPayTypeDesc(String payTypeDesc) {
		this.payTypeDesc = payTypeDesc;
	}

	public void setCtName(String ctName) {
		this.ctName = ctName;
	}

	public void setCouponMoney(BigDecimal couponMoney) {
		this.couponMoney = couponMoney;
	}

	public String getPayTypeDesc() {
		return payTypeDesc;
	}

	public String getCtName() {
		return ctName;
	}

	public BigDecimal getCouponMoney() {
		return couponMoney;
	}

	public String getRenewSourceDesc() {
		return renewSourceDesc;
	}

	public void setRenewSourceDesc(String renewSourceDesc) {
		this.renewSourceDesc = renewSourceDesc;
	}

	public Integer getCardNature() {
		return cardNature;
	}

	public void setCardNature(Integer cardNature) {
		this.cardNature = cardNature;
	}
}
