package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
public interface CoachEvaluateService {
    int countCoachEva(Long coachId);

    List<Map<String,Object>> coachEvaList(Query query);

    int queryTotal(Query query);

    List<Map<String,Object>> storeCoachEvaList(Query query);

    int queryStoreCoachTotal(Query query);

    int countStoreCoachEva(Long coachId);
}
