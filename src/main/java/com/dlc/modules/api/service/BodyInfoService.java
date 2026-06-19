package com.dlc.modules.api.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-10-27 10:47
 */
public interface BodyInfoService {
    int saveOrUpdateBodyInfo(JSONObject bodyInfoMap);

    Map<String,Object> queryBodyInfoByUserId(Long userId);

    int saveOrUpdateBodyDimension(JSONObject bodyDimensionMap);

    int saveOrUpdateBodyStatus(JSONObject bodyStatusMap);

    int saveOrUpdateBodyShape(JSONObject bodyShapeMap);

    List<Map<String,Object>> queryBodyDimensionInfo(Long userId);

    Map<String,Object> queryBodyDimensionScanId(Long userId);

    List<Map<String,Object>> queryBodyInfoBetweenTime(Map<String, Object> paramMap);

    List<Map<String,Object>> queryBodyBodyStatusByUserId(Long userId);

    List<Map<String,Object>> queryBodyShapeLastRecord(Long userId);

    JSONObject bodyinfoFormatData(JSONObject bodyInfoMap);

    JSONObject bodyDimensFormatData(JSONObject bodyDimensionMap);

    JSONObject bodyStatusFormatData(JSONObject bodyStatusMap);

    JSONObject bodyShapeFormatData(JSONObject bodyShapeMap);

}
