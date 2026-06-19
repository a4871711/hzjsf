package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 等级-私教课价格映射表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-19 15:59:25
 */
public class GradePriceShipEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//ID
	private Long lpsId;
	//课程ID
	private Long privateClassId;
	//教练等级
	private Integer grade;
	//课程价格
	private BigDecimal price;

	/**
	 * 设置：ID
	 */
	public void setLpsId(Long lpsId) {
		this.lpsId = lpsId;
	}
	/**
	 * 获取：ID
	 */
	public Long getLpsId() {
		return lpsId;
	}
	/**
	 * 设置：课程ID
	 */
	public void setPrivateClassId(Long privateClassId) {
		this.privateClassId = privateClassId;
	}
	/**
	 * 获取：课程ID
	 */
	public Long getPrivateClassId() {
		return privateClassId;
	}
	/**
	 * 设置：教练等级
	 */
	public void setLevel(Integer level) {
		this.grade = level;
	}
	/**
	 * 获取：教练等级
	 */
	public Integer getLevel() {
		return grade;
	}
	/**
	 * 设置：课程价格
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * 获取：课程价格
	 */
	public BigDecimal getPrice() {
		return price;
	}
}
