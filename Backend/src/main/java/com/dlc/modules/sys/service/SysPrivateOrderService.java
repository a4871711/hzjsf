package com.dlc.modules.sys.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教购买记录后台 Service(第15步):只读列表/详情 + 退款(委托 api 侧 PrivateOrderService.refund 同事务)。
 * 后台不手工建/删订单,无 save/update/delete。
 *
 * @author claude
 */
public interface SysPrivateOrderService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 详情(订单+权益课时+券明细);查不到或不在 storeIds 门店范围返回 null(controller 按 404 处理) */
    Map<String, Object> queryDetail(Long id, String storeIds);

    /** 门店范围内判存(退款前越权校验) */
    boolean existsInScope(Long id, String storeIds);

    /**
     * 退款冲减:整体委托 api 侧 PrivateOrderService.refund(单事务,行锁+金额上限校验防重复)。
     * 校验/渠道/冲减/负向流水口径见该方法注释。
     */
    void refund(Long orderId, BigDecimal refundAmount, Integer refundLessons, String remark, Long operatorId);
}
