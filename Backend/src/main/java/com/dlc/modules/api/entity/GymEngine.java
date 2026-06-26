package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class GymEngine implements Serializable {
    private Long gymEngineId;

    private String engineName;

    private String engineImgUrl;

    private String videoImgUrl;

    private String videoUrl;

    private Date createdDate;

    private String introduce;

    private static final long serialVersionUID = 1L;

    public Long getGymEngineId() {
        return gymEngineId;
    }

    public void setGymEngineId(Long gymEngineId) {
        this.gymEngineId = gymEngineId;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getEngineImgUrl() {
        return engineImgUrl;
    }

    public void setEngineImgUrl(String engineImgUrl) {
        this.engineImgUrl = engineImgUrl;
    }

    public String getVideoImgUrl() {
        return videoImgUrl;
    }

    public void setVideoImgUrl(String videoImgUrl) {
        this.videoImgUrl = videoImgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    @Override
    public String toString() {
        return "GymEngine{" +
                "gymEngineId=" + gymEngineId +
                ", engineName='" + engineName + '\'' +
                ", engineImgUrl='" + engineImgUrl + '\'' +
                ", videoImgUrl='" + videoImgUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", createdDate=" + createdDate +
                ", introduce='" + introduce + '\'' +
                '}';
    }
}