package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益后台 Service(第15步):纯查询页,无 save/update/delete、无特殊动作。
 * 课时变更只能由下单/预约/退款链路驱动,禁止后台直接改账本。
 *
 * @author claude
 */
public interface SysMemberBenefitService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 顶部统计卡:各状态数量 + 剩余/冻结课时合计(随 list 一并返回) */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 详情;查不到或不在 storeIds 门店范围返回 null(controller 按 404 处理) */
    Map<String, Object> queryDetail(Long id, String storeIds);
}
