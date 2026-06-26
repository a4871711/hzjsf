package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysGoodsCategoryEntity;

import java.util.List;
import java.util.Map;

public interface SysGoodsCategoryService {
	
	SysGoodsCategoryEntity queryObject(Long id);
	
	List<SysGoodsCategoryEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SysGoodsCategoryEntity goodsCategory);
	
	void update(SysGoodsCategoryEntity goodsCategory);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<SysGoodsCategoryEntity> queryGoodsCategoryList();
}
