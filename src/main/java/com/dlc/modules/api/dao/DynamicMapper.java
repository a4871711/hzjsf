package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.Dynamic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DynamicMapper {
    int deleteByPrimaryKey(Long dynamicId);

    int insert(Dynamic record);

    int save(Dynamic record);

    Long insertDynamic(Dynamic record);

    Dynamic selectByPrimaryKey(Long dynamicId);

    int updateByPrimaryKeySelective(Dynamic record);

    int updateByPrimaryKey(Dynamic record);

    List<Map<String, Object>> queryDylist(Map<String, Object> queryMap);

    List<Map<String, Object>> queryMyDylist(Map<String, Object> queryMap);

    int queryMyDylistCount(Map<String, Object> queryMap);

    Map<String, Object> queryDynamicDetail(Map<String, Object> queryMap);
    /*评论+1*/
    int updatePlCount(Long dynamicId);
    /*点赞+1*/
    int updateDzCount(Map<String, Object> dzMap);
    /*批量阅读量+1*/
    int batchUpdateReCount(List<Long> dynamicId);
    /*单阅读量+1*/
    int updateReCount(Long dynamicId);

    int queryDylistCount(Query query);

    /**
     * 删除动态
     * @param deleteMap
     * @return
     */
    int deleteByPubIdDyId(Map<String, Object> deleteMap);
    /**查询点赞用户列表*/
    List<Map<String, Object>> queryDyUserlist(Long dynamicId);

    List<Map<String,Object>> queryDzMembersList(Query query);

    int queryDzMembersTotal(Query query);
}