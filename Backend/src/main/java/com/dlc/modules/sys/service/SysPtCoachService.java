package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtCoachEntity;

import java.util.List;
import java.util.Map;

/**
 * 私教教练 Service（pt_coach，与旧 SysCoachService 并存不复用）
 *
 * @author claude
 */
public interface SysPtCoachService {

    PtCoachEntity queryObject(Long id);

    List<PtCoachEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtCoachEntity entity);

    void update(PtCoachEntity entity);

    void deleteBatch(Long[] ids);

    void changeStatus(Long id, Integer status, String disableReason);
}
