package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Share implements Serializable {
    private Long shareId;

    private Long userId;

    private Integer shareType;

    private String content;

    private String imgUrl;

    private String linkUrl;

    private Integer shareChannel;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getShareType() {
        return shareType;
    }

    public void setShareType(Integer shareType) {
        this.shareType = shareType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getShareChannel() {
        return shareChannel;
    }

    public void setShareChannel(Integer shareChannel) {
        this.shareChannel = shareChannel;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Share{" +
                "shareId=" + shareId +
                ", userId=" + userId +
                ", shareType=" + shareType +
                ", content='" + content + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", shareChannel=" + shareChannel +
                ", createdDate=" + createdDate +
                '}';
    }

    public Share(Long shareId, Long userId, Integer shareType, String content, String imgUrl, String linkUrl, Integer shareChannel, Date createdDate) {
        this.shareId = shareId;
        this.userId = userId;
        this.shareType = shareType;
        this.content = content;
        this.imgUrl = imgUrl;
        this.linkUrl = linkUrl;
        this.shareChannel = shareChannel;
        this.createdDate = createdDate;
    }
}