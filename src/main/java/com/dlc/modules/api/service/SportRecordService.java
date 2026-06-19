package com.dlc.modules.api.service;


import com.alibaba.fastjson.JSONObject;
import com.dlc.modules.api.entity.SportActive;

import java.util.List;
import java.util.Map;

public interface SportRecordService {
    /*Integer queryTodaySportStep(Long userId);

    Integer queryActivity(Long userId);

    SportRecord classCount(Long userId);*/

    Map<String,Object>  querySportRecord(Long userId);

    Map<String,Object> querySportListByType(Integer sportType, Long userId);

    JSONObject  sportDataCount(SportActive sportActive);

    int saveSportData(Map<String, Object> map);

    List<Map<String,Object>> queryEnergy();

    Long getSysFlag();
}
