package com.dlc.modules.api.service;

import java.util.List;
import java.util.Map;

/**
 * 异常预警扫描 Service（api 侧，第22步·运营域，依据 §5.2）。
 * 落 api.service.impl 命中事务切面（REQUIRED）：单条 upsert 为单事务，整批循环放在 Task。
 * 幂等靠 uk_exc_warn_dedup + ON DUPLICATE KEY UPDATE。本期只落记录不流转（§18.4）。
 *
 * @author claude
 */
public interface ExceptionWarningScanService {

    /** 启用中的异常预警规则（Task 用于外层遍历） */
    List<Map<String, Object>> loadEnabledRules();

    /**
     * 处理单条规则（单事务）：按周期聚合命中会员并逐个 upsert 记录。
     *
     * @return 该规则命中并 upsert 的记录数
     */
    int processRule(Map<String, Object> rule);
}
