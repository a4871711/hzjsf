package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 关于我们
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-28 19:39:01
 */
public class AboutUsEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//app图标
	private String app_Img_Url;
	//app名称
	private String app_Name;
	//app下载链接
	private String app_Url;

	private String app_Url_Ios;
	//客服电话
	private String service_Tel;
	//创建时间
	private Date create_Time;
	//版本号（或介绍）
	private String version;
	//中文公司名
	private String companyNameCHN;
	//英文公司名
	private String companyNameENG;

	private String brandLogo;
	/**
	 * 设置：
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：app图标
	 */
	public void setApp_Img_Url(String app_Img_Url) {
		this.app_Img_Url = app_Img_Url;
	}
	/**
	 * 获取：app图标
	 */
	public String getApp_Img_Url() {
		return app_Img_Url;
	}
	/**
	 * 设置：app名称
	 */
	public void setApp_Name(String app_Name) {
		this.app_Name = app_Name;
	}
	/**
	 * 获取：app名称
	 */
	public String getApp_Name() {
		return app_Name;
	}
	/**
	 * 设置：app下载链接
	 */
	public void setApp_Url(String app_Url) {
		this.app_Url = app_Url;
	}
	/**
	 * 获取：app下载链接
	 */
	public String getApp_Url() {
		return app_Url;
	}
	/**
	 * 设置：客服电话
	 */
	public void setService_Tel(String service_Tel) {
		this.service_Tel = service_Tel;
	}
	/**
	 * 获取：客服电话
	 */
	public String getService_Tel() {
		return service_Tel;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreate_Time(Date create_Time) {
		this.create_Time = create_Time;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreate_Time() {
		return create_Time;
	}
	/**
	 * 设置：版本号（或介绍）
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * 获取：版本号（或介绍）
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * 设置：中文公司名
	 */
	public void setCompanyNameCHN(String companyNameCHN) {
		this.companyNameCHN = companyNameCHN;
	}
	/**
	 * 获取：中文公司名
	 */
	public String getCompanyNameCHN() {
		return companyNameCHN;
	}
	/**
	 * 设置：英文公司名
	 */
	public void setCompanyNameENG(String companyNameENG) {
		this.companyNameENG = companyNameENG;
	}
	/**
	 * 获取：英文公司名
	 */
	public String getCompanyNameENG() {
		return companyNameENG;
	}

	public String getApp_Url_Ios() {
		return app_Url_Ios;
	}

	public void setApp_Url_Ios(String app_Url_Ios) {
		this.app_Url_Ios = app_Url_Ios;
	}

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }
}
