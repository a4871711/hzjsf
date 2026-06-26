package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysAdvertisingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 广告表
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-12 16:11:35
 */
@Mapper
@Repository
public interface SysAdvertisingDao extends BaseDao<SysAdvertisingEntity> {
	
}
