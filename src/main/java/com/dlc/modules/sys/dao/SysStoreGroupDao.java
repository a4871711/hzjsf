package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysStoreGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * lingkangming
 */
@Mapper
@Repository
public interface SysStoreGroupDao extends BaseDao<SysStoreGroupEntity> {

    int queryStoreById(Long storeId);

    SysStoreGroupEntity queryStoreAddressById(Long storeId);

    int queryDyStoreNum(Long storeId);

    int queryAttionCounts(Long storeId);
}
