package com.dlc.modules.api.entity;

import com.dlc.modules.sys.entity.SysActivityTrainEntity;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

public class ActivityTrain extends SysActivityTrainEntity implements Serializable {
    private Long activityTrainId;

    private String trainName;

    private String trainImgUrl;

    private String videoImgUrl;

    private String videoUrl;

    private Date createdDate;

    private String introduce;

    private static final long serialVersionUID = 1L;

    @Override
    public Long getActivityTrainId() {
        return activityTrainId;
    }

    @Override
    public void setActivityTrainId(Long activityTrainId) {
        this.activityTrainId = activityTrainId;
    }

    @Override
    public String getTrainName() {
        return trainName;
    }

    @Override
    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    @Override
    public String getTrainImgUrl() {
        return trainImgUrl;
    }

    @Override
    public void setTrainImgUrl(String trainImgUrl) {
        this.trainImgUrl = trainImgUrl;
    }

    @Override
    public String getVideoImgUrl() {
        return videoImgUrl;
    }

    @Override
    public void setVideoImgUrl(String videoImgUrl) {
        this.videoImgUrl = videoImgUrl;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String getIntroduce() {
        return introduce;
    }

    @Override
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    @Override
    public String toString() {
        return "ActivityTrain{" +
                "activityTrainId=" + activityTrainId +
                ", trainName='" + trainName + '\'' +
                ", trainImgUrl='" + trainImgUrl + '\'' +
                ", videoImgUrl='" + videoImgUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", createdDate=" + createdDate +
                ", introduce='" + introduce + '\'' +
                '}';
    }
}