package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 后台 · 私教分期 Service(第20步资金域,/sys/installment)。
 * <p>计划/账单由订单生成与回调驱动,后台只读 + 催缴,不提供 save/update/delete。查询走 SysInstallmentDao;
 * 催缴仅触达(站内信),不改账单状态。门店隔离经订单 store_id 由 controller 注入 storeIds。</p>
 *
 * @author claude
 */
public interface SysPtInstallmentService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 计划详情 + 全部账单期明细(返回 plan 与 bills) */
    Map<String, Object> queryDetail(Long id);

    /**
     * 催缴:仅当计划存在逾期账单(bill.status=2)可催,向会员推送站内信;不改账单状态,仅触达。
     *
     * @return true=已触达;false=无逾期账单(controller 提示)
     */
    boolean remind(Long id);
}
