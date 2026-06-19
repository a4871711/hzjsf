package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysStoreGroupDao;
import com.dlc.modules.sys.entity.SysStoreGroupEntity;
import com.dlc.modules.sys.service.SysStoreGroupService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * lingkangming
 */
@Service("sysStoreGroupService")
public class SysStoreGroupServiceImpl implements SysStoreGroupService {
	@Autowired
	private SysStoreGroupDao sysStoreGroupDao;

	@Override
	public SysStoreGroupEntity queryObject(Long id) {
		return sysStoreGroupDao.queryObject(id);
	}

	@Override
	public List<SysStoreGroupEntity> queryList(Map<String, Object> map) {
		//Long sId = ShiroUtils.getUserEntity().getStoreId();
		//if(null != sId){
			map.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
		//}
		return sysStoreGroupDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysStoreGroupDao.queryTotal(map);
	}

	@Override
	public void save(SysStoreGroupEntity storeGroup) {
		sysStoreGroupDao.save(storeGroup);
	}

	@Override
	public void update(SysStoreGroupEntity storeGroup) {
		sysStoreGroupDao.update(storeGroup);
	}

	@Override
	public void delete(Long id) {
		sysStoreGroupDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysStoreGroupDao.deleteBatch(ids);
	}

	@Override
	public int queryStoreById(Long storeId) {
		return sysStoreGroupDao.queryStoreById(storeId);
	}

	@Override
	public SysStoreGroupEntity queryStoreAddressById(Long storeId) {
		return sysStoreGroupDao.queryStoreAddressById(storeId);
	}

	@Override
	public int queryDyStoreById(Long id) {
		return sysStoreGroupDao.queryDyStoreNum(id);
	}

	@Override
	public int queryAttionCount(Long ids) {
		return sysStoreGroupDao.queryAttionCounts(ids);
	}

	@Override
	public int deleteFlagNum(Long ids) {
		return 0;
	}
}
