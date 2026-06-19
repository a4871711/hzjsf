package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.DynamicEvaluate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DynamicEvaluateMapper {
    int deleteByPrimaryKey(Long dyEvaluatId);

    int insert(DynamicEvaluate record);

    int insertSelective(DynamicEvaluate record);

    DynamicEvaluate selectByPrimaryKey(Long dyEvaluatId);

    int updateByPrimaryKeySelective(DynamicEvaluate record);

    int updateByPrimaryKey(DynamicEvaluate record);

    List<Map<String, Object>> queryEvaluatList(Map<String, Object> queryMap);

    int queryEvaluatListCount(Query query);
}