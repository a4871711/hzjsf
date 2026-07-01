package com.dlc.modules.api.vo;

import java.io.Serializable;

/**
 * 教练某星期的一段启用排班窗口（HH:mm），供时段切片使用。
 *
 * @author claude
 */
public class PtScheduleWindowVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String startTime;
    private String endTime;

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
