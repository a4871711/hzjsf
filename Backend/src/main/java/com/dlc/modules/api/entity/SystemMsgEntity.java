package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统消息记录表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-11 16:01:16
 */
public class SystemMsgEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//系统消息ID
	private Long sysMsgId;
	//用户ID
	private Long userId;
	//发送时间
	private Date sendTime;
	//已读标识
	private Integer readFlag;
	//消息类型（0：正常消息 1：举报消息）
	private Integer msgType;
	//发送内容
	private String record;

	/**
	 * 设置：系统消息ID
	 */
	public void setSysMsgId(Long sysMsgId) {
		this.sysMsgId = sysMsgId;
	}
	/**
	 * 获取：系统消息ID
	 */
	public Long getSysMsgId() {
		return sysMsgId;
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
	 * 设置：发送时间
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	/**
	 * 获取：发送时间
	 */
	public Date getSendTime() {
		return sendTime;
	}

	public Integer getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(Integer readFlag) {
		this.readFlag = readFlag;
	}

	/**
	 * 设置：发送内容
	 */
	public void setRecord(String record) {
		this.record = record;
	}
	/**
	 * 获取：发送内容
	 */
	public String getRecord() {
		return record;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	@Override
	public String toString() {
		return "SystemMsgEntity{" +
				"sysMsgId=" + sysMsgId +
				", userId=" + userId +
				", sendTime=" + sendTime +
				", readFlag=" + readFlag +
				", msgType=" + msgType +
				", record='" + record + '\'' +
				'}';
	}
}
