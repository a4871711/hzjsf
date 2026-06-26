package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class Dynamic implements Serializable {
    private Long dynamicId;

    private Long publishId;

    private String content;

    private String publishImgUrl;

    private Long topicId;

    private Long socialGroupId;

    private Integer readCount;

    private Integer dzCount;

    private Integer plCount;

    private Date publishDate;

    private static final long serialVersionUID = 1L;

    public Long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public Long getPublishId() {
        return publishId;
    }

    public void setPublishId(Long publishId) {
        this.publishId = publishId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishImgUrl() {
        return publishImgUrl;
    }

    public void setPublishImgUrl(String publishImgUrl) {
        this.publishImgUrl = publishImgUrl;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getSocialGroupId() {
        return socialGroupId;
    }

    public void setSocialGroupId(Long socialGroupId) {
        this.socialGroupId = socialGroupId;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getDzCount() {
        return dzCount;
    }

    public void setDzCount(Integer dzCount) {
        this.dzCount = dzCount;
    }

    public Integer getPlCount() {
        return plCount;
    }

    public void setPlCount(Integer plCount) {
        this.plCount = plCount;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return "Dynamic{" +
                "dynamicId=" + dynamicId +
                ", publishId=" + publishId +
                ", content='" + content + '\'' +
                ", publishImgUrl='" + publishImgUrl + '\'' +
                ", topicId=" + topicId +
                ", socialGroupId=" + socialGroupId +
                ", readCount=" + readCount +
                ", dzCount=" + dzCount +
                ", plCount=" + plCount +
                ", publishDate=" + publishDate +
                '}';
    }
}