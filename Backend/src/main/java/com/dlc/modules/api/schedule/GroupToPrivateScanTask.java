package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.GroupToPrivateScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 团课转私教识别扫描任务（第22步·运营域，详细实现文档 §5.3/§7）。
 * 每天 3:20 扫团课出勤(stu_teamclass_ship 已完成)/购课(team_class_order 已支付)达阈值的高意向会员 → 幂等落名单。
 * 去重靠 uk_member_id + ON DUPLICATE KEY UPDATE(GroupToPrivateScanServiceImpl.processRule 单事务，不覆盖人工态)。
 * 外层遍历规则放本任务，逐规则调用 service；单条失败不影响其余。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled，cron 0 20 3 * * ?，错开既有任务)，仿 PtAppointmentNoShowTask。
 * 改本类或 api Mapper XML 须重启 Tomcat 生效。
 *
 * @author claude
 */
public class GroupToPrivateScanTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private GroupToPrivateScanService groupToPrivateScanService;

    public void scan() {
        log.info("团课转私教高意向名单扫描 start...");
        int total = 0;
        try {
            List<Map<String, Object>> rules = groupToPrivateScanService.loadEnabledRules();
            for (Map<String, Object> rule : rules) {
                try {
                    // 单规则单事务：合并候选 + 逐条 upsert 幂等，单规则失败不影响其余
                    total += groupToPrivateScanService.processRule(rule);
                } catch (Exception e) {
                    log.error("团课转私教规则处理失败 ruleId=" + rule.get("id"), e);
                }
            }
            log.info("团课转私教高意向名单扫描:命中 upsert " + total + " 条");
        } catch (Exception e) {
            log.error("团课转私教高意向名单扫描异常", e);
        }
        log.info("团课转私教高意向名单扫描 end...");
    }
}
