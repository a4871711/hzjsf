package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 门店设备表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-07 10:46:29
 */
@TableName("store_device")
public class SysStoreDeviceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long sdId;
	/**
	 * 门店id
	 */
	private Long storeId;

	private String storeName;

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	/**
	 * 设备名称
	 */
	private String deviceName;
	/**
	 * 设备编号
	 */
	private String deviceNo;
	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8" ,pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 设置：ID
	 */
	public void setSdId(Long sdId) {
		this.sdId = sdId;
	}
	/**
	 * 获取：ID
	 */
	public Long getSdId() {
		return sdId;
	}
	/**
	 * 设置：门店id
	 */
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	/**
	 * 获取：门店id
	 */
	public Long getStoreId() {
		return storeId;
	}
	/**
	 * 设置：设备名称
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	/**
	 * 获取：设备名称
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * 设置：设备编号
	 */
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	/**
	 * 获取：设备编号
	 */
	public String getDeviceNo() {
		return deviceNo;
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
}
