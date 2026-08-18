package com.dlc.modules.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 包月课程教练提成计算器。
 *
 * <p>计算以单名教练在当前订单下的累计完课数为口径，调用方需要先完成订单行锁和完课数查询。</p>
 */
public final class MonthlyCommissionCalculator {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int CALCULATION_SCALE = 8;
    private static final int MONEY_SCALE = 2;

    private MonthlyCommissionCalculator() {
    }

    /**
     * 计算当前教练本次核销应得提成。
     *
     * @param netAmount             订单净实收（实付 - 累计退款）
     * @param orderLessonCount      订单总课时
     * @param currentLessons        本次教练实际授课课时
     * @param totalFinishedLessons  当前教练在该订单下累计完课课时
     * @param standardLessonCount   达到比例提成的标准课节数
     * @param commissionRate        达标后的提成比例
     * @param belowStandardFee      未达标时的固定单节提成
     */
    public static Result calculate(BigDecimal netAmount,
                                   int orderLessonCount,
                                   int currentLessons,
                                   int totalFinishedLessons,
                                   int standardLessonCount,
                                   BigDecimal commissionRate,
                                   BigDecimal belowStandardFee) {
        if (currentLessons <= 0 || totalFinishedLessons < 0 || standardLessonCount <= 0) {
            throw new IllegalArgumentException("包月提成课时参数非法");
        }
        if (commissionRate == null || belowStandardFee == null) {
            throw new IllegalArgumentException("包月提成配置不能为空");
        }

        int finishedBefore = Math.max(0, totalFinishedLessons - currentLessons);
        BigDecimal safeNetAmount = netAmount == null
                ? BigDecimal.ZERO : netAmount.max(BigDecimal.ZERO);
        BigDecimal commission;
        BigDecimal originalAmount;
        Double percent = null;

        if (totalFinishedLessons < standardLessonCount) {
            commission = belowStandardFee.multiply(BigDecimal.valueOf(currentLessons));
            originalAmount = commission;
        } else {
            if (orderLessonCount <= 0) {
                return new Result(BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP),
                        commissionRate.doubleValue());
            }
            BigDecimal coursePerLessonAmount = safeNetAmount.divide(
                    BigDecimal.valueOf(orderLessonCount), CALCULATION_SCALE, RoundingMode.HALF_UP);
            BigDecimal percentagePerLessonCommission = coursePerLessonAmount
                    .multiply(commissionRate)
                    .divide(HUNDRED, CALCULATION_SCALE, RoundingMode.HALF_UP);
            BigDecimal cumulativeTarget = percentagePerLessonCommission
                    .multiply(BigDecimal.valueOf(totalFinishedLessons));
            BigDecimal previousCommission = finishedBefore < standardLessonCount
                    ? belowStandardFee.multiply(BigDecimal.valueOf(finishedBefore))
                    : percentagePerLessonCommission.multiply(BigDecimal.valueOf(finishedBefore));

            // 固定单节提成可能已经高于比例累计目标，不能因为补差反向扣减教练历史提成。
            commission = cumulativeTarget.subtract(previousCommission).max(BigDecimal.ZERO);
            originalAmount = coursePerLessonAmount.multiply(BigDecimal.valueOf(currentLessons));
            percent = commissionRate.doubleValue();
        }
        return new Result(commission.setScale(MONEY_SCALE, RoundingMode.HALF_UP),
                originalAmount.setScale(MONEY_SCALE, RoundingMode.HALF_UP), percent);
    }

    public static final class Result {
        private final BigDecimal commission;
        private final BigDecimal originalAmount;
        private final Double percent;

        private Result(BigDecimal commission, BigDecimal originalAmount, Double percent) {
            this.commission = commission;
            this.originalAmount = originalAmount;
            this.percent = percent;
        }

        public BigDecimal getCommission() {
            return commission;
        }

        public BigDecimal getOriginalAmount() {
            return originalAmount;
        }

        public Double getPercent() {
            return percent;
        }
    }
}
