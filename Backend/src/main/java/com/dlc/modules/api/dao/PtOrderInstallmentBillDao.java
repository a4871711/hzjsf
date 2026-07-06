package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtOrderInstallmentBillEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 私教订单分期账单 Dao(pt_order_installment_bill,第20步)。
 * <p>下单全量建账单(insertOnDuplicate,uk_(plan_id,period_no) 兜底重复);逐期入账"行锁 selectByPayOrderNoForUpdate
 * → markPaid 带 status IN(0,2) 条件"防重复入账。对应 mapper/api/PtOrderInstallmentBillDao.xml(api XML 改动须重启)。</p>
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtOrderInstallmentBillDao {

    /** 全量建账单(单期):INSERT ... ON DUPLICATE KEY UPDATE plan_id=plan_id,uk_(plan_id,period_no) 幂等 */
    int insertOnDuplicate(PtOrderInstallmentBillEntity bill);

    /** 按计划+期数查(只读) */
    PtOrderInstallmentBillEntity selectByPlanAndPeriod(@Param("planId") Long planId, @Param("periodNo") Integer periodNo);

    /** 按主键查(只读) */
    PtOrderInstallmentBillEntity selectById(@Param("id") Long id);

    /** 按主键行锁(会员付某期前置校验用) */
    PtOrderInstallmentBillEntity selectByIdForUpdate(@Param("id") Long id);

    /** 按本期支付单号行锁(回调入账定位账单,串行化重复回调) */
    PtOrderInstallmentBillEntity selectByPayOrderNoForUpdate(@Param("payOrderNo") String payOrderNo);

    /** 计划下全部账单(详情/结清判断用,按期数升序) */
    List<PtOrderInstallmentBillEntity> selectByPlanId(@Param("planId") Long planId);

    /** 计划下逾期账单(status=2)计数:>0 表示仍有逾期,结清逐期入账判断是否回正进行中用 */
    int countOverdueByPlan(@Param("planId") Long planId);

    /** 写本期支付单号(会员发起付某期时占位):WHERE id=? 覆盖旧单号,允许重付换单号 */
    int updatePayOrderNo(@Param("id") Long id, @Param("payOrderNo") String payOrderNo);

    /** 入账:0待支付/2已逾期→1已支付+实付+支付时间;WHERE status IN(0,2) 幂等,0行=已入账/并发 */
    int markPaid(@Param("id") Long id, @Param("paidAmount") BigDecimal paidAmount, @Param("paidTime") Date paidTime);

    /** 逾期标记:0待支付→2已逾期;WHERE status=0 且 due_date<今天,批量幂等(逾期扫描用) */
    int markOverdueDue();

    /**
     * 按本期支付单号反查会员/门店(记账用,IncomePayDetail 后缀9/a 分支)。
     * 联 plan 取 member_id、联 order 取 store_id;返回 memberId/storeId,查不到=null。
     */
    java.util.Map<String, Object> selectMemberStoreByPayOrderNo(@Param("payOrderNo") String payOrderNo);
}
