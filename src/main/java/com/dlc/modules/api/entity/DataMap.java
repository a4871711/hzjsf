package com.dlc.modules.api.entity;

import java.io.Serializable;

public class DataMap implements Serializable {
    private Long dataMapId;

    private Long idxLabel;

    private Long nextLabel;

    private String dataName;

    private Integer price;

    private Double val;

    private String description;

    private static final long serialVersionUID = 1L;

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Double getVal() {
        return val;
    }

    public void setVal(Double val) {
        this.val = val;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DataMap{" +
                "dataMapId=" + dataMapId +
                ", idxLabel=" + idxLabel +
                ", nextLabel=" + nextLabel +
                ", dataName='" + dataName + '\'' +
                ", price=" + price +
                ", val=" + val +
                ", description='" + description + '\'' +
                '}';
    }
}