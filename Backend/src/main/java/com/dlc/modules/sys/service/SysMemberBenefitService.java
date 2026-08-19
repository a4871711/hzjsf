package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益后台 Service(第15步)。
 * 课时账本仍由下单/预约/退款链路驱动,后台只允许批量调整权益的到期日和归属门店。
 *
 * @author claude
 */
public interface SysMemberBenefitService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 顶部统计卡:各状态数量 + 剩余/冻结课时合计(随 list 一并返回) */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 详情;查不到或不在 storeAddrIds 门店范围返回 null(controller 按 404 处理) */
    Map<String, Object> queryDetail(Long id, String storeIds);

    /** 按每条权益自己的 expire_at 增加/减少天数,操作前必须整批通过门店和状态校验。 */
    void batchAdjustExpireDate(List<Long> benefitIds, String operation, Integer days, String storeAddrIds);

    /** 批量变更权益未来使用门店,历史预约记录不跟随变更。 */
    void batchChangeStore(List<Long> benefitIds, Long targetStoreAddrId, String storeAddrIds);
}
