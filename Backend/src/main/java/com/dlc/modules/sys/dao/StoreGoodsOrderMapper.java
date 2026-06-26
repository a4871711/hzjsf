package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.StoreGoodsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreGoodsOrderMapper {
    int deleteByPrimaryKey(Long sgoId);

    int insert(StoreGoodsOrder record);

    int insertSelective(StoreGoodsOrder record);

    StoreGoodsOrder selectByPrimaryKey(Long sgoId);

    int updateByPrimaryKeySelective(StoreGoodsOrder record);

    int updateByPrimaryKey(StoreGoodsOrder record);

    int batchInsertGoodsOrder(List<Map<String, Object>> goodsOrderList);
}