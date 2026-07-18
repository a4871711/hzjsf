package com.dlc.modules.api.service;

import java.util.List;
import java.util.Map;

/**
 * 会员端限时秒杀 Service（多商品版）。循环生效「今天生效日 + 命中时段」判定集中在此。
 *
 * @author claude
 */
public interface ApiFlashSaleService {

    /**
     * 首页当前秒杀卡片（每个在投商品一张卡）：含 status(preheat/ongoing/soldout)、serverTime、startTime/endTime(当前窗口)。
     * storeAddrId 非空时按商品适用门店过滤（适用门店为空=通店不过滤）。
     */
    List<Map<String, Object>> queryCurrentCards(Long storeAddrId);

    /**
     * 下单可买校验：命中活动+商品且当前处于可抢时段、有剩余库存则返回活动+商品信息
     * （flashSalePrice/purchaseLimit/bizType/...）；否则抛 RRException。
     */
    Map<String, Object> checkBuyable(Long activityId, Long productId);

    /**
     * 支付回调 CAS 扣秒杀库存：库存方式=总量则扣 mk_flash_sale_product.sold_count，
     * =每日则按 orderTime 所在日扣 mk_flash_sale_daily_sold（跨零点回调仍归下单当天），
     * 并同步累加 sold_count 作累计统计。orderTime 传订单创建时间，null 退化为当前时间。
     * 返回受影响行数（>0 成功，0=已售罄/失效）。
     */
    int increaseSold(Long activityId, Long productId, java.util.Date orderTime);
}
