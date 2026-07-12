package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;

/**
 * VIP 权益卡购买/持有 Service(移动端)
 */
public interface VipBenefitService {

    /**
     * 权益卡购买下单(仅微信):后端按当前 sold_count 重算价(不信前端),
     * 建待支付权益占位(status=9),返回 {orderNo, paySum} 供前端调小程序统一支付(/wx/proPay)。
     * storeId/storeAddrId 为前端随单传的"购买时所在门店"(可空,空则回退会员卡归属门店/nowStoreId)
     */
    Map<String, Object> buy(UserInfoVo user, Long vipCardId, Long storeId, Long storeAddrId);

    /**
     * 微信支付成功回调激活:单事务内 幂等激活 + 记账(用途=6) + sold_count+1。
     * 命中 0 行(重复回调/并发)直接幂等返回,返回 1 表示首次激活成功
     */
    int activateByOrderNo(String orderNo, BigDecimal money, String transactionNumber, Integer payType);

    /** 我的权益分页列表(默认查 status=0 正常) */
    PageUtils myBenefits(Map<String, Object> params);

    /** 该用户是否为权益会员(名下有正常且未过期的权益);userId 为 null 时返回 false */
    boolean hasValidBenefit(Long userId);

    /** 该用户最新一张有效权益(与 hasValidBenefit 同口径);无/未登录返回 null */
    VipBenefit latestValidBenefit(Long userId);
}
