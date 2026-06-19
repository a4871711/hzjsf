package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 15:09
 * @description 尺寸表
 */
public class SysCouponEntity implements Serializable {
	/** ID */
    private Long sysCouponId;

    /** 优惠券状态：（1：上架 2：下架） */
    private Long couponStatus;

    /** 有效天数 */
    private Long validity;
    
    private Long goodsId;

    /** 抖音团购 sku_id（验券 prepare 返回的 sku.sku_id） */
    private String douyinSkuId;

    /** 优惠券金额 */
    private BigDecimal couponPrice;

    /** 创建时间 */
    private Date createdDate;

    /** 优惠券门槛 */
    private BigDecimal limitPrice;

    /** 优惠券类型：（0：普通  1抖音 2美团） */
    private Long couponType;

    /** 可用门店 */
    private String storeAddrIds;

    /** 券名称 */
    private String couponTitle;

    /** 每人限领 */
    private Long limitUser;

    /** 发送数量 */
    private Long limitAll;

    /** 已发送数量 */
    private Long sendCount;

    /** 限制健身卡id */
    private String fitCardIds;

    /** 优惠券类型：（0：普通  1新人） */
    private Long couponNew;
    
    private Integer limit;
    private Integer offset;
    private Integer page;
    private Date startTime;
    private Date endTime;

	private List<SysStoreEntity> storeList;
    
	public Long getSysCouponId() {
		return sysCouponId;
	}
	public void setSysCouponId(Long sysCouponId) {
		this.sysCouponId = sysCouponId;
	}
	public Long getCouponStatus() {
		return couponStatus;
	}
	public void setCouponStatus(Long couponStatus) {
		this.couponStatus = couponStatus;
	}
	public Long getValidity() {
		return validity;
	}
	public void setValidity(Long validity) {
		this.validity = validity;
	}
	public BigDecimal getCouponPrice() {
		return couponPrice;
	}
	public void setCouponPrice(BigDecimal couponPrice) {
		this.couponPrice = couponPrice;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public BigDecimal getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(BigDecimal limitPrice) {
		this.limitPrice = limitPrice;
	}
	public Long getCouponType() {
		return couponType;
	}
	public void setCouponType(Long couponType) {
		this.couponType = couponType;
	}
	public String getStoreAddrIds() {
		return storeAddrIds;
	}
	public void setStoreAddrIds(String storeAddrIds) {
		this.storeAddrIds = storeAddrIds;
	}
	public String getCouponTitle() {
		return couponTitle;
	}
	public void setCouponTitle(String couponTitle) {
		this.couponTitle = couponTitle;
	}
	public Long getLimitUser() {
		return limitUser;
	}
	public void setLimitUser(Long limitUser) {
		this.limitUser = limitUser;
	}
	public Long getLimitAll() {
		return limitAll;
	}
	public void setLimitAll(Long limitAll) {
		this.limitAll = limitAll;
	}
	public Long getSendCount() {
		return sendCount;
	}
	public void setSendCount(Long sendCount) {
		this.sendCount = sendCount;
	}
	public String getFitCardIds() {
		return fitCardIds;
	}
	public void setFitCardIds(String fitCardIds) {
		this.fitCardIds = fitCardIds;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public List<SysStoreEntity> getStoreList() {
		return storeList;
	}
	public void setStoreList(List<SysStoreEntity> storeList) {
		this.storeList = storeList;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public String getDouyinSkuId() {
		return douyinSkuId;
	}
	public void setDouyinSkuId(String douyinSkuId) {
		this.douyinSkuId = douyinSkuId;
	}
	public Long getCouponNew() {
		return couponNew;
	}
	public void setCouponNew(Long couponNew) {
		this.couponNew = couponNew;
	}
}
