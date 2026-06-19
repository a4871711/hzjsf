package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 点赞记录表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-26 11:03:59
 */
public class DzRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//id
	private Long dzRecordId;
	//动态id
	private Long dynamicId;
	//点赞者id
	private Long dzUserId;

	/**
	 * 设置：id
	 */
	public void setDzRecordId(Long dzRecordId) {
		this.dzRecordId = dzRecordId;
	}
	/**
	 * 获取：id
	 */
	public Long getDzRecordId() {
		return dzRecordId;
	}
	/**
	 * 设置：动态id
	 */
	public void setDynamicId(Long dynamicId) {
		this.dynamicId = dynamicId;
	}
	/**
	 * 获取：动态id
	 */
	public Long getDynamicId() {
		return dynamicId;
	}
	/**
	 * 设置：点赞者id
	 */
	public void setDzUserId(Long dzUserId) {
		this.dzUserId = dzUserId;
	}
	/**
	 * 获取：点赞者id
	 */
	public Long getDzUserId() {
		return dzUserId;
	}
}
