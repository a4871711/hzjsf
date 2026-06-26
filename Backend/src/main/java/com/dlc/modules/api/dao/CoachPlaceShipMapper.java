package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.CoachPlaceShip;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CoachPlaceShipMapper {
    int deleteByPrimaryKey(Long cpsId);

    int insert(CoachPlaceShip record);

    int insertSelective(CoachPlaceShip record);

    CoachPlaceShip selectByPrimaryKey(Long cpsId);

    int updateByPrimaryKeySelective(CoachPlaceShip record);

    int updateByPrimaryKey(CoachPlaceShip record);

    List<Map<String,Object>> selectStoreIdByCoachId(Long coachId);
}