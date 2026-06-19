package com.dlc.modules.sys.service;


import com.dlc.modules.sys.entity.SysDeviceEntity;

import java.util.List;
import java.util.Map;

/**
 * 我的设备
 *
 * @author wangsheng
 * @email 
 * @date 2018-09-13 10:16:12
 */
public interface SysDeviceService{
    SysDeviceEntity queryObject(Long deviceId);

    List<SysDeviceEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysDeviceEntity sysDeviceEntity);

    int update(SysDeviceEntity sysDeviceEntity);

    void deleteBatch(Long[] deviceIds);

    int batchForbidden(Long[] deviceIds);

    int batchUnForbidden(Long[] deviceIds);

    int queryIfExistDeciceNo(SysDeviceEntity device);

    int updateDeviceInit(String deviceNo);
}

