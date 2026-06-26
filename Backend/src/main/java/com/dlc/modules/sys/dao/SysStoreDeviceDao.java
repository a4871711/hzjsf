package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysStoreDeviceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 门店设备表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-07 10:46:29
 */
@Mapper
@Repository
public interface SysStoreDeviceDao extends BaseDao<SysStoreDeviceEntity> {

    int queryExistSameDevice(String deviceNo);

    int querySameDevice(SysStoreDeviceEntity storeDeviceEntity);
}
