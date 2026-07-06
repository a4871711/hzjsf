package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.PtInstallmentTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 私教分期逾期扫描任务(第20步,详细实现文档资金域 §7.2)。
 * <p>每天 1:40 扫 pt_order_installment_bill:待支付(0)且 due_date&lt;今天 → 2已逾期,再把含逾期账单的进行中计划置3;
 * 错峰避开 0/1/2/8 整点及 1:10 权益到期/1:20 爽约/1:30 券过期既有任务。Task 类只调 Service,业务在
 * PtInstallmentTaskServiceImpl 内(命中事务)。注册在 spring-mvc.xml(手动 bean + task:scheduled,cron 0 40 1 * * ?),
 * 仿 PtBenefitExpireTask/UpdateOrderStatusTask。改本类或 api Mapper XML 须重启 Tomcat 生效。</p>
 *
 * @author claude
 */
public class InstallmentOverdueTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PtInstallmentTaskService ptInstallmentTaskService;

    public void scanOverdue() {
        log.info("私教分期逾期扫描 start...");
        try {
            int rows = ptInstallmentTaskService.scanOverdue();
            log.info("私教分期逾期扫描:标记逾期账单 " + rows + " 条");
        } catch (Exception e) {
            log.error("私教分期逾期扫描异常", e);
        }
        log.info("私教分期逾期扫描 end...");
    }
}
