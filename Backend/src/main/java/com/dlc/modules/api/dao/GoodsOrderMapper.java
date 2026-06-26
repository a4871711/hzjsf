package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GoodsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsOrderMapper {
    int deleteByPrimaryKey(Long goodsOrderId);

    int insert(GoodsOrder record);

    int insertSelective(GoodsOrder record);

    GoodsOrder selectByPrimaryKey(Long goodsOrderId);

    int updateByPrimaryKeySelective(GoodsOrder record);

    int updateByPrimaryKey(GoodsOrder record);

    List<Map<String, Object>> queryGoodsOrderByUserId(Map<String, Object> map);

    List<Map<String, Object>> queryGoodsOrderByStatus(Map<String, Object> map);

    void updateByOrderNo(Map<String, Object> map);

    Long queryUserIdByOrderNo(String out_trade_no);

    GoodsOrder queryCouponMoneyByOrderNo(String orderNo);

    /**
     * 查询订单
     * @param userId
     * @param orderNo
     * @return
     */
    Map<String, Object> queryOrderDetail(@Param("userId") Long userId, @Param("orderNo")String orderNo);

    GoodsOrder selectGoodOrderByOrder(@Param("orderNo") String orderNo);
    /*全部*/
    int queryGoodsOrderCount(Query query);
    /*0待收货，1待发货，2已完成*/
    int queryGoodsOrderByStatusNum(Query query);
    //取消订单
    int updateOrderStatus(Map<String, Object> updateMap);
    //根据订单编号查订单中的所有商品id
    List<Long> queryGidByOrderNo(String orderNo);
    //删除商城订单
    int deleteByOrderNo(String orderNo);
    //根据订单编号查询订单信息
    Map<String, Object> queryGoodsOrderByOrder(String orderNo);

    List<GoodsOrder> selectAllOrderByStatus();

    int queryIfExistGoods(Long goodsId);
}