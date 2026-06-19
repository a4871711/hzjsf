package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LKM
 * @createTime 2018-09-17 11:13
 * @description 教练表
 */
public class SysCoachEntity implements Serializable{
    //教练ID
    private Long coachId;
    //用户ID
    private Long userId;
    //教练等级（审核的时候评定）
    private Integer grade;
    //头像
    private String headImgUrl;
    //姓名
    private String coachName;
    //手机号
    private String phone;
    //性别
    private Byte sex;
    //省
    private String province;
    //市
    private String city;
    //区
    private String zone;
    //省份证
    private String identity;
    //从业年限
    private Integer employTime;
    //身份证正反面图url
    private String identImgUrl;

    private String identBackImgUrl;
    //资历证书图
    private String diplomaImgUrl;
    //选择服务门店（多选：门店1，门店2，...）
    private String storeId;
    //评价星级*（*数，创建时默认0）
    private Integer level;
    //审核状态
    private Integer approveStatus;
    //审核时间
    private Date approveTime;
    //审核失败内容
    private String approveResult;
    //最低RMB/节起
    private Integer minClassMoney;
    //累计上课/节数
    private Byte classCount;
    //教练简介
    private String introduce;
    //创建时间
    private Date createdDate;

    public String getIdentBackImgUrl() {
        return identBackImgUrl;
    }

    public void setIdentBackImgUrl(String identBackImgUrl) {
        this.identBackImgUrl = identBackImgUrl;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Integer getEmployTime() {
        return employTime;
    }

    public void setEmployTime(Integer employTime) {
        this.employTime = employTime;
    }

    public String getIdentImgUrl() {
        return identImgUrl;
    }

    public void setIdentImgUrl(String identImgUrl) {
        this.identImgUrl = identImgUrl;
    }

    public String getDiplomaImgUrl() {
        return diplomaImgUrl;
    }

    public void setDiplomaImgUrl(String diplomaImgUrl) {
        this.diplomaImgUrl = diplomaImgUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public Integer getMinClassMoney() {
        return minClassMoney;
    }

    public void setMinClassMoney(Integer minClassMoney) {
        this.minClassMoney = minClassMoney;
    }

    public Byte getClassCount() {
        return classCount;
    }

    public void setClassCount(Byte classCount) {
        this.classCount = classCount;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
