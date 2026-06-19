package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PointsExchange;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PointsExchangeMapper {
    int deleteByPrimaryKey(Long pointsExchangeId);

    int insert(PointsExchange record);

    int insertSelective(PointsExchange record);

    PointsExchange selectByPrimaryKey(Long pointsExchangeId);

    int updateByPrimaryKeySelective(PointsExchange record);

    int updateByPrimaryKey(PointsExchange record);

    /**
     * 查询积分明细
     * @param userId
     * @return
     */
    List<Map<String, Object>> getPointDetailByUserId(Long userId);

    Integer querySumDayPoint(Long userId);
    Integer queryRulePoints(Long userId);
}