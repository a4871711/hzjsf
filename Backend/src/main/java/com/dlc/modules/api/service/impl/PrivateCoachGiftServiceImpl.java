package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.dao.PrivateCoachGiftDao;
import com.dlc.modules.api.dao.PtMemberPrivateBenefitDao;
import com.dlc.modules.api.dao.PtPrivateOrderDao;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.PrivateCoachGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自由教练赠课事务。
 * 锁顺序固定为来源订单行 -> 来源权益行，和现有退款顺序保持一致，避免同一订单赠课/退款互锁。
 */
@Service("privateCoachGiftService")
public class PrivateCoachGiftServiceImpl implements PrivateCoachGiftService {

    private static final int COACH_TYPE_FREELANCE = 2;
    private static final long LESSON_PACKAGE_TYPE_ID = 2L;

    @Autowired
    private PrivateCoachGiftDao privateCoachGiftDao;
    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private PtMemberPrivateBenefitDao ptMemberPrivateBenefitDao;
    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public List<Map<String, Object>> giftableBenefits(Long userId) {
        requireFreelanceCoach(userId);
        return privateCoachGiftDao.queryGiftableBenefits(userId);
    }

    @Override
    public Map<String, Object> lookupMember(Long userId, String keyword) {
        requireFreelanceCoach(userId);
        String normalized = keyword == null ? "" : keyword.trim();
        if (normalized.isEmpty() || normalized.length() > 32) {
            throw new RRException("请输入完整会员ID或手机号");
        }
        Map<String, Object> member = privateCoachGiftDao.queryMemberExact(normalized);
        if (member == null) {
            throw new RRException("未找到对应会员");
        }
        Long memberId = numberToLong(member.get("userId"));
        if (userId.equals(memberId)) {
            throw new RRException("不能给自己赠送课时");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("userId", memberId);
        result.put("nickname", member.get("nickname"));
        result.put("mobile", maskMobile(member.get("phone")));
        result.put("headImgUrl", member.get("headImgUrl"));
        result.put("storeName", member.get("storeName"));
        return result;
    }

    @Override
    public Map<String, Object> gift(Long userId, Long sourceBenefitId, Long toMemberId,
                                    Integer lessonCount, String requestNo) {
        Map<String, Object> coach = requireFreelanceCoach(userId);
        validateGiftParams(sourceBenefitId, toMemberId, lessonCount, requestNo);
        if (userId.equals(toMemberId)) {
            throw new RRException("不能给自己赠送课时");
        }

        String normalizedRequestNo = requestNo.trim();
        PtPrivateOrderEntity existing = ptPrivateOrderDao.selectByGiftRequestNo(normalizedRequestNo);
        if (existing != null) {
            return existingResult(existing, userId, sourceBenefitId, toMemberId, lessonCount);
        }

        PtMemberPrivateBenefitEntity snapshot = ptMemberPrivateBenefitDao.queryObject(sourceBenefitId);
        if (snapshot == null || snapshot.getOrderId() == null) {
            throw new RRException("来源权益不存在");
        }

        // 必须先锁订单再锁权益；锁内重新核对全部可变字段。
        PtPrivateOrderEntity sourceOrder = ptPrivateOrderDao.selectByIdForUpdate(snapshot.getOrderId());
        PtMemberPrivateBenefitEntity sourceBenefit = ptMemberPrivateBenefitDao.selectForUpdate(sourceBenefitId);
        existing = ptPrivateOrderDao.selectByGiftRequestNoForUpdate(normalizedRequestNo);
        if (existing != null) {
            return existingResult(existing, userId, sourceBenefitId, toMemberId, lessonCount);
        }

        validateSource(userId, sourceOrder, sourceBenefit, sourceBenefitId, lessonCount);
        UserInfo target = userInfoMapper.selectByPrimaryKey(toMemberId);
        if (target == null) {
            throw new RRException("受赠会员不存在或已停用");
        }

        Long coachId = numberToLong(coach.get("id"));
        Date now = new Date();
        PtPrivateOrderEntity giftOrder = buildGiftOrder(sourceOrder, sourceBenefit, target, coachId,
                lessonCount, normalizedRequestNo, now, userId);
        saveGiftOrder(giftOrder);

        if (ptMemberPrivateBenefitDao.giftDeductLessons(sourceBenefitId, lessonCount) == 0) {
            throw new RRException("可赠课时不足，请刷新后重试");
        }
        ptMemberPrivateBenefitDao.markUsedUp(sourceBenefitId);

        PtMemberPrivateBenefitEntity targetBenefit = buildTargetBenefit(giftOrder, sourceBenefit,
                toMemberId, coachId, lessonCount, now);
        saveTargetBenefit(targetBenefit);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("giftOrderNo", giftOrder.getOrderNo());
        result.put("targetBenefitId", targetBenefit.getId());
        result.put("remainingLessons", sourceBenefit.getRemainingLessons() - lessonCount);
        return result;
    }

    @Override
    public PageUtils history(Long userId, Map<String, Object> params) {
        requireFreelanceCoach(userId);
        params.put("userId", userId);
        Query query = new Query(params);
        List<Map<String, Object>> list = privateCoachGiftDao.queryGiftHistory(query);
        return new PageUtils(list, privateCoachGiftDao.countGiftHistory(query), query.getLimit(), query.getPage());
    }

    private Map<String, Object> requireFreelanceCoach(Long userId) {
        if (userId == null) {
            throw new RRException("登录状态已失效");
        }
        Map<String, Object> coach = coachApiDao.queryBoundCoachByUserId(userId);
        if (coach == null) {
            throw new RRException("当前账号未绑定正常状态的教练");
        }
        Object type = coach.get("coachType");
        if (!(type instanceof Number) || ((Number) type).intValue() != COACH_TYPE_FREELANCE) {
            throw new RRException("仅自由教练可使用赠课功能");
        }
        return coach;
    }

    private void validateGiftParams(Long sourceBenefitId, Long toMemberId, Integer lessonCount, String requestNo) {
        if (sourceBenefitId == null || toMemberId == null || lessonCount == null || lessonCount <= 0) {
            throw new RRException("来源权益、受赠会员和赠送课时必须填写");
        }
        String normalized = requestNo == null ? "" : requestNo.trim();
        if (!normalized.matches("^[A-Za-z0-9_-]{16,64}$")) {
            throw new RRException("requestNo格式非法");
        }
    }

    private void validateSource(Long userId, PtPrivateOrderEntity order,
                                PtMemberPrivateBenefitEntity benefit, Long sourceBenefitId, int lessonCount) {
        if (order == null || benefit == null || !sourceBenefitId.equals(benefit.getId())
                || !order.getId().equals(benefit.getOrderId())) {
            throw new RRException("来源订单或权益不存在");
        }
        if (!userId.equals(order.getMemberId()) || !userId.equals(benefit.getMemberId())) {
            throw new RRException("只能赠送自己购买的私教权益");
        }
        if (!Integer.valueOf(0).equals(order.getOrderSource())) {
            throw new RRException("受赠权益不能再次转赠");
        }
        if (!Integer.valueOf(2).equals(order.getPayStatus()) || !Integer.valueOf(2).equals(order.getOrderStatus())) {
            throw new RRException("来源订单尚未结清");
        }
        if (!Long.valueOf(LESSON_PACKAGE_TYPE_ID).equals(order.getProductTypeId())) {
            throw new RRException("仅节次套餐可以赠送");
        }
        if (!Integer.valueOf(1).equals(benefit.getStatus())) {
            throw new RRException("来源权益状态不可赠送");
        }
        if (benefit.getExpireAt() != null && !benefit.getExpireAt().after(new Date())) {
            throw new RRException("来源权益已过期");
        }
        if (benefit.getRemainingLessons() == null || benefit.getRemainingLessons() < lessonCount) {
            throw new RRException("可赠课时不足");
        }
    }

    private PtPrivateOrderEntity buildGiftOrder(PtPrivateOrderEntity sourceOrder,
                                                 PtMemberPrivateBenefitEntity sourceBenefit,
                                                 UserInfo target, Long coachId, int lessonCount,
                                                 String requestNo, Date now, Long operatorId) {
        PtPrivateOrderEntity order = new PtPrivateOrderEntity();
        order.setOrderNo(genOrderNo());
        order.setMemberId(target.getUserId());
        order.setMemberName(target.getNickname());
        order.setMemberMobile(target.getPhone());
        order.setProductId(sourceOrder.getProductId());
        order.setProductName(sourceOrder.getProductName());
        order.setProductTypeId(sourceOrder.getProductTypeId());
        order.setProductTypeName(sourceOrder.getProductTypeName());
        order.setServiceType(sourceOrder.getServiceType());
        order.setStoreId(sourceBenefit.getStoreId());
        order.setCoachId(coachId);
        order.setLessonCount(lessonCount);
        order.setDurationMinutes(sourceOrder.getDurationMinutes());
        order.setValidityDays(sourceOrder.getValidityDays());
        order.setOriginalAmount(BigDecimal.ZERO);
        order.setPayableAmount(BigDecimal.ZERO);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setRefundAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setCouponDiscountAmount(BigDecimal.ZERO);
        order.setMarketingType(0);
        order.setPayMethod(9);
        order.setPayStatus(2);
        order.setOrderStatus(2);
        order.setOrderSource(1);
        order.setSourceOrderId(sourceOrder.getId());
        order.setSourceBenefitId(sourceBenefit.getId());
        order.setGiftRequestNo(requestNo);
        order.setRemark("赠送");
        order.setPaidAt(now);
        order.setSettledAt(now);
        order.setCreatedBy(operatorId);
        order.setUpdatedBy(operatorId);
        return order;
    }

    private void saveGiftOrder(PtPrivateOrderEntity order) {
        try {
            ptPrivateOrderDao.saveGift(order);
        } catch (DuplicateKeyException e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (!message.contains("uk_pt_private_order_no")) {
                throw new RRException("赠课请求号已被使用，请勿重复提交");
            }
            order.setOrderNo(genOrderNo());
            ptPrivateOrderDao.saveGift(order);
        }
    }

    private PtMemberPrivateBenefitEntity buildTargetBenefit(PtPrivateOrderEntity order,
                                                              PtMemberPrivateBenefitEntity source,
                                                              Long memberId, Long coachId,
                                                              int lessonCount, Date now) {
        PtMemberPrivateBenefitEntity benefit = new PtMemberPrivateBenefitEntity();
        benefit.setBenefitNo(genBenefitNo());
        benefit.setOrderId(order.getId());
        benefit.setMemberId(memberId);
        benefit.setProductId(source.getProductId());
        benefit.setStoreId(source.getStoreId());
        benefit.setCoachId(coachId);
        benefit.setTotalLessons(lessonCount);
        benefit.setUsedLessons(0);
        benefit.setFrozenLessons(0);
        benefit.setRemainingLessons(lessonCount);
        benefit.setEffectiveAt(now);
        // 赠送不能延长有效期，必须原样继承来源权益的绝对到期时间。
        benefit.setExpireAt(source.getExpireAt());
        benefit.setStatus(1);
        return benefit;
    }

    private void saveTargetBenefit(PtMemberPrivateBenefitEntity benefit) {
        try {
            ptMemberPrivateBenefitDao.save(benefit);
        } catch (DuplicateKeyException e) {
            benefit.setBenefitNo(genBenefitNo());
            ptMemberPrivateBenefitDao.save(benefit);
        }
    }

    private Map<String, Object> existingResult(PtPrivateOrderEntity existing, Long userId,
                                                Long sourceBenefitId, Long toMemberId, int lessonCount) {
        if (!userId.equals(existing.getCreatedBy())
                || !sourceBenefitId.equals(existing.getSourceBenefitId())
                || !toMemberId.equals(existing.getMemberId())
                || !Integer.valueOf(lessonCount).equals(existing.getLessonCount())) {
            throw new RRException("requestNo已用于其他赠课请求");
        }
        PtMemberPrivateBenefitEntity target = ptMemberPrivateBenefitDao.selectByOrderId(existing.getId());
        PtMemberPrivateBenefitEntity source = ptMemberPrivateBenefitDao.queryObject(sourceBenefitId);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("giftOrderNo", existing.getOrderNo());
        result.put("targetBenefitId", target == null ? null : target.getId());
        result.put("remainingLessons", source == null ? null : source.getRemainingLessons());
        return result;
    }

    private Long numberToLong(Object value) {
        if (!(value instanceof Number)) {
            throw new RRException("教练或会员数据异常");
        }
        return ((Number) value).longValue();
    }

    private String maskMobile(Object value) {
        String mobile = value == null ? "" : String.valueOf(value);
        if (mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    private String genOrderNo() {
        return "PT" + OrderNoGenerator.getOrderIdByTime() + ConfigConstant.PT_PRIVATE_ORDER_TYPE;
    }

    private String genBenefitNo() {
        return "PE" + OrderNoGenerator.getOrderIdByTime();
    }
}
