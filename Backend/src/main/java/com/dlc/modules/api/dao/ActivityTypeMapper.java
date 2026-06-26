package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.ActivityType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ActivityTypeMapper {
    int deleteByPrimaryKey(Long atId);

    int insert(ActivityType record);

    int insertSelective(ActivityType record);

    ActivityType selectByPrimaryKey(Long atId);

    int updateByPrimaryKeySelective(ActivityType record);

    int updateByPrimaryKey(ActivityType record);

    List<Map<String,Object>> queryActivityTypeList(Map<String, Object> params);

    int queryActivityTypeTotal(Map<String, Object> params);

    List<ActivityType> queryActivTypeList(Query query);

    int queryActivTypeTotal(Query query);

    List<ActivityType> queryActivTypeAll();
}