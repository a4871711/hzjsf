package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.PtOrderInstallmentBillDao;
import com.dlc.modules.api.dao.PtOrderInstallmentPlanDao;
import com.dlc.modules.api.service.PtInstallmentTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 私教分期逾期扫描 Service 实现(第20步资金域)。落在 api.service.impl,命中事务切面(REQUIRED)。
 * <p>批量条件 UPDATE 自带原子与幂等;标记逾期账单 + 逾期计划,再触发暂停约课(交易域维度,当前留桩)。
 * api 目录 Mapper XML 改动须重启 Tomcat。</p>
 *
 * @author claude
 */
@Service("ptInstallmentTaskService")
public class PtInstallmentTaskServiceImpl implements PtInstallmentTaskService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PtOrderInstallmentBillDao ptOrderInstallmentBillDao;
    @Autowired
    private PtOrderInstallmentPlanDao ptOrderInstallmentPlanDao;

    @Override
    public int scanOverdue() {
        // 1) 标记逾期账单:0待支付 且 due_date<今天 → 2已逾期(走 idx_due_date,批量幂等)
        int overdueBills = ptOrderInstallmentBillDao.markOverdueDue();
        // 2) 计划状态:含 status=2 逾期账单的 进行中(1)计划 → 3已逾期(批量幂等)
        int overduePlans = ptOrderInstallmentPlanDao.markOverdueByBill();
        // 3) 暂停新预约:对逾期且规则 overdue_pause_booking=1 的会员打"暂停约课"标记(交易域权益/会员维度),
        //    已预约成功的课程不取消(需求 §15.2)。当前第14步交易域暂无"暂停/恢复约课"开关,留桩标注,
        //    待交易域暴露方法后接线;切勿在本域私自实现约课拦截,避免与交易域双写冲突(详见详细文档§9.7)。
        // TODO(第20步/待交易域) 交易域.pauseBookingForOverduePlans();
        log.info("私教分期逾期扫描:标记逾期账单 {} 条,逾期计划 {} 个(暂停约课待交易域接线,留桩)",
                overdueBills, overduePlans);
        return overdueBills;
    }
}
