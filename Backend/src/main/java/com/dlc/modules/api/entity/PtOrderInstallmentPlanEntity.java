package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 私教订单分期计划(对应表 pt_order_installment_plan,第20步资金域),与 pt_private_order 一对一。
 * <p>期数口径:installment_count=含首付期在内的总期数,period_no=1 即首付期(见详细文档§5.2)。
 * uk_order_id 兜底"一订单一计划"并发建计划。金额快照(total/down)下单时锁定,后续规则改动不影响在途计划。</p>
 *
 * @author claude
 */
public class PtOrderInstallmentPlanEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 私教订单ID,唯一键 uk_order_id(一订单一计划,建计划幂等护栏) */
    private Long orderId;
    /** 冗余会员ID */
    private Long memberId;
    /** 冗余私教商品ID */
    private Long productId;
    /** 订单总金额(=商品应付总价,下单快照) */
    private BigDecimal totalAmount;
    /** 首付金额(快照自规则) */
    private BigDecimal downPaymentAmount;
    /** 已付总额(首付成功=首付,之后逐期累加) */
    private BigDecimal paidAmount;
    /** 未付总额(total-paid,每次付款后行锁内重算) */
    private BigDecimal unpaidAmount;
    /** 总期数(含首付期) */
    private Integer installmentCount;
    /** 当前执行期数(首付成功后置 2 等待第2期) */
    private Integer currentPeriod;
    /** 分期状态:1进行中 2已结清 3已逾期 4已关闭 */
    private Integer status;
    /** 权益激活时间(首付成功回调时落) */
    private Date activatedAt;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDownPaymentAmount() { return downPaymentAmount; }
    public void setDownPaymentAmount(BigDecimal downPaymentAmount) { this.downPaymentAmount = downPaymentAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(BigDecimal unpaidAmount) { this.unpaidAmount = unpaidAmount; }

    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }

    public Integer getCurrentPeriod() { return currentPeriod; }
    public void setCurrentPeriod(Integer currentPeriod) { this.currentPeriod = currentPeriod; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getActivatedAt() { return activatedAt; }
    public void setActivatedAt(Date activatedAt) { this.activatedAt = activatedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
