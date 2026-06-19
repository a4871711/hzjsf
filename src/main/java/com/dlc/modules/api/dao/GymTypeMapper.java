package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GymType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GymTypeMapper {
    int deleteByPrimaryKey(Long gtId);

    int insert(GymType record);

    int insertSelective(GymType record);

    GymType selectByPrimaryKey(Long gtId);

    int updateByPrimaryKeySelective(GymType record);

    int updateByPrimaryKey(GymType record);

    List<Map<String,Object>> queryOneTypeList(Map<String, Object> params);

    int queryOneTypeTotal(Map<String, Object> params);

    List<GymType> queryGymTypeList(Query query);

    int queryGymTypeTotal(Query query);

    List<GymType> selectAllGymType();

}