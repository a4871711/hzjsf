package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PointsExchangeEntity;

import java.util.List;
import java.util.Map;

/**
 * 积分兑换明细表
 * 
 * @author LINGKANGMING
 * @email 1647595314@qq.com
 * @date 2018-12-30 14:19:37
 */
public interface PointsExchangeService {
	
	PointsExchangeEntity queryObject(Long pointsExchangeId);
	
	List<PointsExchangeEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(PointsExchangeEntity pointsExchange);
	
	void update(PointsExchangeEntity pointsExchange);
	
	void delete(Long pointsExchangeId);
	
	void deleteBatch(Long[] pointsExchangeIds);
}
