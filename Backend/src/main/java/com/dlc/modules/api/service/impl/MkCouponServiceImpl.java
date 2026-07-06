package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.modules.api.dao.MkCouponApiDao;
import com.dlc.modules.api.dao.MkMemberCouponApiDao;
import com.dlc.modules.api.entity.MkCouponEntity;
import com.dlc.modules.api.entity.MkMemberCouponEntity;
import com.dlc.modules.api.service.MkCouponService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私教营销券服务实现(第18步)。落在 api.service.impl,命中 spring-jdbc.xml 事务切面(REQUIRED)。
 *
 * 命名规避:与旧 {@code CouponServiceImpl}/{@code ApiCouponController} 完全无关,专管 mk_coupon 体系。
 * 四状态(0未使用/3使用中/1已使用/2已过期)口径与 {@code PtPrivateOrderCouponRelDao}
 * (第12/13/16步占用/核销/释放 CAS)一致;占用/核销/释放动作不在本类重复实现,本类只做
 * 「适用性校验 + 抵扣试算 + 领券/查询」。抵扣一律 BigDecimal HALF_UP,≤ 订单应付。
 *
 * 新人券口径(§5.1):私教域「新用户」判定规则需求未定义,任务裁定「先不强拦、别臆造长期规则」。
 * 故本类对外暴露 isNewUser 参数(由调用方决定),内部 usableForOrder 传 isNewUser=true(不隐藏新人券);
 * 交易域下单同样传 true(见 PrivateOrderServiceImpl 券桩替换注释)。规则明确后仅需在调用方替换该实参,
 * 本类算法分支(is_new_user_coupon==1 && !isNewUser → 不可用)已就位,无需改动。
 *
 * @author claude
 */
@Service("mkCouponService")
public class MkCouponServiceImpl implements MkCouponService {

    /** 券类型:满减 */
    private static final int COUPON_TYPE_FULL_REDUCTION = 1;
    /** 适用范围:指定商品 */
    private static final int SCOPE_TYPE_SPECIFIED = 2;
    /** 新人券标记 */
    private static final int NEW_USER_COUPON_YES = 1;
    /** 券上架 */
    private static final int COUPON_STATUS_ON = 1;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MkCouponApiDao mkCouponApiDao;
    @Autowired
    private MkMemberCouponApiDao mkMemberCouponApiDao;

    /* ==================== 抵扣试算(§5.1),供交易域下单替换券桩调用 ==================== */

    @Override
    public BigDecimal calcCouponDiscount(Long memberCouponId, Long memberId, Long productId,
                                         Long storeId, BigDecimal baseAmount, boolean isNewUser) {
        if (memberCouponId == null || memberId == null || baseAmount == null) {
            return BigDecimal.ZERO;
        }
        // 会员券实例(联券模板带出 couponId/couponType 快照 + 折扣/门槛字段);查不到=不属本人 → 0
        MkMemberCouponEntity mc = mkMemberCouponApiDao.selectByIdWithCoupon(memberCouponId, memberId);
        if (mc == null || mc.getCouponId() == null) {
            return BigDecimal.ZERO;
        }
        // 券模板(以模板为准取算法字段;下架但已领仍可用 §5.1-2,故不校验 status)
        MkCouponEntity coupon = mkCouponApiDao.selectById(mc.getCouponId());
        if (coupon == null) {
            return BigDecimal.ZERO;
        }
        return computeDiscount(coupon, mc, productId, storeId, baseAmount, isNewUser);
    }

