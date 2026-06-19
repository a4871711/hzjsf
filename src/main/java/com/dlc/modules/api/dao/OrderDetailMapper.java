package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OrderDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderDetail record);

    int insertSelective(OrderDetail record);

    OrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

    /**
     * 查询订单详情
     */
    List<Map<String, Object>> queryDetailByOrderNo(@Param("userId") Long userId, @Param("orderNo")String orderNo);
    //根据订单编号删除
    int deleteDetailByOrderNo(String orderNo);
}