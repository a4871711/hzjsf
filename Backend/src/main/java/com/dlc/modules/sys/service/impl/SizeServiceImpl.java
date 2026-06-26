package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SizeDao;
import com.dlc.modules.sys.entity.SizeEntity;
import com.dlc.modules.sys.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sizeService")
public class SizeServiceImpl implements SizeService {
	@Autowired
	private SizeDao sizeDao;

	@Override
	public SizeEntity queryObject(Long id) {
		return sizeDao.queryObject(id);
	}

	@Override
	public List<SizeEntity> queryList(Map<String, Object> map) {
		return sizeDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sizeDao.queryTotal(map);
	}

	@Override
	public void save(SizeEntity sizeEntity) {
		sizeDao.save(sizeEntity);
	}

	@Override
	public void update(SizeEntity sizeEntity) {
		sizeDao.update(sizeEntity);
	}

	@Override
	public void delete(Long id) {
		sizeDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sizeDao.deleteBatch(ids);
	}

	@Override
	public List<SizeEntity> selectSizeDetail() {
		return sizeDao.selectSizeDetail();
	}

	@Override
	public List<Map<String, Object>> getExistsSize(Map<String, Object> map) {
		return sizeDao.getExistsSize(map);
	}
}
