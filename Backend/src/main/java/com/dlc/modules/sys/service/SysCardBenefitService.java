package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 会员卡权益聚合视图 Service（无独立表，详见详细实现文档营销域 §1.3）。
 * 聚合 VIP 权益卡（vip_benefit_card）/ 附赠团课（pt_product_group_benefit*）/ 优惠券（mk_coupon）
 * 三类权益按会员卡维度展示——本期占位返回空列表，聚合查询留待后续迭代。
 *
 * @author claude
 */
public interface SysCardBenefitService {

    List<Map<String, Object>> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);
}
