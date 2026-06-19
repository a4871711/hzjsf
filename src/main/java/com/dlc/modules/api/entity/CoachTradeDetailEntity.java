package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 教练收支明细
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-10-20 14:50:47
 */
public class CoachTradeDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//明细id
	private Long id;
	//教练ID
	private Long coachId;
	//交易类型（1.课程报酬入账 2.课程入账退款 ）
	private Integer tradeType;
	//金额数
	private BigDecimal money;
	//原价金额
	private BigDecimal origMoney;
	//提成比例
	private Double percent;
	//提现到的支付宝号
	private String aliAccount;
	//提现到的微信号
	private String wxCount;
	//流水号
	private String transactionNumber;
	//失败原因
	private String reason;
	//提现,充值订单号
	private String orderNo;
	//状态（0：进行中 1：已完成）
	private Integer status;
	//审核通过时间
	private Date checkedTime;
	//交易完成时间
	private Date transactionTime;
	//创建时间
	private Date createTime;

	/**
	 * 设置：明细id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：明细id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：教练ID
	 */
	public void setCoachId(Long coachId) {
		this.coachId = coachId;
	}
	/**
	 * 获取：教练ID
	 */
	public Long getCoachId() {
		return coachId;
	}
	/**
	 * 设置：交易类型（1.课程报酬入账 2.课程入账退款 ）
	 */
	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}
	/**
	 * 获取：交易类型（1.课程报酬入账 2.课程入账退款 ）
	 */
	public Integer getTradeType() {
		return tradeType;
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
	 * 设置：提现到的支付宝号
	 */
	public void setAliAccount(String aliAccount) {
		this.aliAccount = aliAccount;
	}
	/**
	 * 获取：提现到的支付宝号
	 */
	public String getAliAccount() {
		return aliAccount;
	}
	/**
	 * 设置：提现到的微信号
	 */
	public void setWxCount(String wxCount) {
		this.wxCount = wxCount;
	}
	/**
	 * 获取：提现到的微信号
	 */
	public String getWxCount() {
		return wxCount;
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
	 * 设置：失败原因
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * 获取：失败原因
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * 设置：提现,充值订单号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取：提现,充值订单号
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置：状态（0：进行中 1：已完成）
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态（0：进行中 1：已完成）
	 */
	public Integer getStatus() {
		return status;
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
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	public BigDecimal getOrigMoney() {
		return origMoney;
	}

	public void setOrigMoney(BigDecimal origMoney) {
		this.origMoney = origMoney;
	}

	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}
}
