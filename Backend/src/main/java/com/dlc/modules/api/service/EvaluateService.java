package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.vo.UserInfoVo;

import java.util.Map;

public interface EvaluateService {
    int saveEvaluatePc(UserInfoVo user, Map<String, Object> params);

    //int saveAnswerDynamic(UserInfoVo user, DynamicEvaluate dynamicEvaluate);

    R saveDz(Long userId, Long dynamicId);

    R cancelDz(Long userId, Long dynamicId);

    int saveAnswerDynamic(UserInfoVo user, Long dynamicId, String evContent);

    int saveStoreCoachEvaluate(UserInfoVo user, Map<String, Object> params);

    int ifHaveCard(Long userId);
}
