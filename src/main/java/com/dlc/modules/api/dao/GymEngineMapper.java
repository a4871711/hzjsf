package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GymEngine;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GymEngineMapper {
    int deleteByPrimaryKey(Long gymEngineId);

    int insert(GymEngine record);

    int insertSelective(GymEngine record);

    GymEngine selectByPrimaryKey(Long gymEngineId);

    int updateByPrimaryKeySelective(GymEngine record);

    int updateByPrimaryKeyWithBLOBs(GymEngine record);

    int updateByPrimaryKey(GymEngine record);
    /**
     *  @Auther:YD
     *  @parameters:
     *  查询器械列表
     */
    List<Map<String,Object>> queryGymEngineList(Map<String, Object> params);

    List<Map<String,Object>> queryGymEngineListByGtId(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    Map<String,Object> queryGymEngineInfo(Long id);

    int queryGymEngineTotalByGtId(Map<String, Object> params);
}