    /**
     * §5.1 抵扣核心:任一不满足(券态/过期/新人/门店/指定商品/门槛)→ 返回 0(视为该券不可用,不抵扣)。
     * memberCoupon 的 useStatus 已由 XML 实时算过期(0且已过期→2),故此处只放行真正 0未使用。
     */
    private BigDecimal computeDiscount(MkCouponEntity coupon, MkMemberCouponEntity mc, Long productId,
                                       Long storeId, BigDecimal baseAmount, boolean isNewUser) {
        // 1. 会员券有效性:仅 0未使用可用(3使用中/1已使用/2已过期均不可用;XML 已把「0且已过期」算成 2)
        if (mc.getUseStatus() == null || mc.getUseStatus() != 0) {
            return BigDecimal.ZERO;
        }
        // 2. 下架但已领的券仍可用到过期 → 不校验 coupon.status
        // 3. 新人券资格
        if (coupon.getIsNewUserCoupon() != null
                && coupon.getIsNewUserCoupon() == NEW_USER_COUPON_YES && !isNewUser) {
            return BigDecimal.ZERO;
        }
        // 4. 适用范围:指定商品需命中 product_rel
        if (coupon.getScopeType() != null && coupon.getScopeType() == SCOPE_TYPE_SPECIFIED) {
            if (productId == null || mkCouponApiDao.countProductApplicable(coupon.getId(), productId) <= 0) {
                return BigDecimal.ZERO;
            }
        }
        // 5. 适用门店:券无门店配置=不限;配了则购买门店须命中 rel(countStoreApplicable>0 放行)
        if (storeId != null && mkCouponApiDao.countStoreApplicable(coupon.getId(), storeId) <= 0) {
            return BigDecimal.ZERO;
        }
        // 6. 使用门槛
        BigDecimal threshold = coupon.getUseThresholdAmount() == null
                ? BigDecimal.ZERO : coupon.getUseThresholdAmount();
        if (baseAmount.compareTo(threshold) < 0) {
            return BigDecimal.ZERO;
        }
        // 7. 计算抵扣
        BigDecimal discount;
        if (coupon.getCouponType() != null && coupon.getCouponType() == COUPON_TYPE_FULL_REDUCTION) {
            // 满减券:直接抵 discount_amount
            discount = coupon.getDiscountAmount() == null ? BigDecimal.ZERO : coupon.getDiscountAmount();
        } else {
            // 折扣券:discount_rate=8.50 表示 8.5 折,抵扣 = 原价×(10-rate)/10,中间值不提前截断
            BigDecimal rate = coupon.getDiscountRate();
            if (rate == null) {
                return BigDecimal.ZERO;
            }
            discount = baseAmount.multiply(BigDecimal.TEN.subtract(rate))
                    .divide(BigDecimal.TEN, 10, RoundingMode.HALF_UP);
            // 封顶(非必填)
            if (coupon.getMaxDiscountAmount() != null
                    && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        }
        // 8. 抵扣不超过基准金额;分位 HALF_UP;恒 ≥ 0
        if (discount.compareTo(baseAmount) > 0) {
            discount = baseAmount;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    /* ==================== 移动端接口 ==================== */

    @Override
    public List<Map<String, Object>> canReceiveList(Long memberId, Long storeId) {
        // 新人券可见性同「先不强拦」口径:一律带出(newUser=true 不过滤新人券),前端展示,资格在领/用时再判
        List<MkCouponEntity> list = mkCouponApiDao.selectCanReceiveList(storeId, true);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (list == null) {
            return result;
        }
        for (MkCouponEntity c : list) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("couponId", c.getId());
            m.put("couponName", c.getCouponName());
            m.put("couponType", c.getCouponType());
            m.put("discountAmount", c.getDiscountAmount());
            m.put("discountRate", c.getDiscountRate());
            m.put("maxDiscountAmount", c.getMaxDiscountAmount());
            m.put("useThresholdAmount", c.getUseThresholdAmount());
            m.put("validDays", c.getValidDays());
            m.put("scopeType", c.getScopeType());
            m.put("isNewUserCoupon", c.getIsNewUserCoupon());
            result.add(m);
        }
        return result;
    }

    @Override
    public void receive(Long memberId, Long couponId) {
        if (memberId == null || couponId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 券必须存在 + 上架(deleted=0 已在 selectById 收口);下架券不可新领(已领的另说,由 calc 放行)
        MkCouponEntity coupon = mkCouponApiDao.selectById(couponId);
        if (coupon == null || coupon.getStatus() == null || coupon.getStatus() != COUPON_STATUS_ON) {
            throw new RRException(CodeAndMsg.ERROR_COUPON_INVALID);
        }
        // 本期允许重复领(§5.6:券是带 valid_days 的实例;无每人限领字段,前端二次确认防误点)。
        // 快照 coupon_name/coupon_type;expire_time=now+valid_days 由 XML 落库即算;use_status=0
        mkMemberCouponApiDao.receive(memberId, couponId, coupon.getCouponName(),
                coupon.getCouponType(), coupon.getValidDays());
    }

    @Override
    public List<Map<String, Object>> myList(Long memberId, Integer useStatus) {
        List<MkMemberCouponEntity> list = mkMemberCouponApiDao.myList(memberId, useStatus);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (list == null) {
            return result;
        }
        for (MkMemberCouponEntity mc : list) {
            result.add(toMemberCouponMap(mc, null));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> usableForOrder(UserInfoVo user, Long productId, Long storeId,
                                                    BigDecimal amount, Integer marketingType) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 活动单(拼团/秒杀)不叠加券 §5.4 → 直接返回空
        if (marketingType != null && marketingType != 0) {
            return result;
        }
        if (user == null || amount == null) {
            return result;
        }
        Long memberId = user.getUserId();
        // 候选:该会员未使用+未过期的券(门槛/门店/商品/新人由 §5.1 逐张判并预算抵扣)
        List<MkMemberCouponEntity> candidates = mkMemberCouponApiDao.selectUsableCandidates(memberId);
        if (candidates == null) {
            return result;
        }
        // 新人券口径:先不强拦(见类注释),isNewUser=true;规则明确后此处替换为真实判定
        boolean isNewUser = true;
        for (MkMemberCouponEntity mc : candidates) {
            if (mc.getCouponId() == null) {
                continue;
            }
            MkCouponEntity coupon = mkCouponApiDao.selectById(mc.getCouponId());
            if (coupon == null) {
                continue;
            }
            BigDecimal discount = computeDiscount(coupon, mc, productId, storeId, amount, isNewUser);
            // 只返回真正可用(抵扣>0)的券;抵扣=0 视为不适用/未达门槛,不进候选
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                result.add(toMemberCouponMap(mc, discount));
            }
        }
        return result;
    }

    /** 会员券行 → 前端展示 Map;calcDiscount 非空时附带试算抵扣(usableForOrder 用) */
    private Map<String, Object> toMemberCouponMap(MkMemberCouponEntity mc, BigDecimal calcDiscount) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("memberCouponId", mc.getId());
        m.put("couponId", mc.getCouponId());
        m.put("couponName", mc.getCouponName());
        m.put("couponType", mc.getCouponType());
        m.put("receiveTime", mc.getReceiveTime());
        m.put("expireTime", mc.getExpireTime());
        m.put("useStatus", mc.getUseStatus());
        m.put("usedOrderId", mc.getUsedOrderId());
        m.put("usedTime", mc.getUsedTime());
        m.put("discountAmount", mc.getDiscountAmount());
        m.put("discountRate", mc.getDiscountRate());
        m.put("maxDiscountAmount", mc.getMaxDiscountAmount());
        m.put("useThresholdAmount", mc.getUseThresholdAmount());
        if (calcDiscount != null) {
            m.put("calcDiscountAmount", calcDiscount);
        }
        return m;
    }
}
