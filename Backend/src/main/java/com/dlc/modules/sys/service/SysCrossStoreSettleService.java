package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtCrossStoreSettlementRuleEntity;

/**
 * 跨店结算规则 Service（全系统单套规则，get/save upsert）。
 * 本期仅配置不落账，收入报表不按本规则拆分（第23步 §17.1/§17.3）。
 *
 * @author claude
 */
public interface SysCrossStoreSettleService {

    /** 取当前唯一规则；无则返回 null（由 controller 兜默认空模板）。 */
    PtCrossStoreSettlementRuleEntity queryCurrent();

    /**
     * 保存规则（upsert）：存在则 update，不存在则 insert，保证全局一条。
     * 比例校验：incomeOwnerType=3 时 buyStoreRatio+lessonStoreRatio=100；=1 强制(100,0)；=2 强制(0,100)。
     *
     * @param operatorId 当前 sys 用户ID
     */
    void saveOrUpdate(PtCrossStoreSettlementRuleEntity entity, Long operatorId);
}
