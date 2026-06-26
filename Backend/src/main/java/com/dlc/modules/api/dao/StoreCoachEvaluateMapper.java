package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StoreCoachEvaluate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreCoachEvaluateMapper {
    int deleteByPrimaryKey(Long evaluateId);

    int insert(StoreCoachEvaluate record);

    int insertSelective(StoreCoachEvaluate record);

    StoreCoachEvaluate selectByPrimaryKey(Long evaluateId);

    int updateByPrimaryKeySelective(StoreCoachEvaluate record);

    int updateByPrimaryKey(StoreCoachEvaluate record);

    int sumStoreCoachEvleve(Long coachId);

    int countStoreCoachEvleve(Long coachId);

    List<Map<String,Object>> sroreCoachEvaList(Query query);

    int queryStoreCoachTotal(Query query);

    int queryStoreCoachEvelTotal(Long coachId);
}