package com.dlc.modules.api.vo;

import java.io.Serializable;

/**
 * 私教可约时段（纯计算结果，不落库）。
 * 生成规则见需求 5.4：按教练排班窗口，结合商品单次服务时长 + 预约间隔切片。
 * 容量口径：交易域 pt_private_appointment 未建成前，remaining 恒等于 capacity（无已占用记录）；
 * 第14步接入预约表后，remaining 需扣减该时段已生效预约数，与本接口口径保持一致。
 *
 * @author claude
 */
public class PtAvailableSlotVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** yyyy-MM-dd */
    private String date;
    /** HH:mm */
    private String startTime;
    /** HH:mm */
    private String endTime;
    private Integer capacity;
    private Integer remaining;

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
