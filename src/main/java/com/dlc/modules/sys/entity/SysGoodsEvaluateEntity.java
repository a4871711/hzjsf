package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品评价表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-30 13:13:07
 */
@TableName("goods_evaluate")
public class SysGoodsEvaluateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品评论ID
	 */
	@TableId
	private Long goodsEvaluatId;
	/**
	 * 商品ID
	 */
	private Long goodsId;
	/**
	 * 订单编号
	 */
	private String orderNo;
	/**
	 * 评论用户ID
	 */
	private Long userId;
	/**
	 * 头像
	 */
	private String headImgUrl;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 评价等级(*级)
	 */
	private BigDecimal evLevel;
	/**
	 * 评价内容
	 */
	private String evContent;
	/**
	 * 评论图
	 */
	private String evaluatImgUrl;
	/**
	 * 评论时间(yyyy-MM-dd)
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date evaluatDate;

	/**
	 * 设置：商品评论ID
	 */
	public void setGoodsEvaluatId(Long goodsEvaluatId) {
		this.goodsEvaluatId = goodsEvaluatId;
	}
	/**
	 * 获取：商品评论ID
	 */
	public Long getGoodsEvaluatId() {
		return goodsEvaluatId;
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
	 * 设置：评论用户ID
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：评论用户ID
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：头像
	 */
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	/**
	 * 获取：头像
	 */
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	/**
	 * 设置：昵称
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * 获取：昵称
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * 设置：评价等级(*级)
	 */
	public void setEvLevel(BigDecimal evLevel) {
		this.evLevel = evLevel;
	}
	/**
	 * 获取：评价等级(*级)
	 */
	public BigDecimal getEvLevel() {
		return evLevel;
	}
	/**
	 * 设置：评价内容
	 */
	public void setEvContent(String evContent) {
		this.evContent = evContent;
	}
	/**
	 * 获取：评价内容
	 */
	public String getEvContent() {
		return evContent;
	}
	/**
	 * 设置：评论图
	 */
	public void setEvaluatImgUrl(String evaluatImgUrl) {
		this.evaluatImgUrl = evaluatImgUrl;
	}
	/**
	 * 获取：评论图
	 */
	public String getEvaluatImgUrl() {
		return evaluatImgUrl;
	}
	/**
	 * 设置：评论时间(yyyy-MM-dd)
	 */
	public void setEvaluatDate(Date evaluatDate) {
		this.evaluatDate = evaluatDate;
	}
	/**
	 * 获取：评论时间(yyyy-MM-dd)
	 */
	public Date getEvaluatDate() {
		return evaluatDate;
	}
}
