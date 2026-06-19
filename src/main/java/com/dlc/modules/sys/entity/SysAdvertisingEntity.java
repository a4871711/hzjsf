package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 广告表
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-12 16:11:35
 */
public class SysAdvertisingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 广告ID
	 */
	@TableId
	private Long advId;
	/**
	 * 广告类型（1：首页广告 2：商城广告）
	 */
	private Integer advType;
	/**
	 * 广告标题
	 */
	private String advTitle;
	/**
	 * 广告主图
	 */
	private String advMainImg;
	/**
	 * 链接商品
	 */
	private Long goodsId;
	/**商品名称*/
	private String goodsName;
	/**
	 * 广告内容
	 */
	private String advContent;
	/**
	 * 广告插图
	 */
	private String figureImg;
	/**
	 * 创建时间
	 */
	private Date createdDate;

	/**
	 * 设置：广告ID
	 */
	public void setAdvId(Long advId) {
		this.advId = advId;
	}
	/**
	 * 获取：广告ID
	 */
	public Long getAdvId() {
		return advId;
	}
	/**
	 * 设置：广告类型（1：首页广告 2：商城广告）
	 */
	public void setAdvType(Integer advType) {
		this.advType = advType;
	}
	/**
	 * 获取：广告类型（1：首页广告 2：商城广告）
	 */
	public Integer getAdvType() {
		return advType;
	}
	/**
	 * 设置：广告标题
	 */
	public void setAdvTitle(String advTitle) {
		this.advTitle = advTitle;
	}
	/**
	 * 获取：广告标题
	 */
	public String getAdvTitle() {
		return advTitle;
	}
	/**
	 * 设置：广告主图
	 */
	public void setAdvMainImg(String advMainImg) {
		this.advMainImg = advMainImg;
	}
	/**
	 * 获取：广告主图
	 */
	public String getAdvMainImg() {
		return advMainImg;
	}
	/**
	 * 设置：广告内容
	 */
	public void setAdvContent(String advContent) {
		this.advContent = advContent;
	}
	/**
	 * 获取：广告内容
	 */
	public String getAdvContent() {
		return advContent;
	}
	/**
	 * 设置：广告插图
	 */
	public void setFigureImg(String figureImg) {
		this.figureImg = figureImg;
	}
	/**
	 * 获取：广告插图
	 */
	public String getFigureImg() {
		return figureImg;
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

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
