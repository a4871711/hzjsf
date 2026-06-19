package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 分享表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-13 17:53:50
 */
@TableName("share")
public class SysShareEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分享ID
	 */
	@TableId
	private Long shareId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 分享的类型（1.门店分享，2.运动分享）
	 */
	private Long shareType;
	/**
	 * 分享标题
	 */
	private String title;
	/**
	 * 分享内容
	 */
	private String content;
	/**
	 * 分享链接
	 */
	private String linkUrl;
	/**
	 * 分享图片
	 */
	private String imgUrl;
	/**
	 * 分享渠道(1:微信 2：朋友圈 3：QQ 4：QQ空间)
	 */
	private Integer shareChannel;
	/**
	 * 分享时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdDate;

	/**
	 * 设置：分享ID
	 */
	public void setShareId(Long shareId) {
		this.shareId = shareId;
	}
	/**
	 * 获取：分享ID
	 */
	public Long getShareId() {
		return shareId;
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
	 * 设置：分享的类型（1.门店分享，2.运动分享）
	 */
	public void setShareType(Long shareType) {
		this.shareType = shareType;
	}
	/**
	 * 获取：分享的类型（1.门店分享，2.运动分享）
	 */
	public Long getShareType() {
		return shareType;
	}
	/**
	 * 设置：分享标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取：分享标题
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置：分享内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取：分享内容
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置：分享链接
	 */
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	/**
	 * 获取：分享链接
	 */
	public String getLinkUrl() {
		return linkUrl;
	}
	/**
	 * 设置：分享图片
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	/**
	 * 获取：分享图片
	 */
	public String getImgUrl() {
		return imgUrl;
	}
	/**
	 * 设置：分享渠道(1:微信 2：朋友圈 3：QQ 4：QQ空间)
	 */
	public void setShareChannel(Integer shareChannel) {
		this.shareChannel = shareChannel;
	}
	/**
	 * 获取：分享渠道(1:微信 2：朋友圈 3：QQ 4：QQ空间)
	 */
	public Integer getShareChannel() {
		return shareChannel;
	}
	/**
	 * 设置：分享时间
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * 获取：分享时间
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
}
