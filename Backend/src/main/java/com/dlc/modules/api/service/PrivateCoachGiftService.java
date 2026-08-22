package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/** 自由教练赠课服务。 */
public interface PrivateCoachGiftService {

    List<Map<String, Object>> giftableBenefits(Long userId);

    Map<String, Object> lookupMember(Long userId, String keyword);

    Map<String, Object> gift(Long userId, Long sourceBenefitId, Long toMemberId,
                             Integer lessonCount, String requestNo);

    PageUtils history(Long userId, Map<String, Object> params);
}
