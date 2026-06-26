package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 门店商品表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-01-14 09:04:26
 */
@TableName("store_goods")
public class SysStoreGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long goodsId;
	/**
	 * 门店ID
	 */
	private Long storeId;
	/**
	 * 条形码（商品唯一标识）
	 */
	private String barCode;
	/**
	 * 商品名称
	 */
	private String name;
	/**
	 * 商品价格
	 */
	private BigDecimal price;
	/**
	 * 商品型号
	 */
	private String style;
	/**
	 * 库存量
	 */
	private Integer total;
	/**
	 * 商品描述
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdDate;

	/**
	 * 设置：
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	/**
	 * 获取：
	 */
	public Long getGoodsId() {
		return goodsId;
	}
	/**
	 * 设置：门店ID
	 */
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	/**
	 * 获取：门店ID
	 */
	public Long getStoreId() {
		return storeId;
	}
	/**
	 * 设置：条形码（商品唯一标识）
	 */
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	/**
	 * 获取：条形码（商品唯一标识）
	 */
	public String getBarCode() {
		return barCode;
	}
	/**
	 * 设置：商品名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：商品名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：商品价格
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * 获取：商品价格
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * 设置：商品型号
	 */
	public void setStyle(String style) {
		this.style = style;
	}
	/**
	 * 获取：商品型号
	 */
	public String getStyle() {
		return style;
	}
	/**
	 * 设置：库存量
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * 获取：库存量
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置：商品描述
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 获取：商品描述
	 */
	public String getRemark() {
		return remark;
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
