package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.StoreGoods;

public interface StoreGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(StoreGoods record);

    int insertSelective(StoreGoods record);

    StoreGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(StoreGoods record);

    int updateByPrimaryKey(StoreGoods record);
}