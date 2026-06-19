package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.entity.SysPrivateClassEntity;

import java.util.List;
import java.util.Map;

public interface SysPrivateClassService {
    SysPrivateClassEntity queryObject(Long privateClassId);

    List<SysPrivateClassEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysPrivateClassEntity sysPrivateClassEntity);

    void update(SysPrivateClassEntity sysPrivateClassEntity);

    // void updates(FitCardEntity fitCardEntity);

    void delete(Long privateClassId);

    int deleteBatch(Long[] privateClassIds);
    SysPrivateClassEntity queryLast(SysPrivateClassEntity entity);

    List<SysDataMapEntity> queryClassType();
}
