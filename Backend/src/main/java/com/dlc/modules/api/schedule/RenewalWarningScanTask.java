package com.dlc.modules.api.schedule;

import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.service.RenewalWarningScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 续费预警扫描任务（第22步·运营域，详细实现文档 §5.1/§7）。
 * 每天 1:50 扫 pt_member_private_benefit 命中启用规则阈值(剩余课时≤阈值 或 剩余天数<阈值) → 生成/刷新预警记录。
 * 去重靠 benefit_id + closed_at IS NULL 应用层查重(RenewalWarningScanServiceImpl.processBenefit 单事务)。
 * 外层遍历(规则×权益)放本任务，逐条调用 service 处理，避免长事务；单条失败不影响其余。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled，cron 0 50 1 * * ?，错开既有任务)，仿 PtAppointmentNoShowTask。
 * 改本类或 api Mapper XML 须重启 Tomcat 生效。
 *
 * @author claude
 */
public class RenewalWarningScanTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RenewalWarningScanService renewalWarningScanService;

    public void scan() {
        log.info("私教续费预警扫描 start...");
        int hit = 0;
        try {
            List<Map<String, Object>> rules = renewalWarningScanService.loadEnabledRules();
            for (Map<String, Object> rule : rules) {
                Long ruleId = rule.get("id") == null ? null : Long.valueOf(String.valueOf(rule.get("id")));
                // 规则适用门店(空=全部门店)
                List<Long> storeIds = renewalWarningScanService.loadRuleStoreIds(ruleId);
                List<PtMemberPrivateBenefitEntity> benefits = renewalWarningScanService.loadActiveBenefits(storeIds);
                for (PtMemberPrivateBenefitEntity benefit : benefits) {
                    try {
                        // 单条独立事务：查重 + INSERT/UPDATE/close 幂等，单条失败不影响其余
                        if (renewalWarningScanService.processBenefit(rule, benefit)) {
                            hit++;
                        }
                    } catch (Exception e) {
                        log.error("续费预警处理失败 ruleId=" + ruleId + " benefitId=" + benefit.getId(), e);
                    }
                }
            }
            log.info("私教续费预警扫描:命中生成/刷新 " + hit + " 条");
        } catch (Exception e) {
            log.error("私教续费预警扫描异常", e);
        }
        log.info("私教续费预警扫描 end...");
    }
}
