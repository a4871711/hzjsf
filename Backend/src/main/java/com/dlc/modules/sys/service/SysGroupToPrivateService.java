package com.dlc.modules.sys.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.PtGroupToPrivateLeadEntity;
import com.dlc.modules.sys.entity.PtGroupToPrivateRuleEntity;

/**
 * 团课转私教后台 Service（第22步·运营域·团课转私教）。
 * 规则五法 + 转化名单列表/详情 + 发券（复用营销域 grant）/跟进/标记转化。
 * 落 sys.service.impl 命中事务切面（REQUIRED）。名单由 api 侧 GroupToPrivateScanTask 每日 3:20 扫描 upsert。
 *
 * @author claude
 */
public interface SysGroupToPrivateService {

    /* ===== 规则五法 ===== */

    PageUtils queryRulePage(Query query);

    PtGroupToPrivateRuleEntity queryRule(Long id);

    void saveRule(PtGroupToPrivateRuleEntity rule);

    void updateRule(PtGroupToPrivateRuleEntity rule);

    void deleteRule(Long[] ids);

    /* ===== 转化名单与跟进 ===== */

    PageUtils queryLeadPage(Query query);

    /** 详情（含跟进流水 followList），门店隔离收口；越权/不存在返回 null */
    PtGroupToPrivateLeadEntity queryLeadDetail(Long id, String storeIds);

    /** 判存（发券/跟进/转化前越权校验） */
    boolean existsInScope(Long id, String storeIds);

    /**
     * 发私教体验券：状态机防重发（experience_coupon_status=0 且 follow_status!=2 才发）。
     * 单事务：调营销域 grant 给该会员 + 置 experience_coupon_status=1。
     */
    void sendCoupon(Long leadId, Long couponId);

    /** 跟进：单事务插 follow 流水 + 回写 lead 的 follow_status/follow_by/last_follow_time */
    void follow(Long leadId, Integer followStatus, String followRemark, Long operatorId);

    /** 标记已转化：follow_status→2（手动） */
    void markConverted(Long leadId);
}
