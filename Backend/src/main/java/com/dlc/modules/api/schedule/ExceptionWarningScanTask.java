package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.ExceptionWarningScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 异常预警扫描任务（第22步·运营域，详细实现文档 §5.2/§7）。
 * 每天 3:10 扫 pt_private_appointment：频繁取消(status=2)/课时消耗异常(status=3) 按周期聚合达阈值 → 幂等落记录。
 * 去重靠 uk_exc_warn_dedup + ON DUPLICATE KEY UPDATE(ExceptionWarningScanServiceImpl.processRule 单事务)。
 * 外层遍历规则放本任务，逐规则调用 service；单条失败不影响其余。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled，cron 0 10 3 * * ?，错开既有任务)，仿 PtAppointmentNoShowTask。
 * 改本类或 api Mapper XML 须重启 Tomcat 生效。
 *
 * @author claude
 */
public class ExceptionWarningScanTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExceptionWarningScanService exceptionWarningScanService;

    public void scan() {
        log.info("私教异常预警扫描 start...");
        int total = 0;
        try {
            List<Map<String, Object>> rules = exceptionWarningScanService.loadEnabledRules();
            for (Map<String, Object> rule : rules) {
                try {
                    // 单规则单事务：聚合命中会员 + 逐条 upsert 幂等，单规则失败不影响其余
                    total += exceptionWarningScanService.processRule(rule);
                } catch (Exception e) {
                    log.error("异常预警规则处理失败 ruleId=" + rule.get("id"), e);
                }
            }
            log.info("私教异常预警扫描:命中 upsert " + total + " 条");
        } catch (Exception e) {
            log.error("私教异常预警扫描异常", e);
        }
        log.info("私教异常预警扫描 end...");
    }
}
