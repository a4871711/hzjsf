package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;

import java.util.List;
import java.util.Map;

public interface FindService {
    /**
     * 查询发现（“热门话题” 和 “会员动态”）
     * @param userId
     * @param page
     *@param limit @return
     */
    R queryFind(Long userId, Integer page, Integer limit);

    /**
     * 查询我的关注
     * @param params
     * @return
     */
    PageUtils queryMyAttention(Map<String, Object> params);

    /**
     * 添加关注
     *
     * @param userId
     * @param attentedId
     * @param type
     * @return
     */
    int addAttention(Long userId, Long attentedId, Integer type);

    /**
     * 取消关注
     * @param userId
     * @param attentedId
     * @return
     */
    int cancelAttention(Long userId, Long attentedId);

    /**
     * 举报
     * @param userId
     * @param attentedId
     * @param reportType
     * @return
     */
    int report(Long userId, Long attentedId, String reportType);

    /**
     * 查动态列表
     * @param queryMap
     * @return
     */
    PageUtils queryFindDynamic(Map<String, Object> queryMap);

    /**
     *查询举报类型
     * @return
     */
    List<Map<String, Object>> queryReportType();

    PageUtils queryDyList(Map<String, Object> queryMap);
    /**查粉丝列表*/
    PageUtils queryFans(Map<String, Object> params);
    /**查关注列表*/
    PageUtils queryAttend(Map<String, Object> params);
}
