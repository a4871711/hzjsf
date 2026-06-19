package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class FaceIdentyRecord implements Serializable {
    private Long firId;

    private Long userId;

    private String deviceNo;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public FaceIdentyRecord(Long firId, Long userId, String deviceNo, Date createTime) {
        this.firId = firId;
        this.userId = userId;
        this.deviceNo = deviceNo;
        this.createTime = createTime;
    }

    public FaceIdentyRecord() {
        super();
    }

    public Long getFirId() {
        return firId;
    }

    public void setFirId(Long firId) {
        this.firId = firId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo == null ? null : deviceNo.trim();
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
        sb.append(", firId=").append(firId);
        sb.append(", userId=").append(userId);
        sb.append(", deviceNo=").append(deviceNo);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}