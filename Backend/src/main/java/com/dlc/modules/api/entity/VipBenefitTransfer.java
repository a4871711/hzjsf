package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 权益转让申请/记录(状态机,对应表 vip_benefit_transfer)
 * 一次转让申请的全生命周期:待付费→待审核→待受让人确认→生效/驳回/拒绝/超时/撤回。
 * 第9步只建数据模型;实际状态流转(发起/审核/确认/退费)在第10~13步。
 * cardName 为非表字段,列表 join vip_benefit_card 带出卡名展示用。
 */
public class VipBenefitTransfer implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 转让单ID */
    private Long transferId;
    /** 被转让的权益实例ID */
    private Long vipBenefitId;
    /** 转让人 */
    private Long fromUserId;
    /** 受让人 */
    private Long toUserId;
    /** 转让人门店 */
    private Long fromStoreId;
    /** 受让人门店 */
    private Long toStoreId;
    /** 转让费用(转让人付) */
    private BigDecimal serviceFee;
    /** 转让费用微信支付单号(末位后缀7) */
    private String serviceFeeOrderNo;
    /** 微信交易号 */
    private String transactionNumber;
    /** 10待付费 20待审核 31已驳回 40待受让人确认 51已拒绝 52已超时 60已撤回 70已生效 */
    private Integer status;
    /** 审核管理员ID */
    private Long auditUserId;
    /** 审核时间 */
    private Date auditTime;
    /** 审核备注 */
    private String auditRemark;
    /** 受让人确认截止时间 */
    private Date confirmDeadline;
    /** 0未退 1已退 */
    private Integer refundStatus;
    /** 创建时间 */
    private Date createdDate;
    /** 生效时间 */
    private Date effectTime;

    /** 非表字段:被转让权益的来源权益卡名称(列表展示用) */
    private String cardName;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getVipBenefitId() {
        return vipBenefitId;
    }

    public void setVipBenefitId(Long vipBenefitId) {
        this.vipBenefitId = vipBenefitId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getFromStoreId() {
        return fromStoreId;
    }

    public void setFromStoreId(Long fromStoreId) {
        this.fromStoreId = fromStoreId;
    }

    public Long getToStoreId() {
        return toStoreId;
    }

    public void setToStoreId(Long toStoreId) {
        this.toStoreId = toStoreId;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getServiceFeeOrderNo() {
        return serviceFeeOrderNo;
    }

    public void setServiceFeeOrderNo(String serviceFeeOrderNo) {
        this.serviceFeeOrderNo = serviceFeeOrderNo;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Date getConfirmDeadline() {
        return confirmDeadline;
    }

    public void setConfirmDeadline(Date confirmDeadline) {
        this.confirmDeadline = confirmDeadline;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
