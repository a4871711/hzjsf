package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.dlc.modules.api.dao.ExceptionWarningScanDao;
import com.dlc.modules.api.service.ExceptionWarningScanService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常预警扫描 Service 实现（api 侧，第22步·运营域·异常预警，依据详细实现文档 §5.2）。
 * 事务切面覆盖 api.service.impl 全方法（REQUIRED）：processRule 内「聚合 + 逐条 upsert」为单事务，
 * 由 ExceptionWarningScanTask 外层逐规则调用。
 * 周期口径：periodStart = today - (period_days - 1) 天，periodEnd = today（闭区间，SQL 按 < periodEnd+1天取）。
 * 幂等：uk_exc_warn_dedup(rule_id,member_id,warning_type,period_start,period_end) + ON DUPLICATE KEY UPDATE。
 *
 * @author claude
 */
@Service("exceptionWarningScanService")
public class ExceptionWarningScanServiceImpl implements ExceptionWarningScanService {

    private static final Logger log = LoggerFactory.getLogger(ExceptionWarningScanServiceImpl.class);

    @Autowired
    private ExceptionWarningScanDao exceptionWarningScanDao;

    @Override
    public List<Map<String, Object>> loadEnabledRules() {
        return exceptionWarningScanDao.selectEnabledRules();
    }

    @Override
    public int processRule(Map<String, Object> rule) {
        Integer warningType = toInt(rule.get("warningType"));
        Integer periodDays = toInt(rule.get("periodDays"));
        Integer threshold = toInt(rule.get("triggerThreshold"));
        Long ruleId = toLong(rule.get("id"));
        if (warningType == null || periodDays == null || periodDays <= 0 || threshold == null) {
            return 0;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String periodEnd = df.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -(periodDays - 1));
        String periodStart = df.format(cal.getTime());

        List<Long> storeIds = parseStoreIds((String) rule.get("applicableStoreIds"));

        List<Map<String, Object>> rows;
        String descTpl;
        if (warningType == 1) {
            rows = exceptionWarningScanDao.countFrequentCancel(periodStart, periodEnd, threshold, storeIds);
            descTpl = periodDays + "天内取消";
        } else {
            rows = exceptionWarningScanDao.countLessonAbnormal(periodStart, periodEnd, threshold, storeIds);
            descTpl = periodDays + "天内完成";
        }

        int count = 0;
        for (Map<String, Object> r : rows) {
            Integer cnt = toInt(r.get("cnt"));
            String triggerDesc = warningType == 1
                    ? descTpl + cnt + "次"
                    : descTpl + cnt + "节";
            Map<String, Object> params = new HashMap<>();
            params.put("ruleId", ruleId);
            params.put("warningType", warningType);
            params.put("memberId", toLong(r.get("memberId")));
            params.put("coachId", toLong(r.get("coachId")));
            params.put("storeId", toLong(r.get("storeId")));
            params.put("periodStart", periodStart);
            params.put("periodEnd", periodEnd);
            params.put("triggerValue", cnt);
            params.put("triggerDesc", triggerDesc);
            exceptionWarningScanDao.upsertRecord(params);
            count++;
        }
        return count;
    }

    /* ============ 私有辅助 ============ */

    private List<Long> parseStoreIds(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(json, Long.class);
        } catch (Exception ex) {
            log.warn("异常预警规则门店JSON解析失败,按全部门店处理: {}", json);
            return new ArrayList<>();
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
