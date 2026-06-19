package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StoreAttention;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreAttentionMapper {
    int insert(StoreAttention record);

    int insertSelective(StoreAttention record);
    /*查询成员列表*/
    List<Map<String,Object>> queryMemberList(Map<String, Object> queryMap);
    /*查成员数量*/
    int queryMemberCount(Query query);

    int deleteAttends(StoreAttention storeAttention);
}