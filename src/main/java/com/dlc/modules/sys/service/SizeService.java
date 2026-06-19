package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SizeEntity;

import java.util.List;
import java.util.Map;

public interface SizeService {
	
	SizeEntity queryObject(Long id);
	
	List<SizeEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SizeEntity sizeEntity);
	
	void update(SizeEntity sizeEntity);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<SizeEntity> selectSizeDetail();

	/**
	 * 查询是否已经存在该尺寸
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getExistsSize(Map<String, Object> map);
}
