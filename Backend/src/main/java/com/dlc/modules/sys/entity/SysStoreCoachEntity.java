package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 场馆教练
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-04-01 15:52:42
 */
public class SysStoreCoachEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 教练ID
	 */
		private Long scId;
	/**
	 * 所属门店
	 */
	private String storeId;

	private String  storeName;

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	/**
	 * 教练等级（1,2,3,4...）审核的时候评定等级
	 */
	private Integer grade;
	/**
	 * 教练头像
	 */
	private String headImgUrl;
	/**
	 * 姓名
	 */
	private String coachName;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 性别 0未知 1男 2女
	 */
	private Integer sex;
	/**
	 * 省市区
	 */
	private String address;
	/**
	 * 身份证号
	 */
	private String identity;
	/**
	 * 从业年限
	 */
	private Integer employTime;
	/**
	 * 资历证书
	 */
	private String diplomaImgUrl;
	/**
	 * 身份证反面
	 */
	private String identBackImgUrl;
	/**
	 * 身份证正面
	 */
	private String identImgUrl;
	/**
	 * 薪水(元）
	 */
	private BigDecimal salary;
	/**
	 * 评价星级*（*数，创建时默认0）
	 */
	private Double level;
	/**
	 * 状态（1：上架 2：下架）
	 */
	private Integer status;
	/**
	 * 教练简介
	 */
	private String introduce;
	/**
	 * 创建时间
	 */
	private Date createdDate;

	/**
	 * 设置：教练ID
	 */
	public void setScId(Long scId) {
		this.scId = scId;
	}
	/**
	 * 获取：教练ID
	 */
	public Long getScId() {
		return scId;
	}
	/**
	 * 设置：所属门店
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	/**
	 * 获取：所属门店
	 */
	public String getStoreId() {
		return storeId;
	}
	/**
	 * 设置：教练等级（1,2,3,4...）审核的时候评定等级
	 */
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	/**
	 * 获取：教练等级（1,2,3,4...）审核的时候评定等级
	 */
	public Integer getGrade() {
		return grade;
	}
	/**
	 * 设置：教练头像
	 */
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	/**
	 * 获取：教练头像
	 */
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	/**
	 * 设置：姓名
	 */
	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}
	/**
	 * 获取：姓名
	 */
	public String getCoachName() {
		return coachName;
	}
	/**
	 * 设置：手机号
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 获取：手机号
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 设置：性别 0未知 1男 2女
	 */
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	/**
	 * 获取：性别 0未知 1男 2女
	 */
	public Integer getSex() {
		return sex;
	}
	/**
	 * 设置：省市区
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * 获取：省市区
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * 设置：身份证号
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	/**
	 * 获取：身份证号
	 */
	public String getIdentity() {
		return identity;
	}
	/**
	 * 设置：从业年限
	 */
	public void setEmployTime(Integer employTime) {
		this.employTime = employTime;
	}
	/**
	 * 获取：从业年限
	 */
	public Integer getEmployTime() {
		return employTime;
	}
	/**
	 * 设置：资历证书
	 */
	public void setDiplomaImgUrl(String diplomaImgUrl) {
		this.diplomaImgUrl = diplomaImgUrl;
	}
	/**
	 * 获取：资历证书
	 */
	public String getDiplomaImgUrl() {
		return diplomaImgUrl;
	}
	/**
	 * 设置：身份证反面
	 */
	public void setIdentBackImgUrl(String identBackImgUrl) {
		this.identBackImgUrl = identBackImgUrl;
	}
	/**
	 * 获取：身份证反面
	 */
	public String getIdentBackImgUrl() {
		return identBackImgUrl;
	}
	/**
	 * 设置：身份证正面
	 */
	public void setIdentImgUrl(String identImgUrl) {
		this.identImgUrl = identImgUrl;
	}
	/**
	 * 获取：身份证正面
	 */
	public String getIdentImgUrl() {
		return identImgUrl;
	}
	/**
	 * 设置：薪水(元）
	 */
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	/**
	 * 获取：薪水(元）
	 */
	public BigDecimal getSalary() {
		return salary;
	}
	/**
	 * 设置：评价星级*（*数，创建时默认0）
	 */
	public void setLevel(Double level) {
		this.level = level;
	}
	/**
	 * 获取：评价星级*（*数，创建时默认0）
	 */
	public Double getLevel() {
		return level;
	}
	/**
	 * 设置：状态（1：上架 2：下架）
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态（1：上架 2：下架）
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：教练简介
	 */
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	/**
	 * 获取：教练简介
	 */
	public String getIntroduce() {
		return introduce;
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
