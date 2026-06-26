package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

public class UserUpdateRecord implements Serializable {
    private Long uprId;

    private Long userId;

    private Long storeId;

    private Date oldValidityDate;

    private Date newValidityDate;

    private Date createdDate;
    
    private long sysUserId;

    private static final long serialVersionUID = 1L;

    public UserUpdateRecord(Long uprId, Long userId, Long storeId, Date oldValidityDate, Date newValidityDate, Date createdDate) {
        this.uprId = uprId;
        this.userId = userId;
        this.storeId = storeId;
        this.oldValidityDate = oldValidityDate;
        this.newValidityDate = newValidityDate;
        this.createdDate = createdDate;
    }

    public UserUpdateRecord() {
        super();
    }

    public Long getUprId() {
        return uprId;
    }

    public void setUprId(Long uprId) {
        this.uprId = uprId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Date getOldValidityDate() {
        return oldValidityDate;
    }

    public void setOldValidityDate(Date oldValidityDate) {
        this.oldValidityDate = oldValidityDate;
    }

    public Date getNewValidityDate() {
        return newValidityDate;
    }

    public void setNewValidityDate(Date newValidityDate) {
        this.newValidityDate = newValidityDate;
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
        sb.append(", uprId=").append(uprId);
        sb.append(", userId=").append(userId);
        sb.append(", storeId=").append(storeId);
        sb.append(", oldValidityDate=").append(oldValidityDate);
        sb.append(", newValidityDate=").append(newValidityDate);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

	public long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(long sysUserId) {
		this.sysUserId = sysUserId;
	}
}