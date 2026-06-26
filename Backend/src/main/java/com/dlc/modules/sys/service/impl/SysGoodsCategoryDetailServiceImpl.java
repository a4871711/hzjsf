package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysGoodsCategoryDetailDao;
import com.dlc.modules.sys.entity.SysGoodsCategoryDetailEntity;
import com.dlc.modules.sys.service.SysGoodsCategoryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysGoodsCategoryDetailService")
public class SysGoodsCategoryDetailServiceImpl implements SysGoodsCategoryDetailService {
	@Autowired
	private SysGoodsCategoryDetailDao sysGoodsCategoryDetailDao;

	@Override
	public SysGoodsCategoryDetailEntity queryObject(Long id) {
		return sysGoodsCategoryDetailDao.queryObject(id);
	}

	@Override
	public List<SysGoodsCategoryDetailEntity> queryList(Map<String, Object> map) {
		return sysGoodsCategoryDetailDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysGoodsCategoryDetailDao.queryTotal(map);
	}

	@Override
	public void save(SysGoodsCategoryDetailEntity goodsCategoryDetail) {
		sysGoodsCategoryDetailDao.save(goodsCategoryDetail);
	}

	@Override
	public void update(SysGoodsCategoryDetailEntity goodsCategoryDetail) {
		sysGoodsCategoryDetailDao.update(goodsCategoryDetail);
	}

	@Override
	public void delete(Long id) {
		sysGoodsCategoryDetailDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysGoodsCategoryDetailDao.deleteBatch(ids);
	}

	@Override
	public List<SysGoodsCategoryDetailEntity> queryGoodsCategoryDetailList() {
		return sysGoodsCategoryDetailDao.queryGoodsCategoryDetailList();
	}
}
