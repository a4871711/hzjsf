package com.dlc.modules.api.service.impl;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** 月卡多教练提成口径回归测试。 */
public class MonthlyCommissionCalculatorTest {

    @Test
    public void shouldUseFixedFeeBeforeCoachReachesStandardLessons() {
        MonthlyCommissionCalculator.Result result = MonthlyCommissionCalculator.calculate(
                new BigDecimal("3000"), 20, 1, 3, 10,
                new BigDecimal("20"), new BigDecimal("80"));

        assertEquals(new BigDecimal("80.00"), result.getCommission());
        assertEquals(new BigDecimal("80.00"), result.getOriginalAmount());
        assertNull(result.getPercent());
    }

    @Test
    public void shouldCatchUpAtTheLessonThatReachesStandard() {
        MonthlyCommissionCalculator.Result result = MonthlyCommissionCalculator.calculate(
                new BigDecimal("3000"), 20, 1, 10, 10,
                new BigDecimal("20"), new BigDecimal("20"));

        // 每节目标提成=3000/20*20%=30，前9节已按20元结算，达标当次补差120元。
        assertEquals(new BigDecimal("120.00"), result.getCommission());
        assertEquals(new BigDecimal("150.00"), result.getOriginalAmount());
        assertEquals(Double.valueOf(20D), result.getPercent());
    }

    @Test
    public void shouldUsePercentageAfterStandardLessons() {
        MonthlyCommissionCalculator.Result result = MonthlyCommissionCalculator.calculate(
                new BigDecimal("3000"), 20, 1, 11, 10,
                new BigDecimal("20"), new BigDecimal("20"));

        assertEquals(new BigDecimal("30.00"), result.getCommission());
        assertEquals(new BigDecimal("150.00"), result.getOriginalAmount());
        assertEquals(Double.valueOf(20D), result.getPercent());
    }

    @Test
    public void shouldNotCreateNegativeCommissionWhenFixedFeeExceedsTarget() {
        MonthlyCommissionCalculator.Result result = MonthlyCommissionCalculator.calculate(
                new BigDecimal("3000"), 20, 1, 10, 10,
                new BigDecimal("10"), new BigDecimal("50"));

        assertEquals(new BigDecimal("0.00"), result.getCommission());
        assertEquals(new BigDecimal("150.00"), result.getOriginalAmount());
    }
}
