package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysAboutEntity;

import java.util.List;
import java.util.Map;

public interface SysAboutService {
    SysAboutEntity queryObject(Long id);

    List<SysAboutEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysAboutEntity entity);

    void update(SysAboutEntity entity);

    // void updates(FitCardEntity fitCardEntity);

    void delete(Long id);

    void deleteBatch(Long[] ids);
}
