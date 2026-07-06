package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教订单分期账单(对应表 pt_order_installment_bill,第20步资金域),分期计划下每一期的应付明细。
 * <p>下单生成全量账单(含首付期),逐期支付/逾期。uk_(plan_id,period_no) 账单去重+期数唯一护栏;
 * pay_order_no(本期支付单号)唯一键做回调入账幂等(见详细文档§2.5【补充】/§5.4)。</p>
 *
 * @author claude
 */
public class PtOrderInstallmentBillEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 分期计划ID */
    private Long planId;
    /** 期数(1=首付期),与 plan_id 组唯一键 uk_period */
    private Integer periodNo;
    /** 本期应付 */
    private BigDecimal dueAmount;
    /** 本期实付(支付成功置=due_amount) */
    private BigDecimal paidAmount;
    /** 应付日期(按 installment_interval_months 推算) */
    private Date dueDate;
    /** 支付时间 */
    private Date paidTime;
    /** 账单状态:0待支付 1已支付 2已逾期 3已关闭 */
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
    /** 本期支付单号(末位拼分期后缀),回调入账幂等键 */
    private String payOrderNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Integer getPeriodNo() { return periodNo; }
    public void setPeriodNo(Integer periodNo) { this.periodNo = periodNo; }

    public BigDecimal getDueAmount() { return dueAmount; }
    public void setDueAmount(BigDecimal dueAmount) { this.dueAmount = dueAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getPaidTime() { return paidTime; }
    public void setPaidTime(Date paidTime) { this.paidTime = paidTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getPayOrderNo() { return payOrderNo; }
    public void setPayOrderNo(String payOrderNo) { this.payOrderNo = payOrderNo; }
}
