package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 团课转私教识别规则表 pt_group_to_private_rule（第22步·运营域·团课转私教）。
 * 高意向识别阈值（出勤 + 购课双维度，至少配一组）。
 *
 * @author claude
 */
public class PtGroupToPrivateRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruleName;
    /** 出勤统计周期天数 */
    private Integer attendanceDays;
    /** 出勤次数阈值 */
    private Integer attendanceThreshold;
    /** 购课统计周期天数 */
    private Integer purchaseDays;
    /** 购课次数阈值 */
    private Integer purchaseThreshold;
    /** 状态：1启用 0停用 */
    private Integer status;
    private String remark;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getAttendanceDays() { return attendanceDays; }
    public void setAttendanceDays(Integer attendanceDays) { this.attendanceDays = attendanceDays; }

    public Integer getAttendanceThreshold() { return attendanceThreshold; }
    public void setAttendanceThreshold(Integer attendanceThreshold) { this.attendanceThreshold = attendanceThreshold; }

    public Integer getPurchaseDays() { return purchaseDays; }
    public void setPurchaseDays(Integer purchaseDays) { this.purchaseDays = purchaseDays; }

    public Integer getPurchaseThreshold() { return purchaseThreshold; }
    public void setPurchaseThreshold(Integer purchaseThreshold) { this.purchaseThreshold = purchaseThreshold; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
