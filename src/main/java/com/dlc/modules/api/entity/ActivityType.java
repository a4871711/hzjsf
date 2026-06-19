package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class ActivityType implements Serializable {
    private Long atId;

    private String atName;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public ActivityType(Long atId, String atName, Date createdDate) {
        this.atId = atId;
        this.atName = atName;
        this.createdDate = createdDate;
    }

    public ActivityType() {
        super();
    }

    public Long getAtId() {
        return atId;
    }

    public void setAtId(Long atId) {
        this.atId = atId;
    }

    public String getAtName() {
        return atName;
    }

    public void setAtName(String atName) {
        this.atName = atName == null ? null : atName.trim();
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
        sb.append(", atId=").append(atId);
        sb.append(", atName=").append(atName);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}