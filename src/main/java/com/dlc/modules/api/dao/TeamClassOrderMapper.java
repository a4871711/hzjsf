package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.TeamClassOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TeamClassOrderMapper {
    int deleteByPrimaryKey(Long teamClassOrderId);

    int insert(TeamClassOrder record);

    int insertSelective(TeamClassOrder record);

    TeamClassOrder selectByPrimaryKey(Long teamClassOrderId);

    int updateByPrimaryKeySelective(TeamClassOrder record);

    int updateByPrimaryKey(TeamClassOrder record);

    List<Map<String, Object>> queryTeamClassOrder(Map<String, Object> queryMap);

    int queryTeamClassOrderNum(Map<String, Object> queryMap);

    /**
     * 查询团教课订单
     * @param userId
     * @param orderNo
     * @return
     */
    Map<String, Object> queryTeamOrderDetail(@Param("userId") Long userId, @Param("orderNo")String orderNo);

    TeamClassOrder selectTeamClassOrderByOrderNo(@Param("orderNo") String orderNo);
    //取消订单
    int updateTeamOrderStatus(Map<String, Object> updateMap);

    List<TeamClassOrder> selectAllTcListByStatus();
}