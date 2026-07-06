package com.dlc.modules.sys.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.PtExceptionWarningRuleEntity;

import java.util.Map;

/**
 * 异常预警后台 Service（第22步·运营域·异常预警）。
 * 规则五法（applicable_store_ids JSON 序列化：空数组=全部门店存 NULL）+ 记录只读列表 + 顶部统计卡片。
 * 记录本期不做处理态流转（§18.4）。
 *
 * @author claude
 */
public interface SysExceptionWarningService {

    /* ===== 规则五法 ===== */

    PageUtils queryRulePage(Query query);

    PtExceptionWarningRuleEntity queryRule(Long id);

    void saveRule(PtExceptionWarningRuleEntity rule);

    void updateRule(PtExceptionWarningRuleEntity rule);

    void deleteRule(Long[] ids);

    /* ===== 预警记录（只读） ===== */

    PageUtils queryRecordPage(Query query);

    /** 顶部统计卡片：todayNew/frequentCancel/lessonAbnormal/enabledRuleCount */
    Map<String, Object> queryStat(Map<String, Object> params);
}
