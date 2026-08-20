package com.dlc.modules.sys.entity;

import java.math.BigDecimal;

/** 后台审核提现的请求对象。 */
public class PtCoachWithdrawalReviewForm {
    private Long id;
    /** 1驳回，2通过 */
    private Integer status;
    private BigDecimal settlementAmount;
    private BigDecimal actualSettlementAmount;
    private String attachmentUrls;
    private String reviewRemark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public void setSettlementAmount(BigDecimal settlementAmount) { this.settlementAmount = settlementAmount; }
    public BigDecimal getActualSettlementAmount() { return actualSettlementAmount; }
    public void setActualSettlementAmount(BigDecimal actualSettlementAmount) { this.actualSettlementAmount = actualSettlementAmount; }
    public String getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(String attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    public String getReviewRemark() { return reviewRemark; }
    public void setReviewRemark(String reviewRemark) { this.reviewRemark = reviewRemark; }
}
