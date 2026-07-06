package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 教练评价回复表 pt_coach_comment_reply（第21步·运营域·评价）。
 * 后台对某条评价的回复内容，评价 1:N 回复（本期一般一条）。
 *
 * @author claude
 */
public class PtCoachCommentReplyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long commentId;
    private String replyContent;
    /** 回复人（sys 用户ID） */
    private Long replyBy;
    private Date replyTime;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Long getReplyBy() { return replyBy; }
    public void setReplyBy(Long replyBy) { this.replyBy = replyBy; }

    public Date getReplyTime() { return replyTime; }
    public void setReplyTime(Date replyTime) { this.replyTime = replyTime; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
