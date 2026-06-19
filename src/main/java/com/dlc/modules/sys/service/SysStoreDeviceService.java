package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysStoreDeviceEntity;

import java.util.List;
import java.util.Map;

/**
 * 门店设备表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-07 10:46:29
 */
public interface SysStoreDeviceService {

    SysStoreDeviceEntity queryObject(Long id);

    List<SysStoreDeviceEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysStoreDeviceEntity sysStoreDeviceEntity);

    void update(SysStoreDeviceEntity sysStoreDeviceEntity);

    void deleteBatch(Long[] ids);

    int queryExistSameDevice(String deviceNo);

    int querySameDevice(SysStoreDeviceEntity StoreDeviceEntity);
}

