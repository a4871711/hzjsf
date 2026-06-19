package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class UserAttention implements Serializable {
    private Long userAttentionId;

    private Long observerId;

    private Long attentedId;

    private Byte attentionStatus;

    private String reportType;

    private Date attentionDate;

    private static final long serialVersionUID = 1L;

    public Long getUserAttentionId() {
        return userAttentionId;
    }

    public void setUserAttentionId(Long userAttentionId) {
        this.userAttentionId = userAttentionId;
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
    }

    public Long getAttentedId() {
        return attentedId;
    }

    public void setAttentedId(Long attentedId) {
        this.attentedId = attentedId;
    }

    public Byte getAttentionStatus() {
        return attentionStatus;
    }

    public void setAttentionStatus(Byte attentionStatus) {
        this.attentionStatus = attentionStatus;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
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
        return "UserAttention{" +
                "userAttentionId=" + userAttentionId +
                ", observerId=" + observerId +
                ", attentedId=" + attentedId +
                ", attentionStatus=" + attentionStatus +
                ", reportType='" + reportType + '\'' +
                ", attentionDate=" + attentionDate +
                '}';
    }
}