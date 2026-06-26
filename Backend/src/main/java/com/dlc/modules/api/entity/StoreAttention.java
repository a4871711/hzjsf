package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class StoreAttention implements Serializable {
    private Long storeAttentionId;

    private Long observerId;

    private Long storeGroupId;

    private Byte attentionStatus;

    private Byte reportType;

    private Date attentionDate;

    private static final long serialVersionUID = 1L;

    public StoreAttention(Long storeAttentionId, Long observerId, Long storeGroupId, Byte attentionStatus, Byte reportType, Date attentionDate) {
        this.storeAttentionId = storeAttentionId;
        this.observerId = observerId;
        this.storeGroupId = storeGroupId;
        this.attentionStatus = attentionStatus;
        this.reportType = reportType;
        this.attentionDate = attentionDate;
    }

    public StoreAttention() {
        super();
    }

    public Long getStoreAttentionId() {
        return storeAttentionId;
    }

    public void setStoreAttentionId(Long storeAttentionId) {
        this.storeAttentionId = storeAttentionId;
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
    }

    public Long getStoreGroupId() {
        return storeGroupId;
    }

    public void setStoreGroupId(Long storeGroupId) {
        this.storeGroupId = storeGroupId;
    }

    public Byte getAttentionStatus() {
        return attentionStatus;
    }

    public void setAttentionStatus(Byte attentionStatus) {
        this.attentionStatus = attentionStatus;
    }

    public Byte getReportType() {
        return reportType;
    }

    public void setReportType(Byte reportType) {
        this.reportType = reportType;
    }

    public Date getAttentionDate() {
        return attentionDate;
    }

    public void setAttentionDate(Date attentionDate) {
        this.attentionDate = attentionDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", storeAttentionId=").append(storeAttentionId);
        sb.append(", observerId=").append(observerId);
        sb.append(", storeGroupId=").append(storeGroupId);
        sb.append(", attentionStatus=").append(attentionStatus);
        sb.append(", reportType=").append(reportType);
        sb.append(", attentionDate=").append(attentionDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}