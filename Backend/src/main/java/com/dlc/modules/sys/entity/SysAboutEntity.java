package com.dlc.modules.sys.entity;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

/**
 * 关于我们
 */
public class SysAboutEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * app图标
     */
    @NotBlank(message = "请选择图标")
    private String appImgUrl;
    /**
     * app名称
     */
    @NotBlank(message = "请输入名称")
    private String appName;
    /**
     *客服电话
     */
    @NotBlank(message = "请输入客服电话")
    private String serviceTel;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *版本号
     */
    @NotBlank(message = "请输入版本号")
    private String version;
    /**
     *中文公司名
     */
    @NotBlank(message = "请输入中文公司名")
    private String companyNameCHN;
    /**
     *英文公司名
     */
    @NotBlank(message = "请输入英文公司名")
    private String companyNameENG;

    @NotBlank(message = "请输入下载地址")
    private String appUrl;

    @NotBlank(message = "请输入下载地址")
    private String appUrlIos;

    @NotBlank(message = "开门限制距离")
    private String openDoor;

    @NotBlank(message = "开门二维码有效时间")
    private String qrcodeValid;

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppUrlIos() {
        return appUrlIos;
    }

    public void setAppUrlIos(String appUrlIos) {
        this.appUrlIos = appUrlIos;
    }

    public SysAboutEntity() {
        super();
    }

    public SysAboutEntity(Long id, String appImgUrl, String appName, String serviceTel, Date createTime, String version, String companyNameCHN, String companyNameENG) {
        this.id = id;
        this.appImgUrl = appImgUrl;
        this.appName = appName;
        this.serviceTel = serviceTel;
        this.createTime = createTime;
        this.version = version;
        this.companyNameCHN = companyNameCHN;
        this.companyNameENG = companyNameENG;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppImgUrl() {
        return appImgUrl;
    }

    public void setAppImgUrl(String appImgUrl) {
        this.appImgUrl = appImgUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServiceTel() {
        return serviceTel;
    }

    public void setServiceTel(String serviceTel) {
        this.serviceTel = serviceTel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCompanyNameCHN() {
        return companyNameCHN;
    }

    public void setCompanyNameCHN(String companyNameCHN) {
        this.companyNameCHN = companyNameCHN;
    }

    public String getCompanyNameENG() {
        return companyNameENG;
    }

    public void setCompanyNameENG(String companyNameENG) {
        this.companyNameENG = companyNameENG;
    }

    public String getOpenDoor() {
        return openDoor;
    }

    public void setOpenDoor(String openDoor) {
        this.openDoor = openDoor;
    }

    public String getQrcodeValid() {
        return qrcodeValid;
    }

    public void setQrcodeValid(String qrcodeValid) {
        this.qrcodeValid = qrcodeValid;
    }


}
