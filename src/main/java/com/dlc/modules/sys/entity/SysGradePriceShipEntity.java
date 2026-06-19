package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/*
 *私教课价格
 */
public class SysGradePriceShipEntity implements Serializable{
    //id
    private Long lpsId;
    //关联表id
    private Long privateClassId;
    //图片
    private  String img;
    //教练等级
    private Long grade;
    //价格
    private BigDecimal price;

    public Long getLpsId() {
        return lpsId;
    }

    public void setLpsId(Long lpsId) {
        this.lpsId = lpsId;
    }

    public Long getPrivateClassId() {
        return privateClassId;
    }

    public void setPrivateClassId(Long privateClassId) {
        this.privateClassId = privateClassId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public SysGradePriceShipEntity() {
        super();
    }

    public SysGradePriceShipEntity(Long lpsId, Long privateClassId, String img, Long grade, BigDecimal price) {
        this.lpsId = lpsId;
        this.privateClassId = privateClassId;
        this.img = img;
        this.grade = grade;
        this.price = price;
    }
}
