package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员储值流水(对应表 pt_member_wallet_flow,第19步资金域),只增不改的不可变审计流水。
 * 恒等式:after_balance = before_balance + change_amount,写入即校验,任何路径不可破。
 * out_order_no 唯一键 = 充值/消费/退款的回调与重复提交幂等闸(NULL 不参与唯一约束)。
 *
 * @author claude
 */
public class PtMemberWalletFlowEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 储值账户ID */
    private Long walletId;
    /** 冗余会员ID,免联表直查流水 */
    private Long memberId;
    /** 流水类型:1充值 2消费 3退款 4冲正 */
    private Integer flowType;
    /** 变动金额,带符号:充值/退款为正、消费为负、冲正按方向 */
    private BigDecimal changeAmount;
    /** 变动前余额(写流水那一刻账户余额快照) */
    private BigDecimal beforeBalance;
    /** 变动后余额,强制 = before + change */
    private BigDecimal afterBalance;
    /** 关联业务类型:1订单 2退款单 3人工调整(会员微信自助充值为 NULL) */
    private Integer bizType;
    /** 关联业务ID(私教订单ID等) */
    private Long bizId;
    /** 备注(后台人工充值/冲正必填来源说明) */
    private String remark;
    /** 操作人(后台充值/冲正记管理员ID;会员自助充值/消费为 NULL) */
    private Long createdBy;
    private Date createdAt;
    /** 外部业务单号(充值单号/订单号/退款号),唯一键做回调幂等 */
    private String outOrderNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Integer getFlowType() { return flowType; }
    public void setFlowType(Integer flowType) { this.flowType = flowType; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public BigDecimal getBeforeBalance() { return beforeBalance; }
    public void setBeforeBalance(BigDecimal beforeBalance) { this.beforeBalance = beforeBalance; }

    public BigDecimal getAfterBalance() { return afterBalance; }
    public void setAfterBalance(BigDecimal afterBalance) { this.afterBalance = afterBalance; }

    public Integer getBizType() { return bizType; }
    public void setBizType(Integer bizType) { this.bizType = bizType; }

    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getOutOrderNo() { return outOrderNo; }
    public void setOutOrderNo(String outOrderNo) { this.outOrderNo = outOrderNo; }
}
