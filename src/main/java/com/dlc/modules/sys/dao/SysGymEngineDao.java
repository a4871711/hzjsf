package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.GymEngineEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 机械表
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-11 09:17:04
 */
@Mapper
@Repository
public interface SysGymEngineDao extends BaseDao<GymEngineEntity> {
	
}
