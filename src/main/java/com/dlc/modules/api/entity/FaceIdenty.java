package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class FaceIdenty implements Serializable {
    private Long faceId;

    private Long userId;

    private String faceToken;

    private String location;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public FaceIdenty(Long faceId, Long userId, String faceToken, String location, Date createTime) {
        this.faceId = faceId;
        this.userId = userId;
        this.faceToken = faceToken;
        this.location = location;
        this.createTime = createTime;
    }

    public FaceIdenty() {
        super();
    }

    public Long getFaceId() {
        return faceId;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken == null ? null : faceToken.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
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
        sb.append(", faceId=").append(faceId);
        sb.append(", userId=").append(userId);
        sb.append(", faceToken=").append(faceToken);
        sb.append(", location=").append(location);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}