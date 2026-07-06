package com.dlc.modules.api.schedule;

import com.dlc.modules.api.dao.MkMemberCouponApiDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 营销券过期扫描任务(第18步,详细实现文档营销域 §5.6/§7)。
 *
 * 每天 1:30 扫 mk_member_coupon:use_status=0 未使用 且 expire_time<now → 置 2已过期。
 * 与四状态口径(0未使用/3使用中/1已使用/2已过期)一致——只动「0且已过期」,不碰 3使用中/1已使用。
 * 单条批量 UPDATE 自带原子与幂等,无账本增减,不必进事务切面,直调 dao 即可(仿 PtBenefitExpireTask)。
 *
 * 命名规避:MarketingCouponExpireTask 无既有同名类;所依赖的是 api 侧 MkMemberCouponApiDao(mk_coupon 体系),
 * 与旧 CouponService/user_coupon 无关。
 * 注册在 spring-mvc.xml(手动 bean + task:scheduled,cron 0 30 1 * * ?,错开既有 1:10 权益到期 / 1:20 爽约
 * 及 0/1/2/8 整点任务)。改本类或 api Mapper XML 须重启 Tomcat 生效。
 *
 * @author claude
 */
public class MarketingCouponExpireTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MkMemberCouponApiDao mkMemberCouponApiDao;

    public void expireCoupon() {
        log.info("营销券过期扫描 start...");
        try {
            int rows = mkMemberCouponApiDao.expireBatch();
            log.info("营销券过期扫描:置已过期 " + rows + " 条");
        } catch (Exception e) {
            log.error("营销券过期扫描异常", e);
        }
        log.info("营销券过期扫描 end...");
    }
}
