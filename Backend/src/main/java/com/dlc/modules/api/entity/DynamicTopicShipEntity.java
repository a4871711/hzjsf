package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 动态话题关系表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-19 15:06:17
 */
public class DynamicTopicShipEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//ID
	private Long dtsId;
	//动态ID
	private Long dynamicId;
	//话题ID
	private Long topicId;
	//话题名称
	private String topicName;
	//创建日期
	private Date createdDate;

	/**
	 * 设置：ID
	 */
	public void setDtsId(Long dtsId) {
		this.dtsId = dtsId;
	}
	/**
	 * 获取：ID
	 */
	public Long getDtsId() {
		return dtsId;
	}
	/**
	 * 设置：动态ID
	 */
	public void setDynamicId(Long dynamicId) {
		this.dynamicId = dynamicId;
	}
	/**
	 * 获取：动态ID
	 */
	public Long getDynamicId() {
		return dynamicId;
	}
	/**
	 * 设置：话题ID
	 */
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	/**
	 * 获取：话题ID
	 */
	public Long getTopicId() {
		return topicId;
	}
	/**
	 * 设置：话题名称
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	/**
	 * 获取：话题名称
	 */
	public String getTopicName() {
		return topicName;
	}
	/**
	 * 设置：创建日期
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * 获取：创建日期
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
}
