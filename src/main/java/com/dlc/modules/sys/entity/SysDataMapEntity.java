package com.dlc.modules.sys.entity;

import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 兴趣标签
 */
public class SysDataMapEntity {
    //id
    private Long dataMapId;
    //一级标签
    private Long idxLabel;
    //标签
    private Long nextLabel;
    //标签名称
    @NotBlank(message = "请输入标签名")
    private String dataName;
    //价格
    private BigDecimal price;

    private double val;

    private double nextVal;

    //描述
    private  String description;



    public SysDataMapEntity() {
        super();
    }

    public Long getDataMapId() {
        return dataMapId;
    }

    public void setDataMapId(Long dataMapId) {
        this.dataMapId = dataMapId;
    }

    public Long getIdxLabel() {
        return idxLabel;
    }

    public void setIdxLabel(Long idxLabel) {
        this.idxLabel = idxLabel;
    }

    public Long getNextLabel() {
        return nextLabel;
    }

    public void setNextLabel(Long nextLabel) {
        this.nextLabel = nextLabel;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

    public double getNextVal() {
        return nextVal;
    }

    public void setNextVal(double nextVal) {
        this.nextVal = nextVal;
    }

    public SysDataMapEntity(Long dataMapId, Long idxLabel, Long nextLabel, String dataName, BigDecimal price, String description) {
        this.dataMapId = dataMapId;
        this.idxLabel = idxLabel;
        this.nextLabel = nextLabel;
        this.dataName = dataName;
        this.price = price;
        this.description = description;
    }
}
