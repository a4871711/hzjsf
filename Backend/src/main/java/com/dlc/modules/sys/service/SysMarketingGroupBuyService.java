package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.MkGroupBuyActivityEntity;

import java.util.List;
import java.util.Map;

/**
 * 拼团活动 Service（mk_group_buy_activity）
 *
 * @author claude
 */
public interface SysMarketingGroupBuyService {

    MkGroupBuyActivityEntity queryObject(Long id);

    List<MkGroupBuyActivityEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(MkGroupBuyActivityEntity entity);

    void update(MkGroupBuyActivityEntity entity);

    /** 逻辑删除；进行中活动（时间窗内且 status=1）禁删，需先下架 */
    void deleteBatch(Long[] ids);

    /** 上下架切换：1上架/0下架。下架后不可发起新团 */
    void changeStatus(Long id, Integer status, Long userId);
}
