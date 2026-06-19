package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysStoreAddressEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * lingkangming
 */
@Mapper
@Repository
public interface SysStoreAddressDao extends BaseDao<SysStoreAddressEntity> {
    int queryStoreById(Long storeId);

    int updateStoreNameAndPhone(SysStoreAddressEntity sysStoreAddressEntity);
}
