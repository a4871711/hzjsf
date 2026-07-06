package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtPrivateOrderCouponRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 私教订单优惠券明细 Dao(pt_private_order_coupon_rel),并代管下单时对
 * mk_member_coupon 的占用 CAS(第18步营销域移动端落地后此部分可迁移到券 Dao)。
 *
 * 券占用状态机(详细文档 §5.4/§9-7):下单置「使用中」防同券并发下两单;
 * 支付成功回调(第13步)置 1已使用+写 used_time;超时/取消(第16步)释放回 0未使用。
 * DDL use_status 枚举只定义了 0未使用/1已使用/2已过期,无「使用中」值,
 * 按任务裁定取 use_status=3 表示「使用中」(占用态)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtPrivateOrderCouponRelDao {

    /** 写券使用明细(与订单同事务;order_id 唯一键约束一单一券) */
    int save(PtPrivateOrderCouponRelEntity entity);

    /** 按订单查券明细(detail 用) */
    PtPrivateOrderCouponRelEntity selectByOrderId(@Param("orderId") Long orderId);

    /** 查可用会员券:券属本人/未使用/未过期在 SQL 内收口,查不到返回 null(=券不可用) */
    Map<String, Object> selectMemberCoupon(@Param("memberCouponId") Long memberCouponId,
                                           @Param("memberId") Long memberId);

    /**
     * 占用会员券:条件 CAS,use_status 0未使用 → 3使用中,并记 used_order_id。
     * 影响 0 行=券不属本人/已被用/已过期/已被并发抢占,调用方按 ERROR_COUPON_INVALID 处理并回滚。
     */
    int occupyMemberCoupon(@Param("memberCouponId") Long memberCouponId,
                           @Param("memberId") Long memberId,
                           @Param("orderId") Long orderId);

    /**
     * 券核销(第13步支付回调):条件 CAS,use_status 3使用中 → 1已使用,并写 used_time。
     * WHERE 校验占用单=本单,防错核他单占用;影响 0 行=占用已被释放/状态漂移,
     * 调用方记日志不阻断收款主流程。
     */
    int useMemberCoupon(@Param("memberCouponId") Long memberCouponId,
                        @Param("orderId") Long orderId);

    /**
     * 释放占用券(第16步超时取消):条件 CAS,use_status 3使用中 → 0未使用,并清 used_order_id。
     * WHERE 校验占用单=本单,防误放他单占用;影响 0 行=券已被核销/释放,调用方记日志即可。
     */
    int releaseMemberCoupon(@Param("memberCouponId") Long memberCouponId,
                            @Param("orderId") Long orderId);
}
