package com.dlc.modules.sys.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教购买记录后台 Service：列表/详情/退款，以及后台手工建单和关联数据删除。
 *
 * @author claude
 */
public interface SysPrivateOrderService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 按列表筛选及门店权限统计订单、已支付订单、实付和退款金额。 */
    Map<String, Object> queryStats(Map<String, Object> params);

    /** 详情(订单+权益课时+券明细);查不到或不在 storeIds 门店范围返回 null(controller 按 404 处理) */
    Map<String, Object> queryDetail(Long id, String storeIds);

    /** 门店范围内判存(退款前越权校验) */
    boolean existsInScope(Long id, String storeIds);

    /** 后台建单会员候选；仅返回当前管理员管辖门店的会员。 */
    List<Map<String, Object>> queryMemberOptions(String keyword, String storeIds);

    /** 后台建单商品候选；仅返回所选门店可购买的上架商品。 */
    List<Map<String, Object>> queryProductOptions(String keyword, Long storeId, String storeIds);

    /** 后台建单教练候选；仅返回可在所选门店服务该商品的正常教练。 */
    List<Map<String, Object>> queryCoachOptions(String keyword, Long productId, Long storeId,
                                                String storeIds);

    /**
     * 后台手工创建已结清订单，并同步创建权益、销量、入账、提成及附赠权益等本地业务数据。
     * 返回生成的订单号。
     */
    String createManual(Map<String, Object> params, Long operatorId, String storeIds);

    /**
     * 永久删除订单及其可定位的本地关联数据；confirmOrderNo 必须与锁内订单号完全一致。
     * 第三方支付平台的真实资金不会因本操作自动退款。
     */
    void deleteCascade(Long orderId, String confirmOrderNo, Long operatorId, String storeIds);

    /**
     * 退款冲减:整体委托 api 侧 PrivateOrderService.refund(单事务,行锁+金额上限校验防重复)。
     * 校验/渠道/冲减/负向流水口径见该方法注释。
     */
    void refund(Long orderId, BigDecimal refundAmount, Integer refundLessons, String remark, Long operatorId);
}
