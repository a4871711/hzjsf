package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.CoachClassShip;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-18 20:10
 */
public interface CoachClassShipService {
    List<Map<String,Object>> queryCoachClass(Map<String, Object> map);

    int queryCoachClassRecordTotal(Query query);

    int updateCoachClassShio(CoachClassShip coachClassShip);

    int addCoachClassShip(List<CoachClassShip> coachClassList);

    Boolean isExistClass(CoachClassShip coachClassShip);

    List<CoachClassShip> queryAllCoachClass(Long coachId);
}
