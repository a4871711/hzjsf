package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.TopicAttention;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TopicAttentionMapper {
    int insert(TopicAttention record);

    int insertSelective(TopicAttention record);

    /**
     * 查会员列表
     * @param topicId
     * @return
     */
    List<Map<String, Object>> queryTopicMenbers(Long topicId);

    /*查询话题详情中的成员列表*/
    List<Map<String,Object>> queryMemberList(Map<String, Object> queryMap);
    /**查成员总数*/
    int queryMemberCount(Map<String, Object> queryMap);

    int deleteAttend(TopicAttention topicAttention);
}