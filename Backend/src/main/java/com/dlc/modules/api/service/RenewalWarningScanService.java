package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;

import java.util.List;
import java.util.Map;

/**
 * 续费预警扫描 Service（api 侧，第22步·运营域）。
 * 落 api.service.impl 命中事务切面（REQUIRED）：单条「查重 + INSERT/UPDATE」在一个方法内即单事务，
 * 整批循环放在 Task（不放大事务，逐条提交）。去重靠 benefit_id + closed_at IS NULL 应用层查重。
 *
 * @author claude
 */
public interface RenewalWarningScanService {

    /** 启用中的续费预警规则（Task 用于外层遍历） */
    List<Map<String, Object>> loadEnabledRules();

    /** 规则适用门店ID（空=全部门店） */
    List<Long> loadRuleStoreIds(Long ruleId);

    /** 某规则下按门店过滤的生效中权益（Task 用于外层遍历） */
    List<PtMemberPrivateBenefitEntity> loadActiveBenefits(List<Long> storeIds);

    /**
     * 处理单个权益命中判定（单事务）：不命中任一阈值→自动关闭已有未关闭记录；
     * 命中→已存在未关闭记录则刷新，否则插新记录。推送教练在方法内 try/catch 不回滚。
     *
     * @return true=生成/更新了预警记录，false=未命中(已跳过或关闭)
     */
    boolean processBenefit(Map<String, Object> rule, PtMemberPrivateBenefitEntity benefit);
}
