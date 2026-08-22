package com.dlc.modules.api.service.impl;

import java.time.LocalDateTime;

/** 预约开门时间规则：开始前30分钟开放，开始后120分钟失效，结束边界不包含。 */
public final class DoorAccessTimePolicy {

    public static final int ADVANCE_MINUTES = 30;
    public static final int AFTER_START_MINUTES = 120;

    private DoorAccessTimePolicy() {
    }

    public static LocalDateTime accessStart(LocalDateTime classStart) {
        return classStart.minusMinutes(ADVANCE_MINUTES);
    }

    public static LocalDateTime accessEnd(LocalDateTime classStart) {
        return classStart.plusMinutes(AFTER_START_MINUTES);
    }

    public static boolean isAvailable(LocalDateTime now, LocalDateTime classStart) {
        return !now.isBefore(accessStart(classStart)) && now.isBefore(accessEnd(classStart));
    }
}
