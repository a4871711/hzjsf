package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.RenewalWarningScanDao;
import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.service.RenewalWarningScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 续费预警扫描 Service 实现（api 侧，第22步·运营域·续费预警，依据详细实现文档 §5.1）。
 * 事务切面覆盖 api.service.impl 全方法（REQUIRED）：processBenefit 的「查重 + INSERT/UPDATE/close」为单事务，
 * 由 RenewalWarningScanTask 外层逐条调用，避免长事务。
 * 幂等：benefit_id + closed_at IS NULL 应用层查重，命中已存在则刷新，否则插新；
 *       不再命中任一阈值/权益失效 → 自动关闭已有未关闭记录。
 * 推送教练：本期占位(日志)，如需接友盟 UPush 在此 try/catch 调用，失败不回滚。
 *
 * @author claude
 */
@Service("renewalWarningScanService")
public class RenewalWarningScanServiceImpl implements RenewalWarningScanService {

    private static final Logger log = LoggerFactory.getLogger(RenewalWarningScanServiceImpl.class);

    /** 一天的毫秒数，用于剩余天数计算 */
    private static final long ONE_DAY_MILLIS = 24L * 60 * 60 * 1000;

    @Autowired
    private RenewalWarningScanDao renewalWarningScanDao;

    @Override
    public List<Map<String, Object>> loadEnabledRules() {
        return renewalWarningScanDao.selectEnabledRules();
    }

    @Override
    public List<Long> loadRuleStoreIds(Long ruleId) {
        return renewalWarningScanDao.selectRuleStoreIds(ruleId);
    }

    @Override
    public List<PtMemberPrivateBenefitEntity> loadActiveBenefits(List<Long> storeIds) {
        return renewalWarningScanDao.selectActiveBenefits(storeIds);
    }

    @Override
    public boolean processBenefit(Map<String, Object> rule, PtMemberPrivateBenefitEntity benefit) {
        // —— 阈值命中判定 ——
        boolean lessonEnabled = toInt(rule.get("lessonWarningEnabled")) != null
                && toInt(rule.get("lessonWarningEnabled")) == 1;
        Integer lessonThreshold = toInt(rule.get("lessonThreshold"));
        boolean hitLesson = lessonEnabled && lessonThreshold != null
                && benefit.getRemainingLessons() != null
                && benefit.getRemainingLessons() <= lessonThreshold;

        // 剩余天数：长期权益(expire_at 为空)不算天数
        Integer remainingDays = calcRemainingDays(benefit.getExpireAt());
        boolean daysEnabled = toInt(rule.get("daysWarningEnabled")) != null
                && toInt(rule.get("daysWarningEnabled")) == 1;
        Integer daysThreshold = toInt(rule.get("daysThreshold"));
        boolean hitDays = daysEnabled && daysThreshold != null
                && remainingDays != null && remainingDays < daysThreshold;

        if (!hitLesson && !hitDays) {
            // 不命中任一阈值：兜底关闭该权益已有未关闭记录(权益失效场景在 Dao 内 NOT EXISTS 收口)
            renewalWarningScanDao.autoCloseByBenefit(benefit.getId());
            return false;
        }

        // 3=同时命中(§14.3 合并一条)，1=课时不足，2=有效期不足
        int warningType = (hitLesson && hitDays) ? 3 : (hitLesson ? 1 : 2);
        Long coachId = renewalWarningScanDao.selectLatestCoachId(benefit.getId());

        // —— 去重写入 ——
        Long existId = renewalWarningScanDao.selectOpenRecordId(benefit.getId());
        if (existId != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("recordId", existId);
            params.put("remainingLessons", benefit.getRemainingLessons());
            params.put("remainingDays", remainingDays);
            params.put("warningType", warningType);
            params.put("coachId", coachId);
            renewalWarningScanDao.updateOpenRecord(params);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("ruleId", toLong(rule.get("id")));
            params.put("memberId", benefit.getMemberId());
            params.put("benefitId", benefit.getId());
            params.put("coachId", coachId);
            params.put("storeId", benefit.getStoreId());
            params.put("productId", benefit.getProductId());
            params.put("remainingLessons", benefit.getRemainingLessons());
            params.put("remainingDays", remainingDays);
            params.put("warningType", warningType);
            renewalWarningScanDao.insertRecord(params);
        }

        // 推送教练(§14.4)：尽力而为，失败不回滚记录落库
        try {
            pushToCoach(coachId, benefit, warningType);
        } catch (Exception e) {
            log.error("续费预警推送教练失败 benefitId={} coachId={}", benefit.getId(), coachId, e);
        }
        return true;
    }

    /** 剩余天数 = ceil((expire_at - now)/1天)；已过期/长期返回口径：expire_at 空返回 null(长期不预警) */
    private Integer calcRemainingDays(Date expireAt) {
        if (expireAt == null) {
            return null;
        }
        long diff = expireAt.getTime() - System.currentTimeMillis();
        if (diff <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) diff / ONE_DAY_MILLIS);
    }

    /** 推送提醒给教练（本期占位；接入友盟 UPush 时在此实现，由调用方 try/catch 兜底） */
    private void pushToCoach(Long coachId, PtMemberPrivateBenefitEntity benefit, int warningType) {
        // TODO 友盟推送接线：coachId 为空则跳过；本期仅日志，避免误报推送异常影响主流程
        if (coachId == null) {
            return;
        }
        log.info("续费预警[占位推送] coachId={} benefitId={} warningType={}", coachId, benefit.getId(), warningType);
    }

    private Integer toInt(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : Integer.valueOf(s);
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : Long.valueOf(s);
    }
}
