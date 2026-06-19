package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysDataMapEntity;

import java.util.List;
import java.util.Map;

public interface SysDataMapService {
    //查询所有
    List<SysDataMapEntity> queryList(Map<String, Object> map);
    SysDataMapEntity queryObject(Long dataMapId);
    //新增
    int save(SysDataMapEntity dataMapEntity);
    //修改
    int update(SysDataMapEntity dataMapEntity);

    //总数
    int queryTotal(Map<String, Object> map);
    void delete(Long datamapId);

    void deleteBatch(Long[] datamapIds);

}
