package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Information implements Serializable {
    private Long informationId;

    private Boolean infType;

    private String title;

    private Date publishTime;

    private String infImgUrl;

    private Date createdDate;

    private String content;

    private static final long serialVersionUID = 1L;

    public Information(Long informationId, Boolean infType, String title, Date publishTime, String infImgUrl, Date createdDate, String content) {
        this.informationId = informationId;
        this.infType = infType;
        this.title = title;
        this.publishTime = publishTime;
        this.infImgUrl = infImgUrl;
        this.createdDate = createdDate;
        this.content = content;
    }

    public Information() {
        super();
    }

    public Long getInformationId() {
        return informationId;
    }

    public void setInformationId(Long informationId) {
        this.informationId = informationId;
    }

    public Boolean getInfType() {
        return infType;
    }

    public void setInfType(Boolean infType) {
        this.infType = infType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getInfImgUrl() {
        return infImgUrl;
    }

    public void setInfImgUrl(String infImgUrl) {
        this.infImgUrl = infImgUrl == null ? null : infImgUrl.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", informationId=").append(informationId);
        sb.append(", infType=").append(infType);
        sb.append(", title=").append(title);
        sb.append(", publishTime=").append(publishTime);
        sb.append(", infImgUrl=").append(infImgUrl);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}