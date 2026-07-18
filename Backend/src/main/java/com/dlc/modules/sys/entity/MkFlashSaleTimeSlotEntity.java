package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * 秒杀活动-每日投放时段 mk_flash_sale_time_slot（循环生效用，可多个）。
 *
 * @author claude
 */
public class MkFlashSaleTimeSlotEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long activityId;
    private String startHm;
    private String endHm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getStartHm() { return startHm; }
    public void setStartHm(String startHm) { this.startHm = startHm; }

    public String getEndHm() { return endHm; }
    public void setEndHm(String endHm) { this.endHm = endHm; }
}
