package com.dlc.modules.api.schedule;

import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.api.service.PrivateAppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 私教预约爽约扫描任务(第16步,详细实现文档交易域 §7)。
 * 每天 1:20 扫 pt_private_appointment:appointment_status=1 且预约结束时间已过 NO_SHOW_GRACE_HOURS
 * 小时仍未核销/取消 → 置 4爽约,并按商品 no_show_deduct 处理课时(扣课=finish / 不扣=cancel)。
 *
 * 事务边界:本类在 api.schedule 包,不在事务切面内,只做扫描与循环调度;
 * 每一条的「1→4 + 账本联动」委托 PrivateAppointmentService.markNoShow(单条一个 REQUIRED 事务),
 * 单条失败只回滚该条、不阻塞其余,下轮再扫。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled,cron 0 20 1 * * ?,错开 0/1/2/8 整点既有任务),
 * 仿 VipTransferTimeoutTask。改本类或 api Mapper XML 须重启 Tomcat 生效。
 */
public class PtAppointmentNoShowTask {

    /**
     * 爽约判定宽限期(小时):结束时间过后留给门店人工核销/取消的窗口。
     * 详细实现文档 §7 仅写"end_time 已过 N 小时"未定 N,按第16步任务口径取 24h。
     */
    private static final int NO_SHOW_GRACE_HOURS = 24;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;
    @Autowired
    private PrivateAppointmentService privateAppointmentService;

    public void scanNoShow() {
        log.info("私教预约爽约扫描 start...");
        try {
            List<PtPrivateAppointmentEntity> list =
                    ptPrivateAppointmentDao.queryNoShowDue(NO_SHOW_GRACE_HOURS);
            for (PtPrivateAppointmentEntity apt : list) {
                try {
                    // 逐条独立事务:markNoShow 内条件 UPDATE 1→4 幂等,单条失败不影响其余
                    privateAppointmentService.markNoShow(apt.getId());
                } catch (Exception e) {
                    log.error("私教预约爽约处理失败 appointmentNo=" + apt.getAppointmentNo(), e);
                }
            }
            log.info("私教预约爽约扫描:待处理 " + list.size() + " 条");
        } catch (Exception e) {
            log.error("私教预约爽约扫描异常", e);
        }
        log.info("私教预约爽约扫描 end...");
    }
}
