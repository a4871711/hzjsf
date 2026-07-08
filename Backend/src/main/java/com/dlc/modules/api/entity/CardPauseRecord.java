package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 停卡记录(对应表 card_pause_record)
 * 定期停卡:免费(pause_type=0)自选1~7天立即生效并预顺延有效期;付费(pause_type=1)按规则档位建待支付单(status=10),
 * 微信支付回调成功后生效。end_time=计划恢复时间(start+pause_days),到期零动作;提前取消置 status=2 并按未用天数扣回顺延。
 * 存量兼容:end_time IS NULL 的旧开放式记录,取消时走旧 resume 语义(按实际天数顺延,status→1)。
 * status:10待支付 / 0生效 / 1已恢复(仅存量历史) / 2已取消 / 3已关闭未支付
 */
public class CardPauseRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 停卡记录ID */
    private Long pauseId;
    /** 会员ID */
    private Long userId;
    /** 被停的会员卡(card_order)ID */
    private Long cardOrderId;
    /** 停卡类型 0免费 1付费 */
    private Integer pauseType;
    /** 付费停卡命中的规则(vip_pause_rule)ID */
    private Long pauseRuleId;
    /** 付费金额(免费停卡为0) */
    private BigDecimal amount;
    /** 付费停卡支付单号(末位后缀c,唯一) */
    private String payOrderNo;
    /** 微信交易号 */
    private String transactionId;
    /** 支付成功时间 */
    private Date payTime;
    /** 停卡天数(免费自选1~7,付费取档位天数) */
    private Integer pauseDays;
    /** 停卡开始时间 */
    private Date startTime;
    /** 计划结束时间=start+pause_days(旧开放式存量为NULL) */
    private Date endTime;
    /** 提前取消时间 */
    private Date cancelTime;
    /** 实际停卡天数(提前取消时回填) */
    private Integer actualDays;
    /** 10待支付 0生效 1已恢复(仅存量历史) 2已取消 3已关闭未支付 */
    private Integer status;
    /** 创建时间 */
    private Date createdDate;

    /** 非表字段:列表展示状态(生效中但已到计划结束时间 → 99已完成,其余同 status) */
    private Integer displayStatus;

    public Long getPauseId() {
        return pauseId;
    }

    public void setPauseId(Long pauseId) {
        this.pauseId = pauseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardOrderId() {
        return cardOrderId;
    }

    public void setCardOrderId(Long cardOrderId) {
        this.cardOrderId = cardOrderId;
    }

    public Integer getPauseType() {
        return pauseType;
    }

    public void setPauseType(Integer pauseType) {
        this.pauseType = pauseType;
    }

    public Long getPauseRuleId() {
        return pauseRuleId;
    }

    public void setPauseRuleId(Long pauseRuleId) {
        this.pauseRuleId = pauseRuleId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPayOrderNo() {
        return payOrderNo;
    }

    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getPauseDays() {
        return pauseDays;
    }

    public void setPauseDays(Integer pauseDays) {
        this.pauseDays = pauseDays;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Integer getActualDays() {
        return actualDays;
    }

    public void setActualDays(Integer actualDays) {
        this.actualDays = actualDays;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Integer displayStatus) {
        this.displayStatus = displayStatus;
    }
}
