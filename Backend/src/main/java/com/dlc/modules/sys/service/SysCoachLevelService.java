package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtCoachLevelEntity;

import java.util.List;
import java.util.Map;

/**
 * 私教教练等级 Service
 *
 * @author claude
 */
public interface SysCoachLevelService {

    PtCoachLevelEntity queryObject(Long id);

    List<PtCoachLevelEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtCoachLevelEntity entity);

    void update(PtCoachLevelEntity entity);

    void deleteBatch(Long[] ids);

    void changeStatus(Long id, Integer status);

    List<PtCoachLevelEntity> queryOptions();
}
