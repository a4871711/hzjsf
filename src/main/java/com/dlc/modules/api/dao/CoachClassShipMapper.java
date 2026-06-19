package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.CoachClassShip;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CoachClassShipMapper {
    int deleteByPrimaryKey(Long coachClassShipId);

    int insert(CoachClassShip record);

    int insertSelective(CoachClassShip record);

    CoachClassShip selectByPrimaryKey(Long coachClassShipId);

    int updateByPrimaryKeySelective(CoachClassShip record);

    int updateByPrimaryKey(CoachClassShip record);

    List<Map<String,Object>> queryCoachClass(Map<String, Object> map);

    int queryCoachClassRecordTotal(Query query);

    int batchCoachClassShip(List<CoachClassShip> coachClassList);

    Map<String,Object> isExistClass(CoachClassShip coachClassShip);

    List<CoachClassShip> queryAllCoachClass(Long coachId);
}