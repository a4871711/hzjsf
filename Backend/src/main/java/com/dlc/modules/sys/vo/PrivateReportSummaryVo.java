package com.dlc.modules.sys.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 私教收入报表 · 顶部汇总卡片 VO（第23步 §19）。
 * 口径：按已完成预约 finish_at 落统计日；单次收入=净实收÷订单总课时×本次完成课时（按行先算再 SUM）；
 * 成本命中 pt_coach_fee_rule(rule_type=1，先(coach,product)后(coach,0)) 缺配=0；毛利率 income=0 显示 0。
 *
 * @author claude
 */
public class PrivateReportSummaryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 核销课时（本次完成课时合计） */
    private Long totalLessons;
    /** 收入金额（净实收按行折算后 SUM） */
    private BigDecimal income;
    /** 课时成本（命中课时费×本次完成课时后 SUM，缺配按0） */
    private BigDecimal cost;
    /** 毛利 = income - cost */
    private BigDecimal grossProfit;
    /** 毛利率(%) = grossProfit/income*100，income=0 显示 0 */
    private BigDecimal grossRate;

    public Long getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Long totalLessons) { this.totalLessons = totalLessons; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public BigDecimal getGrossRate() { return grossRate; }
    public void setGrossRate(BigDecimal grossRate) { this.grossRate = grossRate; }
}
