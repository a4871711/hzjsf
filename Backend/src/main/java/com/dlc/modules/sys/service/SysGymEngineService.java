package com.dlc.modules.sys.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GymType;
import com.dlc.modules.sys.entity.GymEngineEntity;

import java.util.List;
import java.util.Map;

/**
 * 机械表
 *
 * @author daibenting
 * @email
 * @date 2018-09-11 09:17:04
 */
public interface SysGymEngineService {
    GymEngineEntity queryObject(Long gymEngineId);

    List<GymEngineEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(GymEngineEntity gymEngineEntity);

    void update(GymEngineEntity gymEngineEntity);

    void deleteBatch(Long[] gymEngineIds);

    List<GymType> queryGymTypeList(Query query);

    int queryGymTypeTotal(Query query);

    int deleteGymType(Long gtId);

    List<GymType> getGymTypeList();

    int saveGymType(GymType gymType);

    int updateGymType(GymType gymType);

}

