package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.ActivityTrain;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ActivityTrainMapper {
    int deleteByPrimaryKey(Long activityTrainId);

    int insert(ActivityTrain record);

    int insertSelective(ActivityTrain record);

    ActivityTrain selectByPrimaryKey(Long activityTrainId);

    int updateByPrimaryKeySelective(ActivityTrain record);

    int updateByPrimaryKeyWithBLOBs(ActivityTrain record);

    int updateByPrimaryKey(ActivityTrain record);

    List<Map<String,Object>> queryActivityTrainList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    Map<String,Object> queryActivityTrainInfo(Long id);

    List<Map<String,Object>>  queryActivityTrainListByAtId(Map<String, Object> params);

    int queryActivityTrainTotalByAtId(Map<String, Object> params);
}