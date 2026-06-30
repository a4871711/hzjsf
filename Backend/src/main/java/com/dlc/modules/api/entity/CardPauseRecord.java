package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 停卡记录(对应表 card_pause_record)
 * 开放式停卡:apply 时 status=0、end_time/pause_days 空;resume 时回填实际天数并顺延会员卡有效期
 */
public class CardPauseRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 停卡记录ID */
    private Long pauseId;
    /** 会员ID */
    private Long userId;
    /** 被停的会员卡(card_order)ID */
    private Long cardOrderId;
    /** 所属自然月 yyyy-MM(限每月1次/全年12次) */
    private String pauseMonth;
    /** 实际停卡天数(恢复时回填,用于有效期顺延) */
    private Integer pauseDays;
    /** 停卡开始时间 */
    private Date startTime;
    /** 恢复时间(NULL=停卡中) */
    private Date endTime;
    /** 0停卡中 1已恢复 2已取消 */
    private Integer status;
    /** 创建时间 */
    private Date createdDate;

    public Long getPauseId() {
        return pauseId;
    }

    public void setPauseId(Long pauseId) {
        this.pauseId = pauseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardOrderId() {
        return cardOrderId;
    }

    public void setCardOrderId(Long cardOrderId) {
        this.cardOrderId = cardOrderId;
    }

    public String getPauseMonth() {
        return pauseMonth;
    }

    public void setPauseMonth(String pauseMonth) {
        this.pauseMonth = pauseMonth;
    }

    public Integer getPauseDays() {
        return pauseDays;
    }

    public void setPauseDays(Integer pauseDays) {
        this.pauseDays = pauseDays;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
