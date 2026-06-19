package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.ColorEntity;

import java.util.List;
import java.util.Map;

public interface ColorService {
	
	ColorEntity queryObject(Long id);
	
	List<ColorEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(ColorEntity color);
	
	void update(ColorEntity color);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<ColorEntity> selectColorDetail();

	/**
	 * 查询是否已经存在该颜色
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getExistsColor(Map<String, Object> map);
}
