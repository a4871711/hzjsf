package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class DynamicShield implements Serializable {
    private Long shieldId;

    private Long userId;

    private Long dynamicId;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public DynamicShield(Long shieldId, Long userId, Long dynamicId, Date createdDate) {
        this.shieldId = shieldId;
        this.userId = userId;
        this.dynamicId = dynamicId;
        this.createdDate = createdDate;
    }

    public DynamicShield() {
        super();
    }

    public Long getShieldId() {
        return shieldId;
    }

    public void setShieldId(Long shieldId) {
        this.shieldId = shieldId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Long dynamicId) {
        this.dynamicId = dynamicId;
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
        sb.append(", shieldId=").append(shieldId);
        sb.append(", userId=").append(userId);
        sb.append(", dynamicId=").append(dynamicId);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}