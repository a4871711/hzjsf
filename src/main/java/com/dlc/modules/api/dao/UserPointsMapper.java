package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.UserPoints;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Mapper
@Repository
public interface UserPointsMapper {
    int deleteByPrimaryKey(Long userPointsId);

    int insert(UserPoints record);

    int insertSelective(UserPoints record);

    UserPoints selectByPrimaryKey(Long userPointsId);

    int updateByPrimaryKeySelective(UserPoints record);

    int updateByPrimaryKey(UserPoints record);

    Map<String, Object> getPointsByUserId(Long userId);

    int updatePointCount(Map<String, Object> updateMap);

    int updatePointCountTask(Map<String, Object> updateMap);

    int updatePointByCal(Map<String, Object> params);

    /*查询积分by 能量*/
    Integer getPointByCal(Map<String, Object> params);

    Long getSysFlags();
}