package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;

import java.util.List;
import java.util.Map;

public interface StoreGroupService {
    /**
     * 查询同城社群
     * @return
     */
    PageUtils querySameCityGroup(Map<String, Object> params);
    /**热门*/
    PageUtils queryHotGroup(Map<String, Object> params);
    /**我的*/
    PageUtils queryMyGroup(Map<String, Object> params);
    /*查社群详情*/
    Map<String, Object> queryGroupDetail(Long storeGroupId, Long userId);
    /*成员列表查询*/
    List<Map<String, Object>> queryGroupMemberList(Map<String, Object> queryMap);
    /*查成员总数*/
    int queryGroupMemberCount(Query query);
    /**社群添加关注*/
    R attendGroup(Long userId, Long storeGroupId, Integer type);

    PageUtils queryGroupByUid(Map<String, Object> params);
}
