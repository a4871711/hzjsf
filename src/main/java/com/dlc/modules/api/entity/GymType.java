package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class GymType implements Serializable {
    private Long gtId;

    private String gtName;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public GymType(Long gtId, String gtName, Date createdDate) {
        this.gtId = gtId;
        this.gtName = gtName;
        this.createdDate = createdDate;
    }

    public GymType() {
        super();
    }

    public Long getGtId() {
        return gtId;
    }

    public void setGtId(Long gtId) {
        this.gtId = gtId;
    }

    public String getGtName() {
        return gtName;
    }

    public void setGtName(String gtName) {
        this.gtName = gtName == null ? null : gtName.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", gtId=").append(gtId);
        sb.append(", gtName=").append(gtName);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}