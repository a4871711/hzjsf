package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysStoreAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * lingkangming
 */
public interface SysStoreAddressService {
	
	SysStoreAddressEntity queryObject(Long id);
	
	List<SysStoreAddressEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	int save(SysStoreAddressEntity storeAddress);
	
	int update(SysStoreAddressEntity storeAddress);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	int queryStoreById(Long storeId);

	int updateStoreNameAndPhone(SysStoreAddressEntity sysStoreAddressEntity);

}
