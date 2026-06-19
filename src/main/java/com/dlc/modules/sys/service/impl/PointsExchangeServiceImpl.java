package com.dlc.modules.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.dlc.modules.sys.dao.PointsExchangeDao;
import com.dlc.modules.sys.entity.PointsExchangeEntity;
import com.dlc.modules.sys.service.PointsExchangeService;



@Service("pointsExchangeService")
public class PointsExchangeServiceImpl implements PointsExchangeService {
	@Autowired
	private PointsExchangeDao pointsExchangeDao;
	
	@Override
	public PointsExchangeEntity queryObject(Long pointsExchangeId){
		return pointsExchangeDao.queryObject(pointsExchangeId);
	}
	
	@Override
	public List<PointsExchangeEntity> queryList(Map<String, Object> map){
		return pointsExchangeDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return pointsExchangeDao.queryTotal(map);
	}
	
	@Override
	public void save(PointsExchangeEntity pointsExchange){
		pointsExchangeDao.save(pointsExchange);
	}
	
	@Override
	public void update(PointsExchangeEntity pointsExchange){
		pointsExchangeDao.update(pointsExchange);
	}
	
	@Override
	public void delete(Long pointsExchangeId){
		pointsExchangeDao.delete(pointsExchangeId);
	}
	
	@Override
	public void deleteBatch(Long[] pointsExchangeIds){
		pointsExchangeDao.deleteBatch(pointsExchangeIds);
	}
	
}
