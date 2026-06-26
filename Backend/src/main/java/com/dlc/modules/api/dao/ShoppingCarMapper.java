package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.GoodsOrder;
import com.dlc.modules.api.entity.ShoppingCar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ShoppingCarMapper {
    int deleteByPrimaryKey(Long shoppingCarId);

    int insert(ShoppingCar record);

    int insertSelective(ShoppingCar record);

    ShoppingCar selectByPrimaryKey(Long shoppingCarId);

    int updateByPrimaryKeySelective(ShoppingCar record);

    int updateByPrimaryKey(ShoppingCar record);

    List<Map<String,Object>> selectByUserId(Long userId);

    int updateShoppingCar(Map<String, Object> map);


    void delShoppingCar(@Param("userId") Long userId,@Param("goodsId") Long goodsId);

    List<Map<String,Object>> queryShCarByGoodsId(@Param("goodsId") Long goodsId,@Param("uId") Long uId);

    void addGoodsNum(@Param("pkId") Long pkId,@Param("goodsNum") Integer goodsNum);

    Map<String,Object> selectGoodsInfo(Long goodsId);
}