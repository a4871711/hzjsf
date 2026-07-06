package com.dlc.modules.api.schedule;

import com.dlc.modules.api.dao.PtMemberPrivateBenefitDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 私教权益到期扫描任务(第16步,详细实现文档交易域 §7)。
 * 每天 1:10 扫 pt_member_private_benefit:status=1 且 expire_at<now 且 frozen_lessons=0 → 置 3已过期。
 * frozen=0 条件天然跳过有进行中预约的权益(冻结课时待预约 finish/cancel 解冻后由下轮收口),
 * 单条批量 UPDATE 自带原子与幂等,无账本增减,不必进事务切面,直调 dao 即可。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled,cron 0 10 1 * * ?,错开 0/1/2/8 整点既有任务),
 * 仿 UpdateOrderStatusTask。改本类或 api Mapper XML 须重启 Tomcat 生效。
 */
public class PtBenefitExpireTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PtMemberPrivateBenefitDao ptMemberPrivateBenefitDao;

    public void expireBenefit() {
        log.info("私教权益到期扫描 start...");
        try {
            int rows = ptMemberPrivateBenefitDao.expireBatch();
            log.info("私教权益到期扫描:置已过期 " + rows + " 条");
        } catch (Exception e) {
            log.error("私教权益到期扫描异常", e);
        }
        log.info("私教权益到期扫描 end...");
    }
}
