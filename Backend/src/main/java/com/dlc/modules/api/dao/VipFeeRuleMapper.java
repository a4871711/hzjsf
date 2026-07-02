package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.VipFeeRule;

/**
 * VIP 转让费用规则(vip_fee_rule)Mapper(移动端只读)
 * 仅在试算/发起转让时按 fee_rule_id 读 tiers_json 算分档费用。
 */
public interface VipFeeRuleMapper {

    /** 按主键查费用规则,不存在返回 null */
    VipFeeRule selectById(Long feeRuleId);
}
