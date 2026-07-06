package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 教练评价（移动端，对应表 pt_coach_comment，第21步·运营域·评价）。
 * 会员在私教课完成后写评价：一预约一评价（uk_pt_coach_comment_appointment_id）。
 * api 包内独立实体（与 sys.entity.PtCoachCommentEntity 分属不同模块，各建各的，照项目惯例）。
 * myList 展示时联表回填教练名/门店名/回复内容（非表列）。
 *
 * @author claude
 */
public class PtCoachCommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long appointmentId;
    private Long coachId;
    private Long memberId;
    private String memberNickname;
    private String memberMobile;
    private Integer score;
    private String commentContent;
    /** 回复状态：0未回复 1已回复 */
    private Integer replyStatus;
    /** 处理状态：0待处理 1已跟进 2已忽略 */
    private Integer handleStatus;
    private Date commentTime;
    private Integer deleted;

    /* ---- 查询联表回填字段，非表列 ---- */
    private String coachName;
    private String storeName;
    /** 后台最新回复内容（myList 展示会员可见后台回复） */
    private String replyContent;
    private String replyTime;
    private String appointmentDate;
    private String startTime;
    private String endTime;

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

    public Date getCommentTime() { return commentTime; }
    public void setCommentTime(Date commentTime) { this.commentTime = commentTime; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public String getReplyTime() { return replyTime; }
    public void setReplyTime(String replyTime) { this.replyTime = replyTime; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
