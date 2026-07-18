package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 限时秒杀活动主表 mk_flash_sale_activity（多商品版，照《秒杀功能.dc.html》设计稿）。
 * 一活动多商品（子表 mk_flash_sale_product）；投放方式单次/循环；循环含生效日+每日时段（mk_flash_sale_time_slot）。
 * 原单商品列(product_id/flash_sale_price/activity_stock/sold_count/mt_group_buy_id/dy_group_buy_id)已弃用。
 *
 * @author claude
 */
public class MkFlashSaleActivityEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String activityName;
    /** 业务类型：1私教商品 2会员卡(→fit_card.fitCardId) 3权益卡(→vip_benefit_card.vip_card_id) */
    private Integer bizType;
    /** 秒杀首图URL（会员端卡片展示） */
    private String coverUrl;
    /** 投放方式：1单次生效 2循环生效 */
    private Integer deliveryType;
    /** 单次生效-起止（String 承接 DATE_FORMAT 结果） */
    private String startTime;
    private String endTime;
    /** 循环生效-活动周期（yyyy-MM-dd） */
    private String activityStartDate;
    private String activityEndDate;
    /** 循环生效-生效日 CSV：1..7（周一..周日） */
    private String weekDays;
    /** 库存方式：1每日投放 2投放总量 */
    private Integer stockMode;
    /** 每人限购，NULL=不限购 */
    private Integer purchaseLimit;
    /** 秒杀前倒计时：0关 1开 */
    private Integer countdownEnabled;
    /** 倒计时预热分钟（1/5/15） */
    private Integer countdownMinutes;
    /** 售罄后：1自动结束恢复原价 2显示售罄待结束 */
    private Integer soldOutAction;
    /** 状态：1上架 0下架 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    /** 是否删除：0否 1是 */
    private Integer deleted;

    /* ===== 非持久字段（save/update 入参 + queryObject 回显）===== */
    /** 秒杀商品列表（子表 mk_flash_sale_product） */
    private List<MkFlashSaleProductEntity> products;
    /** 每日投放时段（子表 mk_flash_sale_time_slot，循环生效用） */
    private List<MkFlashSaleTimeSlotEntity> timeSlots;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Integer getBizType() { return bizType; }
    public void setBizType(Integer bizType) { this.bizType = bizType; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public Integer getDeliveryType() { return deliveryType; }
    public void setDeliveryType(Integer deliveryType) { this.deliveryType = deliveryType; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(String activityStartDate) { this.activityStartDate = activityStartDate; }

    public String getActivityEndDate() { return activityEndDate; }
    public void setActivityEndDate(String activityEndDate) { this.activityEndDate = activityEndDate; }

    public String getWeekDays() { return weekDays; }
    public void setWeekDays(String weekDays) { this.weekDays = weekDays; }

    public Integer getStockMode() { return stockMode; }
    public void setStockMode(Integer stockMode) { this.stockMode = stockMode; }

    public Integer getPurchaseLimit() { return purchaseLimit; }
    public void setPurchaseLimit(Integer purchaseLimit) { this.purchaseLimit = purchaseLimit; }

    public Integer getCountdownEnabled() { return countdownEnabled; }
    public void setCountdownEnabled(Integer countdownEnabled) { this.countdownEnabled = countdownEnabled; }

    public Integer getCountdownMinutes() { return countdownMinutes; }
    public void setCountdownMinutes(Integer countdownMinutes) { this.countdownMinutes = countdownMinutes; }

    public Integer getSoldOutAction() { return soldOutAction; }
    public void setSoldOutAction(Integer soldOutAction) { this.soldOutAction = soldOutAction; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public List<MkFlashSaleProductEntity> getProducts() { return products; }
    public void setProducts(List<MkFlashSaleProductEntity> products) { this.products = products; }

    public List<MkFlashSaleTimeSlotEntity> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<MkFlashSaleTimeSlotEntity> timeSlots) { this.timeSlots = timeSlots; }
}
