package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.VipFeeRuleEntity;

import java.util.List;
import java.util.Map;

/**
 * VIP 转让费用规则 Service
 */
public interface SysVipFeeRuleService {

    VipFeeRuleEntity queryObject(Long feeRuleId);

    List<VipFeeRuleEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(VipFeeRuleEntity vipFeeRule);

    void update(VipFeeRuleEntity vipFeeRule);

    void deleteBatch(Long[] feeRuleIds);

    /** 某费用规则被权益卡引用的数量(供删除前校验) */
    int countCardRefByRuleId(Long feeRuleId);
}
