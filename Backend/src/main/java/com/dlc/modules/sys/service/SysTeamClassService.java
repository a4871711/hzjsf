package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;

import java.util.List;
import java.util.Map;

/**
 *	lingkangming
 */
public interface SysTeamClassService {
	
	TeamClassEntity queryObject(Long id);
	
	List<TeamClassEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(TeamClassEntity teamClass);
	
	void update(TeamClassEntity teamClass);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<SysStoreEntity> selectStoreName(String storeIds);
}
