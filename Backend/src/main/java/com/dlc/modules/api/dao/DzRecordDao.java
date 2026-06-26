package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.DzRecordEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 点赞记录表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-26 11:03:59
 */
@Mapper
@Repository
public interface DzRecordDao extends BaseDao<DzRecordEntity> {

    int queryDzByUIdAndDyId(Map<String, Object> queryMap);

    int deleteDz(Map<String, Object> dzMap);
	
}
