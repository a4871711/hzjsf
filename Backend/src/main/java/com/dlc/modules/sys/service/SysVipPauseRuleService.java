package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.VipPauseRuleEntity;

import java.util.List;
import java.util.Map;

/**
 * VIP 停卡规则 Service
 */
public interface SysVipPauseRuleService {

    VipPauseRuleEntity queryObject(Long pauseRuleId);

    List<VipPauseRuleEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(VipPauseRuleEntity vipPauseRule);

    void update(VipPauseRuleEntity vipPauseRule);

    void deleteBatch(Long[] pauseRuleIds);

    /** 某停卡规则被权益卡引用的数量(供删除前校验) */
    int countCardRefByRuleId(Long pauseRuleId);
}
