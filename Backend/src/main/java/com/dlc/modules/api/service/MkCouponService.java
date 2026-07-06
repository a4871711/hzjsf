package com.dlc.modules.api.service;

import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教营销券(mk_coupon/mk_member_coupon)移动端服务(第18步)。
 *
 * 命名规避:项目已有 2018 年旧 {@code CouponService}(Coupon/user_coupon 体系)与
 * {@code ApiCouponController}(占用 /api/coupon),两者不可动;本服务专管私教营销券,
 * 类名统一 Mk 前缀(MkCouponService/MkCouponServiceImpl,@Service),controller 走 /api/mkCoupon。
 *
 * 四状态口径与既有 {@code PtPrivateOrderCouponRelDao}(第12/13/16步 占用/核销/释放 CAS)完全一致:
 * use_status 0未使用 / 3使用中(下单占用态) / 1已使用 / 2已过期。本服务只负责
 * 「适用性校验 + 抵扣试算(§5.1)+ 领券/我的券/可用券查询」,占用/核销/释放的 CAS 动作仍归那套 dao,
 * 不搞两套。抵扣算法一律 {@link BigDecimal} + HALF_UP,抵扣额 ≤ 订单应付。
 *
 * @author claude
 */
public interface MkCouponService {

    /**
     * 券抵扣试算(§5.1 满减/折扣封顶/门槛/新人/门店/指定商品)。供交易域下单(PrivateOrderServiceImpl)
     * 替换券桩调用:券不可用/不适用/未达门槛 → 返回 0(下单动作不因此中断,券仍会被占用并落 0 抵扣明细)。
     * 服务内部按 memberCouponId 取会员券+联券模板,不需调用方预取模板字段。
     *
     * @param memberCouponId 会员券实例 id
     * @param memberId       会员 id(校验券归属 + 新人资格)
     * @param productId      下单商品 id(scope_type=2 指定商品校验)
     * @param storeId        购买门店 id(门店适用校验)
     * @param baseAmount     基准应付金额(普通单口径,不含活动价)
     * @param isNewUser      是否新人(新人券资格,交易域判定后传入;新人判定口径本期见 impl 注释)
     * @return 抵扣金额(2 位 HALF_UP,恒 ≥ 0 且 ≤ baseAmount);券不可用返回 0
     */
    BigDecimal calcCouponDiscount(Long memberCouponId, Long memberId, Long productId,
                                  Long storeId, BigDecimal baseAmount, boolean isNewUser);

    /**
     * 可领券列表(/api/mkCoupon/canReceiveList):status=1 上架且门店匹配的券;非新人不返回新人券。
     * 每张标记 received(该会员是否已领过,仅提示,本期允许重复领 §5.6)。
     */
    List<Map<String, Object>> canReceiveList(Long memberId, Long storeId);

    /**
     * 领券(/api/mkCoupon/receive §5.6):校验券 status=1 上架+未删,写 mk_member_coupon
     * (快照 coupon_name/coupon_type,expire_time=now+valid_days,use_status=0)。本期允许重复领。
     * 券不存在/已下架 → 抛 ERROR_COUPON_INVALID。
     */
    void receive(Long memberId, Long couponId);

    /**
     * 我的券(/api/mkCoupon/myList):按 use_status 过滤(为空则全部);
     * use_status=0 且已过期的实时归入「已过期(2)」,与过期任务落库口径一致。
     */
    List<Map<String, Object>> myList(Long memberId, Integer useStatus);

    /**
     * 下单可用券(/api/mkCoupon/usableForOrder §5.1):传商品/门店/金额,返回该会员可用券 + 每张试算抵扣。
     * 活动单(marketingType≠0)按「活动价不叠加券 §5.4」返回空列表。
     *
     * @param marketingType 营销类型(0普通 1拼团 2秒杀;非 0 直接返回空)
     */
    List<Map<String, Object>> usableForOrder(UserInfoVo user, Long productId, Long storeId,
                                             BigDecimal amount, Integer marketingType);
}
