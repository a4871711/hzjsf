package com.dlc.modules.api.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 会员端限时秒杀 Dao（多商品版）。活动 mk_flash_sale_activity + 商品 mk_flash_sale_product
 * + 每日时段 mk_flash_sale_time_slot + 每日已售 mk_flash_sale_daily_sold。
 * 循环生效「今天生效日 + 命中时段」的判定放在 Service（Java）里做；这里只出数据 + CAS 扣减。
 * 注意：api 目录 Mapper XML 改动须重启 Tomcat。
 *
 * @author claude
 */
@Mapper
@Repository
public interface ApiFlashSaleDao {

    /** 首页候选商品卡：status=1 且（单次:未结束24h内 / 循环:今天在周期内）的每个商品一行，含活动投放字段；storeAddrId 非空按适用门店过滤 */
    List<Map<String, Object>> queryCurrentProductCards(@Param("storeAddrId") Long storeAddrId);

    /** 某活动的每日投放时段（循环生效判活用） */
    List<Map<String, Object>> queryTimeSlots(@Param("activityId") Long activityId);

    /** 下单校验：取活动+商品（status=1、未删），不含时间过滤，活跃判定与库存在 Service 做 */
    Map<String, Object> selectActivityProduct(@Param("activityId") Long activityId, @Param("productId") Long productId);

    /** 取活动库存方式(1每日/2总量)，回调 CAS 决定扣哪套（不限活动状态） */
    Integer queryStockMode(@Param("activityId") Long activityId);

    /** 总量库存 CAS：sold_count+1 且 < activity_stock；0行=售罄 */
    int increaseTotalSold(@Param("activityId") Long activityId, @Param("productId") Long productId);

    /** 纯统计累加(无库存上限条件)：每日库存模式扣完当日额度后同步累计已售 */
    int increaseTotalSoldStat(@Param("activityId") Long activityId, @Param("productId") Long productId);

    /** 每日库存：先确保当天行存在（INSERT IGNORE，sold=0） */
    int ensureDailyRow(@Param("activityId") Long activityId, @Param("productId") Long productId, @Param("soldDate") String soldDate);

    /** 每日库存 CAS：当天 sold+1 且 < 商品 activity_stock(每日投放量)；0行=当日售罄 */
    int increaseDailySold(@Param("activityId") Long activityId, @Param("productId") Long productId, @Param("soldDate") String soldDate);

    /** 查某商品某天已售（0/无=当天未售） */
    Integer queryDailySold(@Param("activityId") Long activityId, @Param("productId") Long productId, @Param("soldDate") String soldDate);

    /** 会员卡秒杀「单商品」限购计数：user×activity×fitCard，未取消/退款的订单数(card_order.status IN 0..4) */
    int countMemberCardFlashOrders(@Param("userId") Long userId, @Param("activityId") Long activityId, @Param("productId") Long productId);

    /** 权益卡秒杀「单商品」限购计数：user×activity×vipCard(排除待支付9,避免占位单堵住重试) */
    int countMemberBenefitFlashOrders(@Param("userId") Long userId, @Param("activityId") Long activityId, @Param("productId") Long productId);
}
