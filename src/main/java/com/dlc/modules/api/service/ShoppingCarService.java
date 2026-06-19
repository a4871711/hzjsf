package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.ShoppingCar;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShoppingCarService {
    void saveShoppingCar(ShoppingCar shoppingCar);

    List<Map<String,Object>> queryList(Long userId);

    int updateShoppingCar(Map<String,Object> map);

    int deleteShoppingCar(Long userId, String pkIds);
}
