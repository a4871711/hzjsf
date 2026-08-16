package com.dlc.modules.api.service;

import java.util.Map;

/** 教练端“我的”业务。 */
public interface PrivateCoachCenterService {

    Map<String, Object> mine(Long userId);

    void updateProfile(Long userId, String coachName, String mobile, Integer gender,
                       String avatarUrl, String intro);

    Map<String, Object> incomeList(Long userId, Integer page, Integer limit, String type);
}
