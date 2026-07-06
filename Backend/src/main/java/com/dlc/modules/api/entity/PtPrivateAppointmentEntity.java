package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 私教预约记录(对应表 pt_private_appointment),第14步预约全链路核心实体。
 * 状态机:1已预约 → 2已取消 / 3已完成(核销) / 4爽约;已取消(2)不占容量。
 * slot_key 为路线B加固列(仅 service_type=1 一对一写入 coachId:date:startTime,
 * 靠 uk_pt_appt_slot 唯一键兜底防双约;一对多为 NULL 不参与唯一约束)。
 *
 * @author claude
 */
public class PtPrivateAppointmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 预约编号,PA+yyyyMMddHHmmss+随机,唯一 */
    private String appointmentNo;
    /** 来源私教订单ID(快照自权益) */
    private Long orderId;
    /** 会员私教权益ID(冻结/核销/取消课时的账本) */
    private Long benefitId;
    private Long memberId;
    private Long productId;
    private Long coachId;
    /** 上课门店ID */
    private Long storeId;
    /** 预约日期 yyyy-MM-dd */
    private String appointmentDate;
    /** 开始时间 HH:mm */
    private String startTime;
    /** 结束时间 HH:mm */
    private String endTime;
    /** 一对一占位键 coachId:date:startTime,仅一对一写入,取消时置 NULL 释放 */
    private String slotKey;
    /** 本次消耗课时,一期固定1 */
    private Integer lessonCount;
    /** 预约状态:1已预约 2已取消 3已完成 4爽约 */
    private Integer appointmentStatus;
    private String cancelReason;
    private Date cancelAt;
    /** 完成时间,完成即视为核销 */
    private Date finishAt;
    /** 创建人ID:会员自约=会员ID,教练代约=管理员ID */
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    private Integer deleted;

    /* ---- 以下为查询联表回填展示字段,非表列 ---- */
    private String productName;
    private String coachName;
    private String storeName;
    private String memberName;
    private String memberMobile;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getBenefitId() { return benefitId; }
    public void setBenefitId(Long benefitId) { this.benefitId = benefitId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getSlotKey() { return slotKey; }
    public void setSlotKey(String slotKey) { this.slotKey = slotKey; }

    public Integer getLessonCount() { return lessonCount; }
    public void setLessonCount(Integer lessonCount) { this.lessonCount = lessonCount; }

    public Integer getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(Integer appointmentStatus) { this.appointmentStatus = appointmentStatus; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public Date getCancelAt() { return cancelAt; }
    public void setCancelAt(Date cancelAt) { this.cancelAt = cancelAt; }

    public Date getFinishAt() { return finishAt; }
    public void setFinishAt(Date finishAt) { this.finishAt = finishAt; }

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

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }
}
