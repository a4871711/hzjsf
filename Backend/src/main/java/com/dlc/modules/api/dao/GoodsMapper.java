package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Map<String,Object>> latestGoodsList(Map<String, Object> query);

    int latestGoodsListCount(Query query);

    List<Map<String,Object>>  goodsDetails(Long goodsId);

    List<Map<String,Object>> queryGoodsColor(Long goodsId);

    List<Map<String,Object>> queryGoodsSize(@Param("goodsId") Long goodsId,@Param("color") String color);
}