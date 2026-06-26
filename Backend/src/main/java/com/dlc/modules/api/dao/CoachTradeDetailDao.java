package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.CoachTradeDetailEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 教练收支明细
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-10-20 14:50:47
 */
@Mapper
@Repository
public interface CoachTradeDetailDao extends BaseDao<CoachTradeDetailEntity> {
	
}
