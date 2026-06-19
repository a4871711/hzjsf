package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.FitCardEntity;
import com.dlc.modules.sys.entity.SysAdvertisingEntity;

import java.util.List;
import java.util.Map;

/**
 * 广告表
 *
 * @author wangsheng
 * @email 
 * @date 2018-09-12 16:11:35
 */
public interface SysAdvertisingService  {
    SysAdvertisingEntity queryObject(Long advId);

    List<SysAdvertisingEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysAdvertisingEntity sysAdvertisingEntity);

    void update(SysAdvertisingEntity sysAdvertisingEntity);

    void deleteBatch(Long[] advIds);

    List<Map<String, Object>> queryGoodsList();
}

