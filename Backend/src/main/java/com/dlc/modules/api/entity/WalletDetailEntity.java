package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 钱包明细表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-21 15:54:31
 */
public class WalletDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//明细id
	private Long id;
	//用户ID
	private Long userId;
	//明细类型（1 提现 2.商城购买 3.会员卡购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.其他）
	private Integer type;
	//金额数
	private BigDecimal money;
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
	//状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
	private Integer status;
	//微信openId
	private String openId;
	//收款人
	private String realName;
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
	 * 设置：明细类型（1 提现 2.商城购买 3.会员卡购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.其他）
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：明细类型（1 提现 2.商城购买 3.会员卡购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.其他）
	 */
	public Integer getType() {
		return type;
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
	 * 设置：状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
	 */
	public Integer getStatus() {
		return status;
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

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
}
