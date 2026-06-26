package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysGoodsCategoryDetailEntity;

import java.util.List;
import java.util.Map;

public interface SysGoodsCategoryDetailService {
	
	SysGoodsCategoryDetailEntity queryObject(Long id);
	
	List<SysGoodsCategoryDetailEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SysGoodsCategoryDetailEntity goodsCategoryDetail);
	
	void update(SysGoodsCategoryDetailEntity goodsCategoryDetail);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<SysGoodsCategoryDetailEntity> queryGoodsCategoryDetailList();
}
