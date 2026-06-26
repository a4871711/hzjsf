package com.dlc.modules.sys.dao;

import org.springframework.stereotype.Repository;

import com.dlc.modules.sys.entity.SysDeviceChangeEntity;

/**
 * 我的设备
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-13 10:16:12
 */
@Repository
public interface SysDeviceChangeDao extends BaseDao<SysDeviceChangeEntity> {
    int insertDeviceSelective(SysDeviceChangeEntity sysDeviceChangeEntity);

}
