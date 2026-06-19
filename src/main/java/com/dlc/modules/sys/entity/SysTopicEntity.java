package com.dlc.modules.sys.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * 话题表
 *
 * @author wangsheng
 * @email
 * @date 2018-09-15 09:27:07
 */
public class SysTopicEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 话题ID
     */
    private Long topicId;
    /**
     * 话题名称
     */
    private String topicName;
    /**
     * 封面图
     */
    private String firstImgUrl;
    /**
     * 动态数量
     */
    private Integer dyNum;
    /**
     * 关注数量
     */
    private Integer attentionNum;
    /**
     * 话题介绍
     */
    private String topicIntroduce;
    /**
     * 创建时间
     */
    private Date createdDate;


    /**
     * 设置：话题ID
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * 获取：话题ID
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * 设置：话题名称
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * 获取：话题名称
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * 设置：封面图
     */
    public void setFirstImgUrl(String firstImgUrl) {
        this.firstImgUrl = firstImgUrl;
    }

    /**
     * 获取：封面图
     */
    public String getFirstImgUrl() {
        return firstImgUrl;
    }

    /**
     * 设置：动态数量
     */
    public void setDyNum(Integer dyNum) {
        this.dyNum = dyNum;
    }

    /**
     * 获取：动态数量
     */
    public Integer getDyNum() {
        return dyNum;
    }

    /**
     * 设置：关注数量
     */
    public void setAttentionNum(Integer attentionNum) {
        this.attentionNum = attentionNum;
    }

    /**
     * 获取：关注数量
     */
    public Integer getAttentionNum() {
        return attentionNum;
    }

    /**
     * 设置：话题介绍
     */
    public void setTopicIntroduce(String topicIntroduce) {
        this.topicIntroduce = topicIntroduce;
    }

    /**
     * 获取：话题介绍
     */
    public String getTopicIntroduce() {
        return topicIntroduce;
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
}
