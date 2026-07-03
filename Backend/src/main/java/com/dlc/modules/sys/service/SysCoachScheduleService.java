package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtCoachScheduleEntity;

import java.util.List;
import java.util.Map;

/**
 * 教练固定周排班 Service
 *
 * @author claude
 */
public interface SysCoachScheduleService {

    PtCoachScheduleEntity queryObject(Long id);

    List<PtCoachScheduleEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    /** 按 星期×门店 笛卡尔展开批量保存，带重叠/归属校验 */
    void save(PtCoachScheduleEntity entity);

    void update(PtCoachScheduleEntity entity);

    void deleteBatch(Long[] ids);

    void changeEnabled(Long id, Integer isEnabled);
}
