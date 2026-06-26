package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.GoodsOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 商城订单表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-10-25 17:12:06
 */
@Mapper
@Repository
public interface GoodsOrderDao extends BaseDao<GoodsOrderEntity> {
    List<GoodsOrderEntity> queryLists(Map<String, Object> map);

    int queryListsCount(Map<String, Object> map);

    List<Map<String,Object>> queryDetails(Long goodsOrderId);
}
