package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.PointsExchange;

import java.util.List;
import java.util.Map;

/**
 * @author lnx
 * @Date 2018-09-15
 */
public interface PointsService {

    /**
     * 查询该用户积分数，可兑换余额
     */
    Map<String, Object> queryMyPoints(Long userId);

    /**
     * 积分明细查询
     */
    List<Map<String, Object>> queryPointsExchange(Long userId);

    /**
     * 积分兑换
     */
    R pointsExchange(PointsExchange pointsExchange, Long userId);

    /**
     * 定时能量换积分
     * @param updateMap
     * @return
     */
    int updatePointCountTask(Map<String, Object> updateMap);
}
