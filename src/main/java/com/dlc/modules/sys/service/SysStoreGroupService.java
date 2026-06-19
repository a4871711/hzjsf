package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysStoreGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * lingkangming
 */
public interface SysStoreGroupService {
	
	SysStoreGroupEntity queryObject(Long id);
	
	List<SysStoreGroupEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SysStoreGroupEntity storeGroup);
	
	void update(SysStoreGroupEntity storeGroup);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	int queryStoreById(Long storeId);

	SysStoreGroupEntity queryStoreAddressById(Long storeId);

    int queryDyStoreById(Long id);

	int queryAttionCount(Long ids);

	int deleteFlagNum(Long ids);
}
