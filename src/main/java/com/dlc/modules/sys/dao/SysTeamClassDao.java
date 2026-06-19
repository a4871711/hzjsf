package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysTeamClassDao extends BaseDao<TeamClassEntity> {
	List<SysStoreEntity> selectStoreName(@Param("storeIds") String storeIds);
    //分页查询团体课列表
    List<Map<String,Object>> queryTeamClass(Query query);
    //分页查询团体课列表
    int queryTeamClassTotal(Query query);

    int batchUpdateStatus(List<Long> teamClassIdList);

    int queryOverTimeClass(List<Long> teamClassIdList);
}
