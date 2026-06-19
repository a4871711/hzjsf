package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息记录表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-10 17:46:22
 */
@TableName("system_msg")
public class SysSystemMsgEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统消息ID
	 */
	@TableId
	private Long sysMsgId;
	/**
	 * 用户ID
	 */
	private Long userId;


	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 发送时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date sendTime;
	/**
	 * 消息类型（0:正常消息 1:举报消息 2：退款消息 3：教练回款）
	 */
	private Integer msgType;
	/**
	 * 已读标识（0：未读 1：已读）
	 */
	private Integer readFlag;
	/**
	 * 发送内容
	 */
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
	/**
	 * 设置：消息类型（0:正常消息 1:举报消息 2：退款消息 3：教练回款）
	 */
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
	/**
	 * 获取：消息类型（0:正常消息 1:举报消息 2：退款消息 3：教练回款）
	 */
	public Integer getMsgType() {
		return msgType;
	}
	/**
	 * 设置：已读标识（0：未读 1：已读）
	 */
	public void setReadFlag(Integer readFlag) {
		this.readFlag = readFlag;
	}
	/**
	 * 获取：已读标识（0：未读 1：已读）
	 */
	public Integer getReadFlag() {
		return readFlag;
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
}
