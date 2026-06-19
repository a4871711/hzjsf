package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.List;

public class DynamicRequest implements Serializable {
    private Long publishId;

    private String content;

    private String publishImgUrl;

    private Long socialGroupId;

    private String dynamicTopic;

    private static final long serialVersionUID = 1L;

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

    public Long getSocialGroupId() {
        return socialGroupId;
    }

    public void setSocialGroupId(Long socialGroupId) {
        this.socialGroupId = socialGroupId;
    }

    public String getDynamicTopic() {
        return dynamicTopic;
    }

    public void setDynamicTopic(String dynamicTopic) {
        this.dynamicTopic = dynamicTopic;
    }

    @Override
    public String toString() {
        return "DynamicRequest{" +
                "publishId=" + publishId +
                ", content='" + content + '\'' +
                ", publishImgUrl='" + publishImgUrl + '\'' +
                ", socialGroupId=" + socialGroupId +
                ", DynamicTopic=" + dynamicTopic +
                '}';
    }
}
