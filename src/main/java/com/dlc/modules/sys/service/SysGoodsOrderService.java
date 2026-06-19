package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.GoodsOrderEntity;

import java.util.List;
import java.util.Map;

public interface SysGoodsOrderService {
    GoodsOrderEntity queryObject(Long goodsOrderId);

    List<GoodsOrderEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(GoodsOrderEntity goodsOrder);

    void update(GoodsOrderEntity goodsOrder);

    void delete(Long goodsOrderId);

    void deleteBatch(Long[] goodsOrderIds);

    List<Map<String,Object>> queryDetailList(Long goodsOrderId);
}
