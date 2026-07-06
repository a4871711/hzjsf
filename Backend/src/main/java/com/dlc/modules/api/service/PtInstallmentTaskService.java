package com.dlc.modules.api.service;

/**
 * 私教分期逾期扫描 Service(第20步,M4 资金域)。落在 api.service.impl,命中事务切面(REQUIRED)。
 * <p>由 {@code InstallmentOverdueTask} 定时驱动;Task 类只调本 Service,业务(标记逾期/暂停约课)在此内完成。
 * api 目录 Mapper XML 改动须重启 Tomcat。</p>
 *
 * @author claude
 */
public interface PtInstallmentTaskService {

    /**
     * 逾期扫描:
     * <ol>
     *   <li>标记逾期账单:0待支付 且 due_date&lt;今天 → 2已逾期(批量幂等);</li>
     *   <li>计划状态:含 status=2 逾期账单的 进行中(1)计划 → 3已逾期;</li>
     *   <li>暂停新预约:对逾期且规则 overdue_pause_booking=1 的会员打"暂停约课"标记(交易域权益/会员维度),
     *       已预约成功的课程不取消(需求 §15.2)。当前交易域暂无暂停开关,留桩标注。</li>
     * </ol>
     *
     * @return 本轮新标记逾期的账单数
     */
    int scanOverdue();
}
