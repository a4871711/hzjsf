package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.TopicEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 话题表
 *
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-18 14:46:39
 */
@Mapper
@Repository
public interface TopicDao extends BaseDao<TopicEntity> {
    /**
     * 查询热门话题
     */
    List<Map<String, Object>> queryHotTopic();

    /**
     * 分页查询话题
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryTopicList(Map<String, Object> map);

    /*查询话题详情*/
    Map<String, Object> queryTopicById(Map<String, Object> queryMap);

    /**
     * 查询全部话题
     * @return
     */
    List<Map<String,Object>> queryAllTopicList();

    int queryTopicListCount(Query query);
    /*话题中的动态数*/
    int queryTopicDyCount(Long topicId);
}
