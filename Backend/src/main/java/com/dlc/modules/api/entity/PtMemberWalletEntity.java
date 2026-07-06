package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员储值账户(对应表 pt_member_wallet,第19步资金域),一会员一行(uk_member_id)。
 * 私教专用余额主账户,与旧提现/充值明细 SysWalletDetailEntity(type=1/2/3)并存不混账(风险8)。
 * 余额红线:任何变动必须走 PtMemberWalletService.changeBalance(行锁内 after=before+change),
 * 禁止任何"先查后改"的无锁路径直接 UPDATE balance_amount。
 *
 * @author claude
 */
public class PtMemberWalletEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 会员ID(= user_info.userId),唯一键 */
    private Long memberId;
    /** 当前可用余额,变动一律在行锁内 余额=余额+变动 */
    private BigDecimal balanceAmount;
    /** 累计充值(仅 flow_type=1 累加) */
    private BigDecimal totalRechargeAmount;
    /** 累计消费(仅 flow_type=2 累加;退款不冲减,口径=历史发生额) */
    private BigDecimal totalConsumeAmount;
    /** 状态:1正常 2冻结(冻结后拒绝一切余额变动,仅可解冻) */
    private Integer status;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public BigDecimal getTotalRechargeAmount() { return totalRechargeAmount; }
    public void setTotalRechargeAmount(BigDecimal totalRechargeAmount) { this.totalRechargeAmount = totalRechargeAmount; }

    public BigDecimal getTotalConsumeAmount() { return totalConsumeAmount; }
    public void setTotalConsumeAmount(BigDecimal totalConsumeAmount) { this.totalConsumeAmount = totalConsumeAmount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
