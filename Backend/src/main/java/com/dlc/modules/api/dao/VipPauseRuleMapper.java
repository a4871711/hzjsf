package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.VipPauseRule;

/**
 * 停卡规则(vip_pause_rule)Mapper(移动端只读)
 * 仅在停卡预检/付费停卡申请时按 pause_rule_id 读 tiers_json 算档位。
 */
public interface VipPauseRuleMapper {

    /** 按主键查停卡规则,不存在返回 null */
    VipPauseRule selectById(Long pauseRuleId);
}
