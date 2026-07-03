package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 教练固定周排班表 pt_coach_schedule
 *
 * @author claude
 */
public class PtCoachScheduleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long coachId;
    private Long storeId;
    /** 星期：1周一 ... 7周日 */
    private Integer weekday;
    /** 开始时间 HH:mm */
    private String startTime;
    /** 结束时间 HH:mm */
    private String endTime;
    /** 是否启用：0否 1是 */
    private Integer isEnabled;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    /** 非持久字段：保存时按 星期×门店 笛卡尔展开 */
    private List<Integer> weekdays;
    /** 非持久字段：保存时按 星期×门店 笛卡尔展开 */
    private List<Long> storeIds;
    /** 非持久字段：列表展示用门店名 */
    private String storeName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Integer getWeekday() { return weekday; }
    public void setWeekday(Integer weekday) { this.weekday = weekday; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<Integer> getWeekdays() { return weekdays; }
    public void setWeekdays(List<Integer> weekdays) { this.weekdays = weekdays; }

    public List<Long> getStoreIds() { return storeIds; }
    public void setStoreIds(List<Long> storeIds) { this.storeIds = storeIds; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}
