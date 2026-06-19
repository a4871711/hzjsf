package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StoreGroup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreGroupMapper {
    int deleteByPrimaryKey(Long storeGroupId);

    int insert(StoreGroup record);

    int insertSelective(StoreGroup record);

    StoreGroup selectByPrimaryKey(Long storeGroupId);

    int updateByPrimaryKeySelective(StoreGroup record);

    int updateByPrimaryKey(StoreGroup record);
    /**查平台社群列表*/
    List<Map<String, Object>> queryStoreGroupList();
    /**查平台同城社群列表*/
    List<Map<String,Object>> querySameCityGroupList(Map<String, Object> queryMap);
    /*查询同城分页总数*/
    int querySameCityGroupTotal(Map<String, Object> params);
    /*热门*/
    List<Map<String,Object>> queryHotGroup(Query query);
    /*查询热门总数*/
    int queryHotGroupTotal();
    /*查询我的社群列表*/
    List<Map<String,Object>> queryMyGroup(Query query);
    /*查询我的社群列表总数*/
    int queryMyGroupTotal(Query query);
    /*查询社群详情*/
    Map<String, Object> queryGroupDetail(Map<String, Object> queryMap);
    /*查社群下的会员数*/
    List<Map<String, Object>> queryGroupMembers(@Param("storeGroupId") Long storeGroupId);
    /*查询社群下的动态列表*/
    List<Map<String,Object>> queryDynamicList(Map<String, Object> queryMap);

    int queryDynamicListCount(Map<String, Object> queryMap);

    List<Map<String,Object>> queryGroupByUid(Map<String, Object> queryMap);

    int queryGroupByUidCount(Query query);
}