package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysCardBenefitService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 会员卡权益聚合视图 Service 实现——本期占位，返回空列表。
 *
 * 【后续迭代】会员卡权益无独立表（详细实现文档营销域 §1.3，不建 card_benefit* 表）：
 * - 「优惠券」类权益 → mk_coupon（券模板）+ mk_member_coupon（发放），配置入口在优惠券页；
 * - 「团课次数」类权益 → pt_product_group_benefit* / pt_member_group_benefit*（交易/运营域），
 *   配置入口在私教商品表单「附赠团课权益」；
 * - 「VIP 权益」类 → vip_benefit_card / vip_benefit（VIP 权益卡域），配置入口在 VIP 权益卡页。
 * 后续迭代在此按会员卡维度 UNION 聚合三类权益做只读展示（编辑跳转对应域配置页，不在本域落库）。
 *
 * @author claude
 */
@Service("sysCardBenefitService")
public class SysCardBenefitServiceImpl implements SysCardBenefitService {

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> map) {
        return Collections.emptyList();
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return 0;
    }
}
