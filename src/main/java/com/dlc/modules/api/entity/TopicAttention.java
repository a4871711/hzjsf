package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class TopicAttention implements Serializable {
    private Long topicAttentionId;

    private Long observerId;

    private Long topicId;

    private Byte attentionStatus;

    private Byte reportType;

    private Date attentionDate;

    private static final long serialVersionUID = 1L;

    public TopicAttention(Long topicAttentionId, Long observerId, Long topicId, Byte attentionStatus, Byte reportType, Date attentionDate) {
        this.topicAttentionId = topicAttentionId;
        this.observerId = observerId;
        this.topicId = topicId;
        this.attentionStatus = attentionStatus;
        this.reportType = reportType;
        this.attentionDate = attentionDate;
    }

    public TopicAttention() {
        super();
    }

    public Long getTopicAttentionId() {
        return topicAttentionId;
    }

    public void setTopicAttentionId(Long topicAttentionId) {
        this.topicAttentionId = topicAttentionId;
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
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
        sb.append(", topicAttentionId=").append(topicAttentionId);
        sb.append(", observerId=").append(observerId);
        sb.append(", topicId=").append(topicId);
        sb.append(", attentionStatus=").append(attentionStatus);
        sb.append(", reportType=").append(reportType);
        sb.append(", attentionDate=").append(attentionDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}