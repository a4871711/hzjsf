package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysStoreAddressDao;
import com.dlc.modules.sys.entity.SysStoreAddressEntity;
import com.dlc.modules.sys.service.SysStoreAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysStoreAddressService")
public class SysStoreAddressServiceImpl implements SysStoreAddressService {
	@Autowired
	private SysStoreAddressDao sysStoreAddressDao;

	@Override
	public SysStoreAddressEntity queryObject(Long id) {
		return sysStoreAddressDao.queryObject(id);
	}

	@Override
	public List<SysStoreAddressEntity> queryList(Map<String, Object> map) {
		return sysStoreAddressDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysStoreAddressDao.queryTotal(map);
	}

	@Override
	public int save(SysStoreAddressEntity storeAddress) {
		return sysStoreAddressDao.save(storeAddress);
	}

	@Override
	public int update(SysStoreAddressEntity storeAddress) {
		return sysStoreAddressDao.update(storeAddress);
	}

	@Override
	public void delete(Long id) {
		sysStoreAddressDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysStoreAddressDao.deleteBatch(ids);
	}

	@Override
	public int queryStoreById(Long storeId) {
		return sysStoreAddressDao.queryStoreById(storeId);
	}

	@Override
	public int updateStoreNameAndPhone(SysStoreAddressEntity sysStoreAddressEntity) {
		return sysStoreAddressDao.updateStoreNameAndPhone(sysStoreAddressEntity);
	}
}
