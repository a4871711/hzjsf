package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 教练评价表 pt_coach_comment（第21步·运营域·评价）。
 * 一预约一评价（uk_pt_coach_comment_appointment_id）；评价由会员端「完成评价」写入，
 * 后台只做查询/回复/处理/删除/手动补录。本表无 store_id，后台门店隔离需 JOIN pt_private_appointment 取 store_id。
 * 非持久字段（coachName/memberInfo/storeName/replyList）挂本实体供列表与详情回填，不另建 VO。
 *
 * @author claude
 */
public class PtCoachCommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 关联预约ID（唯一键，一预约一评价） */
    private Long appointmentId;
    private Long coachId;
    private Long memberId;
    /** 会员昵称快照 */
    private String memberNickname;
    /** 会员手机号快照 */
    private String memberMobile;
    /** 评分：1-5星 */
    private Integer score;
    private String commentContent;
    /** 回复状态：0未回复 1已回复 */
    private Integer replyStatus;
    /** 处理状态：0待处理 1已跟进 2已忽略 */
    private Integer handleStatus;
    /** 处理备注（仅后台可见） */
    private String handleRemark;
    private Date commentTime;
    private Integer deleted;
    private Date createdAt;
    private Date updatedAt;

    /* ---- 以下为查询联表回填展示字段，非表列 ---- */
    private String coachName;
    private String memberInfo;
    private String storeName;
    private Long storeId;
    /** 详情带出的回复列表 */
    private List<PtCoachCommentReplyEntity> replyList;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getMemberNickname() { return memberNickname; }
    public void setMemberNickname(String memberNickname) { this.memberNickname = memberNickname; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getCommentContent() { return commentContent; }
    public void setCommentContent(String commentContent) { this.commentContent = commentContent; }

    public Integer getReplyStatus() { return replyStatus; }
    public void setReplyStatus(Integer replyStatus) { this.replyStatus = replyStatus; }

    public Integer getHandleStatus() { return handleStatus; }
    public void setHandleStatus(Integer handleStatus) { this.handleStatus = handleStatus; }

    public String getHandleRemark() { return handleRemark; }
    public void setHandleRemark(String handleRemark) { this.handleRemark = handleRemark; }

    public Date getCommentTime() { return commentTime; }
    public void setCommentTime(Date commentTime) { this.commentTime = commentTime; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getMemberInfo() { return memberInfo; }
    public void setMemberInfo(String memberInfo) { this.memberInfo = memberInfo; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public List<PtCoachCommentReplyEntity> getReplyList() { return replyList; }
    public void setReplyList(List<PtCoachCommentReplyEntity> replyList) { this.replyList = replyList; }
}
