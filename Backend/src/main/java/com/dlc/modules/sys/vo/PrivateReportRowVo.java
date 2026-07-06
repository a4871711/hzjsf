package com.dlc.modules.sys.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 私教收入报表 · 分组行 VO（门店/教练/课程报表通用，第23步 §19）。
 * groupName 按分组维度落值：门店报表=门店名，教练报表=教练姓名，课程报表=课程名。
 * coachStoreName 仅教练报表用（教练所属门店，可空）。
 *
 * @author claude
 */
public class PrivateReportRowVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分组名称：门店名/教练姓名/课程名 */
    private String groupName;
    /** 教练所属门店（仅教练报表填充） */
    private String coachStoreName;
    /** 完成课时（本次完成课时合计） */
    private Long finishLessons;
    /** 收入 */
    private BigDecimal income;
    /** 成本 */
    private BigDecimal cost;
    /** 毛利 = income - cost */
    private BigDecimal grossProfit;
    /** 毛利率(%)，income=0 显示 0 */
    private BigDecimal grossRate;

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getCoachStoreName() { return coachStoreName; }
    public void setCoachStoreName(String coachStoreName) { this.coachStoreName = coachStoreName; }

    public Long getFinishLessons() { return finishLessons; }
    public void setFinishLessons(Long finishLessons) { this.finishLessons = finishLessons; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public BigDecimal getGrossRate() { return grossRate; }
    public void setGrossRate(BigDecimal grossRate) { this.grossRate = grossRate; }
}
