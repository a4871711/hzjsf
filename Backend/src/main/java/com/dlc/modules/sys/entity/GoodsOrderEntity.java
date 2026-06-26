package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 商城订单表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-10-25 17:12:06
 */
public class GoodsOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//商城订单ID
	private Long goodsOrderId;
	//openId
	private String openId;
	//用户ID
	private Long userId;
	//商品ID
	private Long goodsId;
	//商品属性（颜色，型号，尺寸等）
	private String goodsProperty;
	//购买数量
	private Integer buyCount;
	//订单类型
	private Integer orderType;
	//订单编号
	private String orderNo;
	//收货人姓名
	private String receiveName;
	//收货人联系电话
	private String receivePhone;
	//收货人地址
	private String receiveAddr;
	//收货地址ID
	private Long receiveAddrId;
	//门店地址ID
	private Long shopAddrId;
	//交易流水号
	private String transactionNo;
	//商品总价（不算优惠券）
	private BigDecimal goodsSum;
	//运费
	private BigDecimal fare;
	//优惠券ID
	private Long couponId;
	//使用优惠券金额
	private BigDecimal couponMoney;
	//支付方式(1:余额支付 2:微信 3:支付宝)
	private Integer payType;
	//物流公司编码（不支持中文）
	private String company;
	//实付款
	private BigDecimal realPayment;
	//物流编号
	private String logisticsNo;
	//物流公司
	private String logisticsName;
	//订单状态：0 待付款，1 待发货，2 待收货，3已完成，4已取消 5已评价 6已退款
	private Integer status;
	//创建时间
	private Date createdDate;
	//支付时间
	private Date payTime;
	//发货时间
	private Date deliveryTime;
	//完成时间
	private Date finish;
	//收货时间
	private Date tradeTime;
	//商品主图
	private String imgUrl;
	//留言
	private String messages;
	//商品名称
	private String goodsName;

	/**
	 * 设置：商城订单ID
	 */
	public void setGoodsOrderId(Long goodsOrderId) {
		this.goodsOrderId = goodsOrderId;
	}
	/**
	 * 获取：商城订单ID
	 */
	public Long getGoodsOrderId() {
		return goodsOrderId;
	}
	/**
	 * 设置：openId
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * 获取：openId
	 */
	public String getOpenId() {
		return openId;
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
	 * 设置：商品ID
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	/**
	 * 获取：商品ID
	 */
	public Long getGoodsId() {
		return goodsId;
	}
	/**
	 * 设置：商品属性（颜色，型号，尺寸等）
	 */
	public void setGoodsProperty(String goodsProperty) {
		this.goodsProperty = goodsProperty;
	}
	/**
	 * 获取：商品属性（颜色，型号，尺寸等）
	 */
	public String getGoodsProperty() {
		return goodsProperty;
	}
	/**
	 * 设置：购买数量
	 */
	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}
	/**
	 * 获取：购买数量
	 */
	public Integer getBuyCount() {
		return buyCount;
	}
	/**
	 * 设置：订单类型
	 */
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	/**
	 * 获取：订单类型
	 */
	public Integer getOrderType() {
		return orderType;
	}
	/**
	 * 设置：订单编号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取：订单编号
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置：收货人姓名
	 */
	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}
	/**
	 * 获取：收货人姓名
	 */
	public String getReceiveName() {
		return receiveName;
	}
	/**
	 * 设置：收货人联系电话
	 */
	public void setReceivePhone(String receivePhone) {
		this.receivePhone = receivePhone;
	}
	/**
	 * 获取：收货人联系电话
	 */
	public String getReceivePhone() {
		return receivePhone;
	}
	/**
	 * 设置：收货人地址
	 */
	public void setReceiveAddr(String receiveAddr) {
		this.receiveAddr = receiveAddr;
	}
	/**
	 * 获取：收货人地址
	 */
	public String getReceiveAddr() {
		return receiveAddr;
	}
	/**
	 * 设置：收货地址ID
	 */
	public void setReceiveAddrId(Long receiveAddrId) {
		this.receiveAddrId = receiveAddrId;
	}
	/**
	 * 获取：收货地址ID
	 */
	public Long getReceiveAddrId() {
		return receiveAddrId;
	}
	/**
	 * 设置：门店地址ID
	 */
	public void setShopAddrId(Long shopAddrId) {
		this.shopAddrId = shopAddrId;
	}
	/**
	 * 获取：门店地址ID
	 */
	public Long getShopAddrId() {
		return shopAddrId;
	}
	/**
	 * 设置：交易流水号
	 */
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	/**
	 * 获取：交易流水号
	 */
	public String getTransactionNo() {
		return transactionNo;
	}
	/**
	 * 设置：商品总价（不算优惠券）
	 */
	public void setGoodsSum(BigDecimal goodsSum) {
		this.goodsSum = goodsSum;
	}
	/**
	 * 获取：商品总价（不算优惠券）
	 */
	public BigDecimal getGoodsSum() {
		return goodsSum;
	}
	/**
	 * 设置：运费
	 */
	public void setFare(BigDecimal fare) {
		this.fare = fare;
	}
	/**
	 * 获取：运费
	 */
	public BigDecimal getFare() {
		return fare;
	}
	/**
	 * 设置：优惠券ID
	 */
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	/**
	 * 获取：优惠券ID
	 */
	public Long getCouponId() {
		return couponId;
	}
	/**
	 * 设置：使用优惠券金额
	 */
	public void setCouponMoney(BigDecimal couponMoney) {
		this.couponMoney = couponMoney;
	}
	/**
	 * 获取：使用优惠券金额
	 */
	public BigDecimal getCouponMoney() {
		return couponMoney;
	}
	/**
	 * 设置：支付方式(1:余额支付 2:微信 3:支付宝)
	 */
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	/**
	 * 获取：支付方式(1:余额支付 2:微信 3:支付宝)
	 */
	public Integer getPayType() {
		return payType;
	}
	/**
	 * 设置：物流公司编码（不支持中文）
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * 获取：物流公司编码（不支持中文）
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * 设置：实付款
	 */
	public void setRealPayment(BigDecimal realPayment) {
		this.realPayment = realPayment;
	}
	/**
	 * 获取：实付款
	 */
	public BigDecimal getRealPayment() {
		return realPayment;
	}
	/**
	 * 设置：物流编号
	 */
	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}
	/**
	 * 获取：物流编号
	 */
	public String getLogisticsNo() {
		return logisticsNo;
	}
	/**
	 * 设置：物流公司
	 */
	public void setLogisticsName(String logisticsName) {
		this.logisticsName = logisticsName;
	}
	/**
	 * 获取：物流公司
	 */
	public String getLogisticsName() {
		return logisticsName;
	}
	/**
	 * 设置：订单状态：0 待付款，1 待发货，2 待收货，3已完成，4已取消 5已评价 6已退款
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：订单状态：0 待付款，1 待发货，2 待收货，3已完成，4已取消 5已评价 6已退款
	 */
	public Integer getStatus() {
		return status;
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
	/**
	 * 设置：支付时间
	 */
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	/**
	 * 获取：支付时间
	 */
	public Date getPayTime() {
		return payTime;
	}
	/**
	 * 设置：发货时间
	 */
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	/**
	 * 获取：发货时间
	 */
	public Date getDeliveryTime() {
		return deliveryTime;
	}
	/**
	 * 设置：完成时间
	 */
	public void setFinish(Date finish) {
		this.finish = finish;
	}
	/**
	 * 获取：完成时间
	 */
	public Date getFinish() {
		return finish;
	}
	/**
	 * 设置：收货时间
	 */
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}
	/**
	 * 获取：收货时间
	 */
	public Date getTradeTime() {
		return tradeTime;
	}
	/**
	 * 设置：商品主图
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	/**
	 * 获取：商品主图
	 */
	public String getImgUrl() {
		return imgUrl;
	}
	/**
	 * 设置：留言
	 */
	public void setMessages(String messages) {
		this.messages = messages;
	}
	/**
	 * 获取：留言
	 */
	public String getMessages() {
		return messages;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
}
