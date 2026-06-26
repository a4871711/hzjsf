package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/10/010
 */
public interface GymEngineService {
    /**
     *  @Auther:YD
     *  @parameters:
     *  查询器械列表
     */
    List<Map<String,Object>> queryGymEngineList(Map<String,Object> params);

    int queryTotal(Map<String,Object> params);
    /**
     *  @Auther:YD
     *  @parameters:
     *  器械详情查询
     */
    Map<String,Object> queryGymEngineInfo(Long id);

    List<Map<String,Object>> querySecondGymEngineList(Query query);

    int querySecondTotal(Query query);
}
