package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PrivateClassOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PrivateClassOrderMapper {
    int deleteByPrimaryKey(Long privateClassOrderId);

    int insert(PrivateClassOrder record);

    int insertSelective(PrivateClassOrder record);

    PrivateClassOrder selectByPrimaryKey(Long privateClassOrderId);

    int updateByPrimaryKeySelective(PrivateClassOrder record);

    int updateByPrimaryKey(PrivateClassOrder record);

    List<Map<String, Object>> queryPrivateClassOrder(Map<String, Object> queryMap);

    int queryPrivateClassOrderNum(Map<String, Object> queryMap);

    /**
     * 查询私教课订单
     * @param userId
     * @param orderNo
     * @return
     */
    Map<String, Object> queryPrivateOrderDetail(@Param("userId") Long userId, @Param("orderNo")String orderNo);

    PrivateClassOrder selectPrivateClassOrderByOrder(@Param("orderNo") String orderNo);

    Double queryCoachTotalIncome(Long coachId);

    List<Map<String,Object>> queryOrderList(Query query);

    int queryOrderListTotal(Query query);

    Map<String,Object> queryPrivateOrderDetailByOrderId(Long privateClassOrderId);
    //取消订单
    int updatePrivateOrderStatus(Map<String, Object> updateMap);
    //
    int updateStatus(Map<String, Object> queryMap);

    List<PrivateClassOrder> selectPcoListByStatus();

    int queryIsOrBuyTyclass(Long userId);
}