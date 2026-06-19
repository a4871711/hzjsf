package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StoreCoach;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreCoachMapper {
    int deleteByPrimaryKey(Long scId);

    int insert(StoreCoach record);

    int insertSelective(StoreCoach record);

    StoreCoach selectByPrimaryKey(Long scId);

    int updateByPrimaryKeySelective(StoreCoach record);

    int updateByPrimaryKeyWithBLOBs(StoreCoach record);

    int updateByPrimaryKey(StoreCoach record);

    List<Map<String,Object>> recommendStoreCoach(Long storeId);

    Map<String,Object> selectStoreCoachByCoachId(Map<String, Object> params);

	int queryStoreCoachTotal(Query query);

	List<Map<String, Object>> queryStoreCoachList(Map<String, Object> params);
}