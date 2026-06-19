package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StoreCoach implements Serializable {
    private Long scId;

    private String storeId;

    private Integer grade;

    private String headImgUrl;

    private String coachName;

    private String phone;

    private Integer sex;

    private String address;

    private String identity;

    private Integer employTime;

    private String diplomaImgUrl;

    private String identBackImgUrl;

    private String identImgUrl;

    private BigDecimal salary;

    private Double level;

    private Integer status;

    private Date createdDate;

    private String introduce;

    private static final long serialVersionUID = 1L;

    public StoreCoach(Long scId, String storeId, Integer grade, String headImgUrl, String coachName, String phone, Integer sex, String address, String identity, Integer employTime, String diplomaImgUrl, String identBackImgUrl, String identImgUrl, BigDecimal salary, Double level, Integer status, Date createdDate, String introduce) {
        this.scId = scId;
        this.storeId = storeId;
        this.grade = grade;
        this.headImgUrl = headImgUrl;
        this.coachName = coachName;
        this.phone = phone;
        this.sex = sex;
        this.address = address;
        this.identity = identity;
        this.employTime = employTime;
        this.diplomaImgUrl = diplomaImgUrl;
        this.identBackImgUrl = identBackImgUrl;
        this.identImgUrl = identImgUrl;
        this.salary = salary;
        this.level = level;
        this.status = status;
        this.createdDate = createdDate;
        this.introduce = introduce;
    }

    public StoreCoach() {
        super();
    }

    public Long getScId() {
        return scId;
    }

    public void setScId(Long scId) {
        this.scId = scId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl == null ? null : headImgUrl.trim();
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName == null ? null : coachName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity == null ? null : identity.trim();
    }

    public Integer getEmployTime() {
        return employTime;
    }

    public void setEmployTime(Integer employTime) {
        this.employTime = employTime;
    }

    public String getDiplomaImgUrl() {
        return diplomaImgUrl;
    }

    public void setDiplomaImgUrl(String diplomaImgUrl) {
        this.diplomaImgUrl = diplomaImgUrl == null ? null : diplomaImgUrl.trim();
    }

    public String getIdentBackImgUrl() {
        return identBackImgUrl;
    }

    public void setIdentBackImgUrl(String identBackImgUrl) {
        this.identBackImgUrl = identBackImgUrl == null ? null : identBackImgUrl.trim();
    }

    public String getIdentImgUrl() {
        return identImgUrl;
    }

    public void setIdentImgUrl(String identImgUrl) {
        this.identImgUrl = identImgUrl == null ? null : identImgUrl.trim();
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Double getLevel() {
        return level;
    }

    public void setLevel(Double level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", scId=").append(scId);
        sb.append(", storeId=").append(storeId);
        sb.append(", grade=").append(grade);
        sb.append(", headImgUrl=").append(headImgUrl);
        sb.append(", coachName=").append(coachName);
        sb.append(", phone=").append(phone);
        sb.append(", sex=").append(sex);
        sb.append(", address=").append(address);
        sb.append(", identity=").append(identity);
        sb.append(", employTime=").append(employTime);
        sb.append(", diplomaImgUrl=").append(diplomaImgUrl);
        sb.append(", identBackImgUrl=").append(identBackImgUrl);
        sb.append(", identImgUrl=").append(identImgUrl);
        sb.append(", salary=").append(salary);
        sb.append(", level=").append(level);
        sb.append(", status=").append(status);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", introduce=").append(introduce);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}