package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
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
     * 建待支付权益占位(status=9),返回 {orderNo, paySum} 供前端调小程序统一支付(/wx/proPay)
     */
    Map<String, Object> buy(UserInfoVo user, Long vipCardId);

    /**
     * 微信支付成功回调激活:单事务内 幂等激活 + 记账(用途=6) + sold_count+1。
     * 命中 0 行(重复回调/并发)直接幂等返回,返回 1 表示首次激活成功
     */
    int activateByOrderNo(String orderNo, BigDecimal money, String transactionNumber, Integer payType);

    /** 我的权益分页列表(默认查 status=0 正常) */
    PageUtils myBenefits(Map<String, Object> params);

    /**
     * 随购卡单加购的权益卡激活:按订单号查权益占位(普通订单无占位,幂等跳过),
     * 幂等激活(status 9→0)并 sold_count+1;整单金额已按购卡记账,此处不重复记流水
     */
    int activateAttached(String orderNo);
}
