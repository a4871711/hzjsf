package com.dlc.modules.sys.entity;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

public class SysActivityTrainEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    //id
    private Long activityTrainId;
    //视频图片
    @NotBlank(message = "请选择视频图")
    private String videoImgUrl;
    //视频链接
    @NotBlank(message = "请选择视频")
    private String videoUrl;
    //动作名称
    @NotBlank(message = "请输入动作名")
    private String trainName;
    //动作图片
    private String trainImgUrl;
    //动作介绍
    @NotBlank(message = "请说明动作")
    private String introduce;
    //创建时间
    private Date createdDate;

    private Long atId;

    private String atName;

    public SysActivityTrainEntity() {
        super();
    }


    public SysActivityTrainEntity(Long activityTrainId, String videoImgUrl, String videoUrl, String trainName, String trainImgUrl, String introduce, Date createdDate, Long atId) {
        this.activityTrainId = activityTrainId;
        this.videoImgUrl = videoImgUrl;
        this.videoUrl = videoUrl;
        this.trainName = trainName;
        this.trainImgUrl = trainImgUrl;
        this.introduce = introduce;
        this.createdDate = createdDate;
        this.atId = atId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getActivityTrainId() {
        return activityTrainId;
    }

    public void setActivityTrainId(Long activityTrainId) {
        this.activityTrainId = activityTrainId;
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

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainImgUrl() {
        return trainImgUrl;
    }

    public void setTrainImgUrl(String trainImgUrl) {
        this.trainImgUrl = trainImgUrl;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getAtId() {
        return atId;
    }

    public void setAtId(Long atId) {
        this.atId = atId;
    }

    public String getAtName() {
        return atName;
    }

    public void setAtName(String atName) {
        this.atName = atName;
    }
}
