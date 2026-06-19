package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户记录表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-15 10:55:18
 */
public class UserRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//用户记录ID
	private Long userRecordId;
	//用户ID
	private Long userId;
	//记录类型
	private String recordType;
	//记录内容
	private String record;
	//创建时间
	private Date createdDate;

	/**
	 * 设置：用户记录ID
	 */
	public void setUserRecordId(Long userRecordId) {
		this.userRecordId = userRecordId;
	}
	/**
	 * 获取：用户记录ID
	 */
	public Long getUserRecordId() {
		return userRecordId;
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
	 * 设置：记录类型
	 */
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	/**
	 * 获取：记录类型
	 */
	public String getRecordType() {
		return recordType;
	}
	/**
	 * 设置：记录内容
	 */
	public void setRecord(String record) {
		this.record = record;
	}
	/**
	 * 获取：记录内容
	 */
	public String getRecord() {
		return record;
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
}
