package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysInformAtionEntity;

import java.util.List;
import java.util.Map;

public interface SysInformAtionService {
    SysInformAtionEntity queryObject(Long informationId);

    List<SysInformAtionEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysInformAtionEntity informationId);

    void update(SysInformAtionEntity informationId);

    // void updates(FitCardEntity fitCardEntity);

    void delete(Long informationId);

    void deleteBatch(Long[] informationIds);
}
