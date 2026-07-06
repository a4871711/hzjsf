package com.dlc.modules.sys.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 私教收入报表 · 明细行 VO（一行=一次已完成核销，第23步 §19）。
 * unitIncome = 净实收(paid-refund) ÷ 订单总课时 × 本次完成课时（按 appointment 行先算，不先汇总）。
 * unitCost = 命中课时费(缺配0) × 本次完成课时；unitGross = unitIncome - unitCost。
 *
 * @author claude
 */
public class PrivateReportDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 完成日期 DATE(finish_at) */
    private String finishDate;
    /** 门店名 */
    private String storeName;
    /** 教练姓名 */
    private String coachName;
    /** 课程名（订单商品名快照） */
    private String productName;
    /** 用户信息（会员姓名，空则手机号） */
    private String memberInfo;
    /** 订单号 */
    private String orderNo;
    /** 订单总课时 */
    private Integer totalLessons;
    /** 本次完成课时 */
    private Integer finishLessons;
    /** 单次收入 */
    private BigDecimal unitIncome;
    /** 单次成本 */
    private BigDecimal unitCost;
    /** 单次毛利 */
    private BigDecimal unitGross;

    public String getFinishDate() { return finishDate; }
    public void setFinishDate(String finishDate) { this.finishDate = finishDate; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getMemberInfo() { return memberInfo; }
    public void setMemberInfo(String memberInfo) { this.memberInfo = memberInfo; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Integer getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }

    public Integer getFinishLessons() { return finishLessons; }
    public void setFinishLessons(Integer finishLessons) { this.finishLessons = finishLessons; }

    public BigDecimal getUnitIncome() { return unitIncome; }
    public void setUnitIncome(BigDecimal unitIncome) { this.unitIncome = unitIncome; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getUnitGross() { return unitGross; }
    public void setUnitGross(BigDecimal unitGross) { this.unitGross = unitGross; }
}
