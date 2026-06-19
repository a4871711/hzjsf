package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PointsExchangeEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 积分兑换明细表
 * 
 * @author LINGKANGMING
 * @email 1647595314@qq.com
 * @date 2018-12-30 14:19:37
 */
@Mapper
@Repository
public interface PointsExchangeDao extends BaseDao<PointsExchangeEntity> {
	
}
