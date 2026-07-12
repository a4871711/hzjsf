package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 权益会员(vip_benefit)后台管理:列表 + 停用/启用、备注、更新有效期、更换门店、注销。
 *
 * @date 2026-07-12
 */
public interface SysVipMemberService {

    List<Map<String, Object>> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    /** 停用(0正常→2已冻结)或启用(2→0);当前状态不允许时抛 RRException */
    void freeze(Long vipBenefitId, boolean disable);

    void updateRemark(Long vipBenefitId, String remark);

    /** 改到期时间(yyyy-MM-dd HH:mm:ss);3已过期且新时间在未来自动回 0正常 */
    void updateValidity(Long vipBenefitId, String expireTime);

    /** 更换开通门店(store_addr_id + store_id 联动) */
    void changeStore(Long vipBenefitId, Long storeAddrId);

    /** 注销:0/2/3 → 4已注销(终态);待支付/已转出不允许 */
    void cancel(Long vipBenefitId);
}
