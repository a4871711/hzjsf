package com.dlc.modules.sys.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教课
 */
public class SysPrivateClassEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 课程id
     */
    private Long privateClassId;
    /**
     * 课程名称
     */
    @NotBlank(message = "请输入课程名称")
    private String className;
    /**
     *封面图
     */
    @NotBlank(message = "请选择图片")
    private String firstImgUrl;
    /**
     *私教课图片
     */
    @NotBlank(message = "请选择图片")
    private String imgUrl;
    /**
     *课程类型
     */
    private Long classType;
    /**
     *每节课时长
     */
    @NotNull(message = "请输入每节课时长")
        private BigDecimal classTime;
    /**
     *最低购买课时数量
     */
    private Long leastBuyNum;
    /**
     * 消耗能量
     */
    private double energy;
    /**
     *课程单价
     */
    private BigDecimal classPrice;
    /**
     *课程详细
     */
    @NotBlank(message = "请输入课程详细")
    private String classDetail;
    /**
     *课程有效时间
     */
    @NotNull(message = "请输入课程有效时间(天)")
    private Long validityDay;
    /**
     *创建时间
     */
    private Date createdDate;
    /*教练等级*/
    private Integer grade;
    /**等级名称*/
    private String classTypeName;

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public SysPrivateClassEntity() {
        super();
    }

    public SysPrivateClassEntity(Long privateClassId, String className, String firstImgUrl, String imgUrl, Long classType, BigDecimal classTime, Long leastBuyNum, BigDecimal classPrice, String classDetail, Long validityDay, Date createdDate) {
        this.privateClassId = privateClassId;
        this.className = className;
        this.firstImgUrl = firstImgUrl;
        this.imgUrl = imgUrl;
        this.classType = classType;
        this.classTime = classTime;
        this.leastBuyNum = leastBuyNum;
        this.classPrice = classPrice;
        this.classDetail = classDetail;
        this.validityDay = validityDay;
        this.createdDate = createdDate;
    }

    public Long getPrivateClassId() {
        return privateClassId;
    }

    public void setPrivateClassId(Long privateClassId) {
        this.privateClassId = privateClassId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFirstImgUrl() {
        return firstImgUrl;
    }

    public void setFirstImgUrl(String firstImgUrl) {
        this.firstImgUrl = firstImgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getClassType() {
        return classType;
    }

    public void setClassType(Long classType) {
        this.classType = classType;
    }

    public BigDecimal getClassTime() {
        return classTime;
    }

    public void setClassTime(BigDecimal classTime) {
        this.classTime = classTime;
    }

    public Long getLeastBuyNum() {
        return leastBuyNum;
    }

    public void setLeastBuyNum(Long leastBuyNum) {
        this.leastBuyNum = leastBuyNum;
    }

    public BigDecimal getClassPrice() {
        return classPrice;
    }

    public void setClassPrice(BigDecimal classPrice) {
        this.classPrice = classPrice;
    }

    public String getClassDetail() {
        return classDetail;
    }

    public void setClassDetail(String classDetail) {
        this.classDetail = classDetail;
    }

    public Long getValidityDay() {
        return validityDay;
    }

    public void setValidityDay(Long validityDay) {
        this.validityDay = validityDay;
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

    public String getClassTypeName() {
        return classTypeName;
    }

    public void setClassTypeName(String classTypeName) {
        this.classTypeName = classTypeName;
    }
}
