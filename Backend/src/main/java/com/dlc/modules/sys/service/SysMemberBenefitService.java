package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益后台 Service(第15步)。
 * 课时账本仍由下单/预约/退款链路驱动,后台只允许批量调整权益的到期日、归属门店和所属服务人。
 *
 * @author claude
 */
public interface SysMemberBenefitService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 顶部统计卡:各状态数量 + 剩余/冻结课时合计(随 list 一并返回) */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 查询批量变更服务人的候选教练;候选结果受管理员门店数据权限约束。 */
    List<Map<String, Object>> queryCoachOptions(String keyword, String storeIds);

    /** 详情;查不到或不在 storeAddrIds 门店范围返回 null(controller 按 404 处理) */
    Map<String, Object> queryDetail(Long id, String storeIds);

    /** 按每条权益自己的 expire_at 增加/减少天数,操作前必须整批通过门店和状态校验。 */
    void batchAdjustExpireDate(List<Long> benefitIds, String operation, Integer days, String storeAddrIds);

    /** 批量变更权益未来使用门店,历史预约记录不跟随变更。 */
    void batchChangeStore(List<Long> benefitIds, Long targetStoreAddrId, String storeAddrIds);

    /** 批量变更权益所属服务人;不改订单销售归属和已生成预约的实际授课教练。 */
    void batchChangeCoach(List<Long> benefitIds, Long targetCoachId, String storeAddrIds);
}
