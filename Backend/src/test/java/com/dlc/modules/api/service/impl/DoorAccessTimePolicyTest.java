package com.dlc.modules.api.service.impl;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoorAccessTimePolicyTest {

    private final LocalDateTime classStart = LocalDateTime.of(2026, 8, 20, 14, 0, 0);

    @Test
    public void shouldUseInclusiveStartAndExclusiveEnd() {
        assertFalse(DoorAccessTimePolicy.isAvailable(LocalDateTime.of(2026, 8, 20, 13, 29, 59), classStart));
        assertTrue(DoorAccessTimePolicy.isAvailable(LocalDateTime.of(2026, 8, 20, 13, 30, 0), classStart));
        assertTrue(DoorAccessTimePolicy.isAvailable(LocalDateTime.of(2026, 8, 20, 15, 59, 59), classStart));
        assertFalse(DoorAccessTimePolicy.isAvailable(LocalDateTime.of(2026, 8, 20, 16, 0, 0), classStart));
    }
}
