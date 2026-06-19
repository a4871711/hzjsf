package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class BodyShape implements Serializable {
    private Long id;

    private String scanId;

    private Long userId;

    private String result;

    private Long gymEngineId;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public BodyShape(Long id, String scanId, Long userId, String results, Date createTime) {
        this.id = id;
        this.scanId = scanId;
        this.userId = userId;
        this.result = results;
        this.createTime = createTime;
    }

    public BodyShape() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId == null ? null : scanId.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getResults() {
        return result;
    }

    public void setResults(String results) {
        this.result = results == null ? null : results.trim();
    }

    public Long getGymEngineId() {
        return gymEngineId;
    }

    public void setGymEngineId(Long gymEngineId) {
        this.gymEngineId = gymEngineId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", scanId=").append(scanId);
        sb.append(", userId=").append(userId);
        sb.append(", result=").append(result);
        sb.append(", gymEngineId=").append(gymEngineId);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}