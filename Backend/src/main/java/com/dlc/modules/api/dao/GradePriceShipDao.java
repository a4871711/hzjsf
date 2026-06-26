package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.GradePriceShipEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 等级-私教课价格映射表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-19 15:59:25
 */
@Mapper
public interface GradePriceShipDao extends BaseDao<GradePriceShipEntity> {
	
}
