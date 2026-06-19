package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysTopicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 话题表
 * 
 * @author wangsheng
 * @email
 * @date 2018-09-15 09:27:07
 */
@Repository
@Mapper
public interface SysTopicDao extends BaseDao<SysTopicEntity> {

	
}
