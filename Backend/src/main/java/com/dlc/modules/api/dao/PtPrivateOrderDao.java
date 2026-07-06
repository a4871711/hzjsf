package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.entity.PtProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教订单 Dao(pt_private_order),含下单校验所需的商品/门店/活动只读查询。
 * 营销活动(mk_group_buy_activity/mk_flash_sale_activity)本步只读查价,CRUD 在第17/18步营销域实现。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtPrivateOrderDao {

    PtPrivateOrderEntity queryObject(Long id);

    /** 按订单号查(detail 用,本人校验在 service 层做) */
    PtPrivateOrderEntity selectByOrderNo(@Param("orderNo") String orderNo);

    /** 新建待支付订单(回填自增 id;order_no 撞唯一键由调用方重试) */
    int save(PtPrivateOrderEntity entity);

    /**
     * 限购计数:该会员对该商品的已支付+待支付单数(order_status IN 0/1/2,不含已取消/已退款)。
     * 与 purchase_limit 比较;并发窗口下可能双过,回调侧库存条件 UPDATE 兜底。
     */
    int countMemberProductOrders(@Param("memberId") Long memberId, @Param("productId") Long productId);

    /** 活动限购计数:该会员在该活动下的已支付+待支付单数 */
    int countMemberActivityOrders(@Param("memberId") Long memberId,
                                  @Param("marketingType") Integer marketingType,
                                  @Param("activityId") Long activityId);

    /** 商品-门店适用关系计数(pt_product_store_rel),>0 才允许在该门店下单 */
    int countProductStore(@Param("productId") Long productId, @Param("storeId") Long storeId);

    /** 下单用商品查询:上架中(listing_status=1 且 deleted=0 且未到 unlisting_at),查不到=不可购买 */
    PtProduct selectProductForOrder(@Param("productId") Long productId);

    /**
     * 拼团活动只读查价:仅返回上架中且在时间窗内的活动
     * (列表/下单实时判 now BETWEEN start AND end AND status=1,不建过期任务——详细文档总则0.10)
     */
    Map<String, Object> selectGroupBuyActivity(@Param("activityId") Long activityId);

    /** 秒杀活动只读查价:同 selectGroupBuyActivity 口径 */
    Map<String, Object> selectFlashSaleActivity(@Param("activityId") Long activityId);

    /* ==================== 第13步 支付成功回调(updatePrivateOrder) ==================== */

    /** 行锁取订单(FOR UPDATE):串行化并发/重复回调,配合 service 层幂等闸1(order_status 前置判断) */
    PtPrivateOrderEntity selectByOrderNoForUpdate(@Param("orderNo") String orderNo);

    /** 一次性支付结清:0待支付→2已结清+pay_status=2+实收/支付/结清时间;WHERE order_status=0 与闸1双保险,0行=异常 */
    int settleOrder(@Param("id") Long id, @Param("paidAmount") BigDecimal paidAmount);

    /** 幂等闸3:sold_count+1 条件 UPDATE(sale_stock 为 NULL 不限量);0行=售罄,调用方抛异常回滚 */
    int increaseProductSoldCount(@Param("productId") Long productId);

    /** 拼团活动已售 CAS(activity_stock 为 NULL 不限量);0行=活动售罄 */
    int increaseGroupBuySoldCount(@Param("activityId") Long activityId);

    /** 秒杀活动已售 CAS(activity_stock 必填);0行=活动售罄 */
    int increaseFlashSaleSoldCount(@Param("activityId") Long activityId);

    /** 附赠团课权益规则(is_enabled=1 且 status=1 启用才发放),null=未配置/未启用 */
    Map<String, Object> selectGroupBenefitRule(@Param("productId") Long productId);

    /** 按来源订单判存:一单只发一次附赠团课权益(闸1外再兜一道) */
    int countMemberGroupBenefitByOrder(@Param("orderId") Long orderId);

    /** 发放会员附赠团课权益实例(effective=NOW,expire=NOW+validityDays,status=1生效中) */
    int insertMemberGroupBenefit(@Param("memberId") Long memberId, @Param("orderId") Long orderId,
                                 @Param("productId") Long productId, @Param("giftCount") Integer giftCount,
                                 @Param("validityDays") Integer validityDays);

    /** 取刚发放的权益实例 id(同事务内,写发放流水用) */
    Long selectMemberGroupBenefitIdByOrder(@Param("orderId") Long orderId);

    /** 发放流水:flow_type=1发放,biz_type=1私教订单 */
    int insertMemberGroupBenefitFlow(@Param("benefitId") Long benefitId, @Param("memberId") Long memberId,
                                     @Param("giftCount") Integer giftCount, @Param("orderId") Long orderId);

    /** 本人订单分页列表 */
    List<PtPrivateOrderEntity> queryMyOrders(Query query);

    int countMyOrders(Query query);

    /** 商品被订单引用计数(deleted=0 全状态);>0 商品不可删除(第14步回填第9步护栏) */
    int countByProduct(@Param("productId") Long productId);

    /* ==================== 第15步 后台退款冲减(refund) ==================== */

    /** 行锁取订单(退款用):FOR UPDATE 串行化并发退款,金额上限校验在锁内天然防重复提交 */
    PtPrivateOrderEntity selectByIdForUpdate(@Param("id") Long id);

    /**
     * 退款落账:CAS(order_status IN (1,2) AND refund_amount=旧值),0行=并发已变须回滚。
     * 全额退时传 orderStatus=4/payStatus=3;部分退传 null 保持原状态。
     */
    int updateRefund(@Param("id") Long id,
                     @Param("newRefundAmount") BigDecimal newRefundAmount,
                     @Param("oldRefundAmount") BigDecimal oldRefundAmount,
                     @Param("orderStatus") Integer orderStatus,
                     @Param("payStatus") Integer payStatus,
                     @Param("operatorId") Long operatorId);

    /* ==================== 第16步 定时任务:超时未付自动取消 ==================== */

    /** 超时待支付单扫描:order_status=0 且 created_at 早于 now-minutes 分钟(每小时任务用,minutes=30) */
    List<PtPrivateOrderEntity> queryTimeoutUnpaid(@Param("minutes") int minutes);

    /** 超时取消:0待支付→3已取消+cancel_at;WHERE order_status=0 幂等,0行=已被支付回调/他处处理,勿再释放券 */
    int cancelTimeoutOrder(@Param("id") Long id);

    /* ==================== 第20步 分期:首付激活/结清订单状态推进 ==================== */

    /**
     * 分期首付已付:0待支付→1首付已付/pay_status=1部分支付+首付实付+支付时间;
     * WHERE order_status=0 幂等(重复回调0行),paidAmount=首付额(累计已付以计划表为准)。
     */
    int markInstallmentPartPaid(@Param("id") Long id, @Param("paidAmount") BigDecimal paidAmount);

    /**
     * 分期全部付清:1首付已付→2已结清/pay_status=2已支付+累计实付+结清时间;
     * WHERE order_status=1 幂等(重复回调0行);paidAmount=计划已付总额。
     */
    int markInstallmentSettled(@Param("id") Long id, @Param("paidAmount") BigDecimal paidAmount);
}
