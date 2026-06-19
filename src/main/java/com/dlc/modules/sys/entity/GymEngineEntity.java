package com.dlc.modules.sys.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * 机械表
 *
 * @author daibenting
 * @email
 * @date 2018-09-11 09:17:04
 */
public class GymEngineEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long gymEngineId;

    private Long gtId;
    private String gtName;
    /**
     * 器械名称（类型）
     */
    private String engineName;
    /**
     * 视频链接图
     */
    private String videoImgUrl;
    /**
     * 视频链接url
     */
    private String videoUrl;
    /**
     * 器械介绍
     */
    private String introduce;
    /**
     * 创建时间
     */
    private Date createdDate;

    public GymEngineEntity() {
    }

    public GymEngineEntity(Long gymEngineId, Long gtId, String engineName, String videoImgUrl, String videoUrl, String introduce, Date createdDate) {
        this.gymEngineId = gymEngineId;
        this.gtId = gtId;
        this.engineName = engineName;
        this.videoImgUrl = videoImgUrl;
        this.videoUrl = videoUrl;
        this.introduce = introduce;
        this.createdDate = createdDate;
    }

    /**
     * 设置：ID
     */
    public void setGymEngineId(Long gymEngineId) {
        this.gymEngineId = gymEngineId;
    }

    /**
     * 获取：ID
     */
    public Long getGymEngineId() {
        return gymEngineId;
    }

    /**
     * 设置：器械名称（类型）
     */
    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    /**
     * 获取：器械名称（类型）
     */
    public String getEngineName() {
        return engineName;
    }

    /**
     * 设置：器械图片
     */
    public void setVideoImgUrl(String videoImgUrl) {
        this.videoImgUrl = videoImgUrl;
    }

    /**
     * 获取：器械图片
     */
    public String getVideoImgUrl() {
        return videoImgUrl;
    }

    /**
     * 获取：视频链接url
     */
    public String getVideoUrl() {
        return videoUrl;
    }
    /**
     * 设置：视频链接url
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * 设置：器械介绍
     */
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    /**
     * 获取：器械介绍
     */
    public String getIntroduce() {
        return introduce;
    }

    /**
     * 设置：创建时间
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 获取：创建时间
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    public Long getGtId() {
        return gtId;
    }

    public void setGtId(Long gtId) {
        this.gtId = gtId;
    }

    public String getGtName() {
        return gtName;
    }

    public void setGtName(String gtName) {
        this.gtName = gtName;
    }
}
