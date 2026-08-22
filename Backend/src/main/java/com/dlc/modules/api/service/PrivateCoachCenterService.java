package com.dlc.modules.api.service;

import java.util.Map;
import java.math.BigDecimal;

/** 教练端“我的”业务。 */
public interface PrivateCoachCenterService {

    Map<String, Object> mine(Long userId);

    Map<String, Object> members(Long userId, Integer page, Integer limit, String keyword);

    void updateProfile(Long userId, String coachName, String mobile, Integer gender,
                       String avatarUrl, String intro);

    Map<String, Object> incomeList(Long userId, Integer page, Integer limit,
                                   String type, String month);

    Map<String, Object> withdrawalList(Long userId, Integer page, Integer limit, String month);

    void applyWithdrawal(Long userId, BigDecimal amount, String accountName,
                         String bankName, String bankCardNo);
}
