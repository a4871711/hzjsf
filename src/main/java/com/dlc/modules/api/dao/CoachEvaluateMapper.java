package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.CoachEvaluate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CoachEvaluateMapper {
    int deleteByPrimaryKey(Long coachEvaluatId);

    int insert(CoachEvaluate record);

    int insertSelective(CoachEvaluate record);

    CoachEvaluate selectByPrimaryKey(Long coachEvaluatId);

    int updateByPrimaryKeySelective(CoachEvaluate record);

    int updateByPrimaryKey(CoachEvaluate record);

    int countCoachEva(Long coachId);

    List<Map<String,Object>> coachEvaList(Query query);

    int queryTotal(Query query);
}