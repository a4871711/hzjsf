package com.dlc.modules.api.vo;

import java.io.Serializable;

/**
 * 私教可约时段（纯计算结果，不落库）。
 * 生成规则见需求 5.4：按教练排班窗口，结合商品单次服务时长 + 预约间隔切片。
 * 容量口径（第14步统一）：remaining = capacity - PtPrivateAppointmentDao.countOccupied（状态 1/3/4 已占用），
 * 与下单护栏共用同一条 COUNT SQL，保证"列表余量=下单余量"。
 *
 * @author claude
 */
public class PtAvailableSlotVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 上课门店ID(时段所属排班窗口的门店,book 时原样带回) */
    private Long storeId;
    /** yyyy-MM-dd */
    private String date;
    /** HH:mm */
    private String startTime;
    /** HH:mm */
    private String endTime;
    private Integer capacity;
    private Integer remaining;

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getRemaining() { return remaining; }
    public void setRemaining(Integer remaining) { this.remaining = remaining; }
}
