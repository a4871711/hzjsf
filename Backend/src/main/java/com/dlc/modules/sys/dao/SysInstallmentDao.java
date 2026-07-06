package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 后台 · 私教分期计划/账单只读查询 Dao(第20步资金域,/sys/installment)。
 * <p>计划/账单由订单生成与回调驱动,后台只读 + 催缴,本 Dao 不出写语句(催缴仅触达,不改账单状态)。
 * 联表带会员昵称/手机 + 关联商品名,列表统一返回 Map。对应 mapper/sys/SysInstallmentDao.xml(sys 目录 XML 可热刷新)。</p>
 * <p>门店隔离:分期计划挂订单,经 pt_private_order.store_id 过滤(storeIds 逗号串,空=超管看全部)。</p>
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysInstallmentDao {

    /** 计划分页(联订单 store_id 门店隔离 + 会员昵称/手机 + 商品名);params 含 planNo/keyword/status/startTime/endTime/storeIds/offset/limit */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /** 与 queryList 同条件总数 */
    int queryTotal(Map<String, Object> params);

    /** 计划详情(联会员昵称/手机 + 商品名) */
    Map<String, Object> queryObject(@Param("id") Long id);

    /** 计划下全部账单期明细(按期数升序) */
    List<Map<String, Object>> queryBills(@Param("planId") Long planId);

    /** 计划下逾期账单(status=2)计数:催缴前置(>0 才可催),并汇总逾期金额 */
    Map<String, Object> queryOverdueSummary(@Param("planId") Long planId);
}
