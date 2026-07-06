package com.dlc.modules.sys.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.PtRenewalWarningRuleEntity;

/**
 * 续费预警后台 Service（第22步·运营域·续费预警）。
 * 规则五法（rule 主表 + _store_rel 全删全插，单事务）+ 记录只读列表 + 跟进/续费/忽略处理。
 * 落 sys.service.impl 命中事务切面（REQUIRED）。
 *
 * @author claude
 */
public interface SysRenewalWarningService {

    /* ===== 规则配置 ===== */

    PageUtils queryRulePage(Query query);

    PtRenewalWarningRuleEntity queryRule(Long id);

    /** 单事务：插 rule + 批量插 _store_rel（storeIds 空=全部门店不插 rel）；条件必填校验 */
    void saveRule(PtRenewalWarningRuleEntity rule);

    /** 单事务：更新 rule + 先删后插 _store_rel */
    void updateRule(PtRenewalWarningRuleEntity rule);

    /** 删 rule + 级联删 _store_rel */
    void deleteRule(Long[] ids);

    /* ===== 预警记录处理 ===== */

    PageUtils queryRecordPage(Query query);

    /** 跟进：置 follow_status=1 + 记备注；门店隔离收口 */
    void follow(Long recordId, String followRemark, String storeIds);

    /** 标记状态：2已续费/3已忽略，写 closed_at 关闭记录；门店隔离收口 */
    void markStatus(Long recordId, Integer followStatus, String storeIds);
}
