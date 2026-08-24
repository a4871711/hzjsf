package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/** “每 X 天最多上 Y 节”的固定周期边界测试。 */
public class PrivateAppointmentLessonLimitPeriodTest {

    @Test
    public void shouldUseEffectiveDateAsFirstPeriodStart() {
        assertEquals(LocalDate.of(2026, 8, 10), periodStart(LocalDate.of(2026, 8, 10)));
        assertEquals(LocalDate.of(2026, 8, 10), periodStart(LocalDate.of(2026, 9, 8)));
    }

    @Test
    public void shouldStartNextPeriodOnExclusiveBoundary() {
        assertEquals(LocalDate.of(2026, 9, 9), periodStart(LocalDate.of(2026, 9, 9)));
        assertEquals(LocalDate.of(2026, 9, 9), periodStart(LocalDate.of(2026, 10, 8)));
        assertEquals(LocalDate.of(2026, 10, 9), periodStart(LocalDate.of(2026, 10, 9)));
    }

    @Test
    public void shouldRejectAppointmentBeforeBenefitEffectiveDate() {
        try {
            periodStart(LocalDate.of(2026, 8, 9));
            fail("早于权益生效日的预约必须明确报错");
        } catch (RRException e) {
            assertEquals("预约日期不能早于权益生效日期", e.getMessage());
        }
    }

    private LocalDate periodStart(LocalDate targetDate) {
        return PrivateAppointmentServiceImpl.lessonLimitPeriodStart(
                Timestamp.valueOf("2026-08-10 10:00:00"), targetDate, 30);
    }
}
