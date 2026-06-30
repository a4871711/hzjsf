package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;

import java.util.List;
import java.util.Map;

/**
 * 教练课时费/分成规则 Service（扩展版，全系统唯一）。
 * matchFeeRule 供交易域结算、运营域报表跨域复用。
 *
 * @author claude
 */
public interface SysCoachFeeRuleService {

    PtCoachFeeRuleEntity queryObject(Long id);

    List<PtCoachFeeRuleEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtCoachFeeRuleEntity entity);

    void update(PtCoachFeeRuleEntity entity);

    void deleteBatch(Long[] ids);

    void changeStatus(Long id, Integer status);

    /**
     * 按 4 级优先级匹配教练规则：
     * L1 教练+门店+课程 > L2 教练+课程 > L3 教练+门店 > L4 教练默认。
     * 命中返回规则，无匹配返回 null。
     *
     * @param ruleType 1课时费 2销售提成；为 null 表示不限类型
     */
    PtCoachFeeRuleEntity matchFeeRule(Long coachId, Long productId, Long storeId, Integer ruleType);
}
