package com.dlc.modules.sys.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.ActivityType;
import com.dlc.modules.sys.entity.SysActivityTrainEntity;

import java.util.List;
import java.util.Map;

/**
 * 动作训练
 */
public interface SysActivityTrainService {
    SysActivityTrainEntity queryObject(Long activityTrainId);

    List<SysActivityTrainEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysActivityTrainEntity activityTrainEntity);

    void update(SysActivityTrainEntity activityTrainEntity);

    // void updates(FitCardEntity fitCardEntity);

    void delete(Long activityTrainId);

    void deleteBatch(Long[] activityTrainIds);

    List<ActivityType> queryActivList(Query query);

    int queryActivTotal(Query query);

    List<ActivityType> selectAllActivType();

    int deleteActiveType(Long atId);

    int saveActivityType(ActivityType activityType);

    int updateActivityType(ActivityType activityType);
}
