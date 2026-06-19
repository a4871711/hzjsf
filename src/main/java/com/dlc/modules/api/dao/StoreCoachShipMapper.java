package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.StoreCoachShip;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreCoachShipMapper {
    int deleteByPrimaryKey(Long storeCoachShipId);

    int insert(StoreCoachShip record);

    int insertSelective(StoreCoachShip record);

    StoreCoachShip selectByPrimaryKey(Long storeCoachShipId);

    int updateByPrimaryKeySelective(StoreCoachShip record);

    int updateByPrimaryKey(StoreCoachShip record);
}