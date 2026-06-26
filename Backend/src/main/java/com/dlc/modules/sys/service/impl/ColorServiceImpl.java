package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.ColorDao;
import com.dlc.modules.sys.entity.ColorEntity;
import com.dlc.modules.sys.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("colorService")
public class ColorServiceImpl implements ColorService {
	@Autowired
	private ColorDao colorDao;

	@Override
	public ColorEntity queryObject(Long id) {
		return colorDao.queryObject(id);
	}

	@Override
	public List<ColorEntity> queryList(Map<String, Object> map) {
		return colorDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return colorDao.queryTotal(map);
	}

	@Override
	public void save(ColorEntity color) {
		colorDao.save(color);
	}

	@Override
	public void update(ColorEntity color) {
		colorDao.update(color);
	}

	@Override
	public void delete(Long id) {
		colorDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		colorDao.deleteBatch(ids);
	}

	@Override
	public List<ColorEntity> selectColorDetail() {
		return colorDao.selectColorDetail();
	}

	@Override
	public List<Map<String, Object>> getExistsColor(Map<String, Object> map) {
		return colorDao.getExistsColor(map);
	}
}
