package com.dlc.modules.api.vo;

import java.io.Serializable;

/**
 * 教练某星期的一段启用排班窗口（HH:mm），供时段切片使用。
 *
 * @author claude
 */
public class PtScheduleWindowVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 排班所属门店ID(第14步补充:预约侧切片需按窗口落门店快照) */
    private Long storeId;
    private String startTime;
    private String endTime;

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
