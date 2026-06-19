package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysGoodsCategoryDao;
import com.dlc.modules.sys.entity.SysGoodsCategoryEntity;
import com.dlc.modules.sys.service.SysGoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysGoodsCategoryService")
public class SysGoodsCategoryServiceImpl implements SysGoodsCategoryService {
	@Autowired
	private SysGoodsCategoryDao sysGoodsCategoryDao;


	@Override
	public SysGoodsCategoryEntity queryObject(Long id) {
		return sysGoodsCategoryDao.queryObject(id);
	}

	@Override
	public List<SysGoodsCategoryEntity> queryList(Map<String, Object> map) {
		return sysGoodsCategoryDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysGoodsCategoryDao.queryTotal(map);
	}

	@Override
	public void save(SysGoodsCategoryEntity goodsCategory) {
		sysGoodsCategoryDao.save(goodsCategory);
	}

	@Override
	public void update(SysGoodsCategoryEntity goodsCategory) {
		sysGoodsCategoryDao.update(goodsCategory);
	}

	@Override
	public void delete(Long id) {
		sysGoodsCategoryDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysGoodsCategoryDao.deleteBatch(ids);
	}

	@Override
	public List<SysGoodsCategoryEntity> queryGoodsCategoryList() {
		return sysGoodsCategoryDao.queryGoodsCategoryList();
	}
}
