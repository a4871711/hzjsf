package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;

import java.util.List;
import java.util.Map;

/**
 * 话题
 */
public interface TopicService {
    /**分页查询全部话题列表
    */
    PageUtils queryTopicList(Map<String, Object> map);

    /**
     * 查询话题详情
     *
     * @param userId
     * @param topicId
     * @return
     */
    Map<String, Object> queryTopicDetail(Long userId, Long topicId);

    /**
     * 查询话题中的成员列表
     * @return
     */
    List<Map<String, Object>> queryTopicMemberList(Map<String, Object> queryMap);

    /**
     * 查话题成员总数
     * @param queryMap
     * @return
     */
    int queryTopicMemberCount(Query queryMap);

    /**
     * 关注话题
     * @param userId
     * @param topicId
     * @param type
     * @return
     */
    R attendTopic(Long userId, Long topicId, Integer type);


}
