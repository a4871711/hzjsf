package com.dlc.modules.api.service;


import com.dlc.common.utils.Query;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/10/010
 */
public interface ActivityTrainService {
    /**
     *  @Auther:YD
     *  @parameters:
     *  动作训练列表
     */
    List<Map<String,Object>> queryActivityTrainList(Map<String,Object> params);

    int queryTotal(Map<String,Object> params);
    /**
     *  @Auther:YD
     *  @parameters:
     *  动作训练详情
     */
    Map<String,Object> queryActivityTrainInfo(Long id);

    List<Map<String,Object>> querySecondActivityTrainList(Query query);

    int querySecondActivityTrainTotal(Query query);
}
