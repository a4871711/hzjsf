package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtOrderInstallmentPlanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 私教订单分期计划 Dao(pt_order_installment_plan,第20步)。
 * <p>建计划靠 uk_order_id 幂等(insertOnDuplicate);计划推进一律"行锁 selectByIdForUpdate → 带条件 UPDATE",
 * 与账单入账在同一事务。对应 mapper/api/PtOrderInstallmentPlanDao.xml(api XML 改动须重启 Tomcat)。</p>
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtOrderInstallmentPlanDao {

    /**
     * 建计划:INSERT ... ON DUPLICATE KEY UPDATE order_id=order_id,
     * 靠 uk_order_id 保证并发/重复下单只建一行(幂等);回填自增 id。
     */
    int insertOnDuplicate(PtOrderInstallmentPlanEntity plan);

    /** 按订单ID查(只读,无锁) */
    PtOrderInstallmentPlanEntity selectByOrderId(@Param("orderId") Long orderId);

    /** 按主键行锁取计划(推进入账用,串行化并发回调) */
    PtOrderInstallmentPlanEntity selectByIdForUpdate(@Param("id") Long id);

    /**
     * 计划推进(行锁内基于快照重算后写入):已付/未付/当前期/状态/激活时间。
     * activatedAt 传 null 表示不覆盖既有激活时间。
     */
    int updateProgress(@Param("id") Long id,
                       @Param("paidAmount") BigDecimal paidAmount,
                       @Param("unpaidAmount") BigDecimal unpaidAmount,
                       @Param("currentPeriod") Integer currentPeriod,
                       @Param("status") Integer status,
                       @Param("activatedAt") Date activatedAt);

    /** 逾期扫描:把"含 status=2 逾期账单"的进行中(1)计划置 3已逾期,批量幂等 */
    int markOverdueByBill();

    /** 会员端我的分期计划分页(倒序);params 含 memberId/offset/limit */
    List<PtOrderInstallmentPlanEntity> queryMyPlans(java.util.Map<String, Object> params);

    /** 与 queryMyPlans 同条件总数 */
    int countMyPlans(java.util.Map<String, Object> params);
}
