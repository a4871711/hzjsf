package com.dlc.modules.sys.dao;

import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.entity.PtProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教购买记录后台 Dao。查询、后台手工建单及关联数据删除均在此收口。
 * 退款仍委托 api 侧 PrivateOrderService.refund。
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysPrivateOrderDao {

    /** 分页列表:联 store 取门店名、联权益取课时四态/到期时间;门店隔离 storeIds 空=超管不过滤 */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 按列表同口径汇总订单数量与支付、退款金额。 */
    Map<String, Object> queryStats(Map<String, Object> params);

    /** 详情(含权益课时/门店名);storeIds 过滤在 SQL 内收口,越权/不存在返回 null → 404 */
    Map<String, Object> queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 券明细(pt_private_order_coupon_rel),一单一券 */
    Map<String, Object> queryCouponRel(@Param("orderId") Long orderId);

    /** 门店范围内订单判存(退款前越权校验):0=不存在或不在管辖门店 → 404 */
    int countInScope(@Param("id") Long id, @Param("storeIds") String storeIds);

    List<Map<String, Object>> queryMemberOptions(@Param("keyword") String keyword,
                                                 @Param("storeIds") String storeIds);

    List<Map<String, Object>> queryProductOptions(@Param("keyword") String keyword,
                                                  @Param("storeId") Long storeId,
                                                  @Param("storeIds") String storeIds);

    List<Map<String, Object>> queryCoachOptions(@Param("keyword") String keyword,
                                                @Param("productId") Long productId,
                                                @Param("storeId") Long storeId,
                                                @Param("storeIds") String storeIds);

    Map<String, Object> queryMemberSnapshot(@Param("memberId") Long memberId,
                                            @Param("storeIds") String storeIds);

    PtProduct queryProductSnapshot(@Param("productId") Long productId,
                                   @Param("storeId") Long storeId,
                                   @Param("storeIds") String storeIds);

    Long querySingleCoachId(@Param("productId") Long productId,
                            @Param("storeId") Long storeId);

    int countAvailableCoach(@Param("coachId") Long coachId,
                            @Param("productId") Long productId,
                            @Param("storeId") Long storeId);

    int saveManualOrder(PtPrivateOrderEntity entity);

    /** 锁内读取删除目标；门店越权与不存在统一返回 null。 */
    PtPrivateOrderEntity queryOrderForDelete(@Param("id") Long id,
                                             @Param("storeIds") String storeIds);

    /** 锁定来源订单下的全部赠课子订单，删除来源订单时先清子订单。 */
    List<PtPrivateOrderEntity> queryGiftOrdersForDelete(@Param("sourceOrderId") Long sourceOrderId);

    int restoreSourceBenefitLessons(@Param("benefitId") Long benefitId,
                                    @Param("lessonCount") Integer lessonCount);

    int deleteAppointments(@Param("orderId") Long orderId);

    int deleteCoachTradeDetails(@Param("orderNo") String orderNo);

    int deleteMemberGroupBenefitFlows(@Param("orderId") Long orderId);

    int deleteMemberGroupBenefits(@Param("orderId") Long orderId);

    int restoreMemberCoupon(@Param("orderId") Long orderId);

    int deleteCouponRel(@Param("orderId") Long orderId);

    int deleteIncomeDetails(@Param("orderId") Long orderId, @Param("orderNo") String orderNo);

    int deleteInstallmentBills(@Param("orderId") Long orderId);

    int deleteInstallmentPlan(@Param("orderId") Long orderId);

    int deletePrivateBenefit(@Param("orderId") Long orderId);

    int decreaseProductSoldCount(@Param("productId") Long productId);

    int decreaseGroupBuySoldCount(@Param("activityId") Long activityId);

    int decreaseFlashSaleSoldCount(@Param("activityId") Long activityId);

    int deleteOrder(@Param("id") Long id);
}
