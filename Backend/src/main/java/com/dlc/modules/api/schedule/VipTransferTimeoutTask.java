package com.dlc.modules.api.schedule;

import com.dlc.modules.api.entity.VipBenefitTransfer;
import com.dlc.modules.api.service.VipTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * VIP 权益转让「受让人确认超时」扫描任务(详细技术设计 §7.6)。
 * 每小时扫 status=40 且 confirm_deadline < now 的转让单,逐笔置 52 已超时 + 退服务费 + 推送。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled,cron 0 0 0/1 * * ?),仿 UpdateOrderStatusTask。
 *
 * 事务边界:本类在 api.schedule 包,不在事务切面内,故只做循环调度;
 * 每一笔的「40→52 + 退费 + 推送」委托给 VipTransferService.timeoutOne(单笔一个 REQUIRED 事务),
 * 单笔失败只回滚该笔、不阻塞其余。
 */
public class VipTransferTimeoutTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private VipTransferService vipTransferService;

    public void confirmTimeoutScan() {
        log.info("VIP转让受让确认超时扫描 start...");
        try {
            List<VipBenefitTransfer> list = vipTransferService.listConfirmTimeout();
            for (VipBenefitTransfer t : list) {
                try {
                    // 逐笔独立事务:单笔失败不影响其余,下轮再扫
                    vipTransferService.timeoutOne(t.getTransferId());
                } catch (Exception e) {
                    log.error("VIP转让超时处理失败 transferId=" + t.getTransferId(), e);
                }
            }
        } catch (Exception e) {
            log.error("VIP转让超时扫描异常", e);
        }
        log.info("VIP转让受让确认超时扫描 end...");
    }
}
