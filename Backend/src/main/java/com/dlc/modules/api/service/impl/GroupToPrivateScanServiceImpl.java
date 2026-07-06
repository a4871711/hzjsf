package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.GroupToPrivateScanDao;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.service.GroupToPrivateScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 团课转私教扫描 Service 实现（api 侧，第22步·运营域，依据详细实现文档 §5.3）。
 * 事务切面覆盖 api.service.impl 全方法（REQUIRED）：processRule 内「聚合候选 + 逐个 upsert」为单事务，
 * 由 GroupToPrivateScanTask 外层逐规则调用；autoConvertOnPaid 亦为单事务（由回调方 try/catch 兜底）。
 * 候选合并：出勤维度 + 购课维度并集，按 member_id 归并 attendance_count/purchase_count/intention_reason。
 * 幂等：uk_pt_group_to_private_lead_member_id + ON DUPLICATE KEY UPDATE，UPDATE 不动人工 follow_status/experience_coupon_status。
 *
 * @author claude
 */
@Service("groupToPrivateScanService")
public class GroupToPrivateScanServiceImpl implements GroupToPrivateScanService {

    private static final Logger log = LoggerFactory.getLogger(GroupToPrivateScanServiceImpl.class);

    @Autowired
    private GroupToPrivateScanDao groupToPrivateScanDao;

    @Override
    public List<Map<String, Object>> loadEnabledRules() {
        return groupToPrivateScanDao.selectEnabledRules();
    }

    @Override
    public int processRule(Map<String, Object> rule) {
        Long ruleId = toLong(rule.get("id"));
        Integer attendanceDays = toInt(rule.get("attendanceDays"));
        Integer attendanceThreshold = toInt(rule.get("attendanceThreshold"));
        Integer purchaseDays = toInt(rule.get("purchaseDays"));
        Integer purchaseThreshold = toInt(rule.get("purchaseThreshold"));

        // 按 memberId 归并候选（保序，便于阅读日志）
        Map<Long, Candidate> candidates = new LinkedHashMap<>();

        // 出勤维度
        if (attendanceDays != null && attendanceDays > 0 && attendanceThreshold != null && attendanceThreshold > 0) {
            List<Map<String, Object>> rows = groupToPrivateScanDao.countAttendance(attendanceDays, attendanceThreshold);
            for (Map<String, Object> r : rows) {
                Long memberId = toLong(r.get("memberId"));
                if (memberId == null) { continue; }
                Candidate c = candidates.computeIfAbsent(memberId, k -> new Candidate());
                c.storeId = toLong(r.get("storeId"));
                c.attendanceCount = toInt(r.get("cnt"));
                c.appendReason("近" + attendanceDays + "天出勤" + c.attendanceCount + "次");
            }
        }

        // 购课维度
        if (purchaseDays != null && purchaseDays > 0 && purchaseThreshold != null && purchaseThreshold > 0) {
            List<Map<String, Object>> rows = groupToPrivateScanDao.countPurchase(purchaseDays, purchaseThreshold);
            for (Map<String, Object> r : rows) {
                Long memberId = toLong(r.get("memberId"));
                if (memberId == null) { continue; }
                Candidate c = candidates.computeIfAbsent(memberId, k -> new Candidate());
                if (c.storeId == null) {
                    c.storeId = toLong(r.get("storeId"));
                }
                c.purchaseCount = toInt(r.get("cnt"));
                c.appendReason("近" + purchaseDays + "天购课" + c.purchaseCount + "次");
            }
        }

        int count = 0;
        for (Map.Entry<Long, Candidate> e : candidates.entrySet()) {
            Candidate c = e.getValue();
            Map<String, Object> params = new HashMap<>();
            params.put("memberId", e.getKey());
            params.put("storeId", c.storeId);
            params.put("ruleId", ruleId);
            params.put("attendanceCount", c.attendanceCount);
            params.put("purchaseCount", c.purchaseCount);
            params.put("intentionReason", c.reason.length() > 255
                    ? c.reason.substring(0, 255) : c.reason.toString());
            groupToPrivateScanDao.upsertLead(params);
            count++;
        }
        return count;
    }

    @Override
    public void autoConvertOnPaid(PtPrivateOrderEntity order) {
        if (order == null || order.getMemberId() == null) {
            return;
        }
        Long leadId = groupToPrivateScanDao.selectUnconvertedLeadId(order.getMemberId());
        if (leadId == null) {
            return;
        }
        // 条件 UPDATE follow_status<2→2，命中0行=已被抢先转化，幂等跳过跟进流水
        if (groupToPrivateScanDao.autoMarkConverted(leadId) > 0) {
            groupToPrivateScanDao.insertAutoFollow(leadId, order.getMemberId());
            log.info("团课转私教:会员购买私教自动标记已转化 memberId={} leadId={}", order.getMemberId(), leadId);
        }
    }

    /* ============ 私有辅助 ============ */

    /** 候选会员聚合体：合并出勤/购课两维度 */
    private static class Candidate {
        Long storeId;
        Integer attendanceCount = 0;
        Integer purchaseCount = 0;
        StringBuilder reason = new StringBuilder();

        void appendReason(String part) {
            if (reason.length() > 0) {
                reason.append('，');
            }
            reason.append(part);
        }
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
