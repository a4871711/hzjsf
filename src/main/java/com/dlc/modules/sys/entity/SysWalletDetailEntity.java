package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 钱包明细表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:55
 */
@TableName("wallet_detail")
public class SysWalletDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 明细id
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	private  String  userName;
	private String phone;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 明细类型（1.(用户) 提现 2.（教练）提现 3.(用户）充值）
	 */
	private Integer type;
	/**
	 * 金额数
	 */
	private BigDecimal money;
	/**
	 * 提现到的支付宝号
	 */
	private String aliAccount;
	/**
	 * 真实用户
	 */
	private String realName;
	/**
	 * 提现到的微信号
	 */
	private String wxCount;
	/**
	 * 流水号
	 */
	private String transactionNumber;
	/**
	 * 失败原因
	 */
	private String reason;
	/**
	 * 提现,充值订单号
	 */
	private String orderNo;
	/**
	 * 状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败 0：充值中）
	 */
	private Integer status;
	/**
	 * 微信openId
	 */
	private String openId;
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
	 * 设置：明细类型（1.(用户) 提现 2.（教练）提现 3.(用户）充值）
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：明细类型（1.(用户) 提现 2.（教练）提现 3.(用户）充值）
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
	 * 设置：状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败 0：充值中）
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败 0：充值中）
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
