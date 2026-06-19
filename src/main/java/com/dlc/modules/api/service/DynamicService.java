package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.DynamicRequest;

import java.util.List;
import java.util.Map;

public interface DynamicService {
    /**
     * 查询动态详情
     * @param dynamicId
     * @param userId
     * @return
     */
    Map<String, Object> queryDynamicDetail(Long dynamicId, Long userId);

    /**
     * 发布动态
     * @return
     * @param dynamicRequest
     */
    R publishDynamic(DynamicRequest dynamicRequest);

    /**
     * 查询社群列表
     * @return
     */
    List<Map<String, Object>> queryGroupList();

    /**查询话题列表*/
    List<Map<String, Object>> queryTopicList();

    PageUtils queryMyDynamic(Map<String, Object> params);

    PageUtils queryDyPlList(Map<String, Object> params);
    /**删除动态*/
    int deleteDynamic(Long dynamicId, Long userId);

    List<Map<String,Object>> queryDzMembersList(Query query);

    int queryDzMembersTotal(Query query);
}
