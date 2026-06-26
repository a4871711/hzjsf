package com.dlc.modules.sys.entity;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * s-live说
 */
public class SysInformAtionEntity {
    /**
     * 咨询id
     */
    private Long informationId;
    /**
     * 咨询类型
     */
    private Long infType;

    private Long imgType;

    public Long getImgType() {
        return imgType;
    }

    public void setImgType(Long imgType) {
        this.imgType = imgType;
    }

    /**
     * 标题
     */
    private String title;
    /**
     * 发布时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    /**
     * 列表显示图
     */
    private String infImgUrl;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createdDate;

    public SysInformAtionEntity() {
        super();
    }

    public SysInformAtionEntity(Long informationId, Long infType, String title, Date publishTime, String infImgUrl, String content, Date createdDate) {
        this.informationId = informationId;
        this.infType = infType;
        this.title = title;
        this.publishTime = publishTime;
        this.infImgUrl = infImgUrl;
        this.content = content;
        this.createdDate = createdDate;
    }

    public Long getInformationId() {

        return informationId;
    }

    public void setInformationId(Long informationId) {
        this.informationId = informationId;
    }

    public Long getInfType() {
        return infType;
    }

    public void setInfType(Long infType) {
        this.infType = infType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        this.infImgUrl = infImgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
