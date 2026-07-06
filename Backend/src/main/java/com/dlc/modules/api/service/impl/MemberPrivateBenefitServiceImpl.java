package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.modules.api.dao.PtMemberPrivateBenefitDao;
import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.service.MemberPrivateBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 会员私教权益课时账本三态机实现。
 * 落在 api.service.impl,命中 spring-jdbc.xml 事务切面(REQUIRED),一个方法一个事务;
 * 与预约/订单状态机在同一调用事务内联动(调用方注入本 bean 直接调用即可)。
 */
@Service("memberPrivateBenefitService")
public class MemberPrivateBenefitServiceImpl implements MemberPrivateBenefitService {

    @Autowired
    private PtMemberPrivateBenefitDao ptMemberPrivateBenefitDao;

    @Override
    public PtMemberPrivateBenefitEntity activate(Long orderId, Long memberId, Long productId,
                                                 Long storeId, Integer lessonCount, Integer validityDays) {
        if (orderId == null || memberId == null || productId == null || storeId == null
                || lessonCount == null || lessonCount <= 0
                || validityDays == null || (validityDays != -1 && validityDays <= 0)) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 幂等:一单一权益,按 order_id 查重(回调侧已对订单行 FOR UPDATE 串行化)
        PtMemberPrivateBenefitEntity exist = ptMemberPrivateBenefitDao.selectByOrderId(orderId);
        if (exist != null) {
            return exist;
        }
        Date now = new Date();
        PtMemberPrivateBenefitEntity benefit = new PtMemberPrivateBenefitEntity();
        benefit.setBenefitNo(genBenefitNo());
        benefit.setOrderId(orderId);
        benefit.setMemberId(memberId);
        benefit.setProductId(productId);
        benefit.setStoreId(storeId);
        benefit.setTotalLessons(lessonCount);
        benefit.setUsedLessons(0);
        benefit.setFrozenLessons(0);
        benefit.setRemainingLessons(lessonCount);
        benefit.setEffectiveAt(now);
        benefit.setExpireAt(calcExpireAt(now, validityDays));
        benefit.setStatus(1);
        try {
            ptMemberPrivateBenefitDao.save(benefit);
        } catch (DuplicateKeyException e) {
            // 唯一键仅 benefit_no:撞键重试1次(全局单号约定)
            benefit.setBenefitNo(genBenefitNo());
            ptMemberPrivateBenefitDao.save(benefit);
        }
        return benefit;
    }

    @Override
    public void freeze(Long benefitId, int n) {
        PtMemberPrivateBenefitEntity benefit = lockAndGet(benefitId, n);
        if (benefit.getStatus() == null || benefit.getStatus() != 1) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_STATUS_ABNORMAL);
        }
        // 过期实时判断,不依赖定时任务(锁内判定,同事务内不会漂移)
        if (benefit.getExpireAt() != null && !benefit.getExpireAt().after(new Date())) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_EXPIRED);
        }
        if (benefit.getRemainingLessons() == null || benefit.getRemainingLessons() < n) {
            throw new RRException(CodeAndMsg.ERROR_LESSON_NOT_ENOUGH);
        }
        // 条件 UPDATE 乐观护栏兜底(status=1 AND remaining>=n)
        if (ptMemberPrivateBenefitDao.freezeLessons(benefitId, n) == 0) {
            throw new RRException(CodeAndMsg.ERROR_LESSON_NOT_ENOUGH);
        }
    }

    @Override
    public void finish(Long benefitId, int n) {
        PtMemberPrivateBenefitEntity benefit = lockAndGet(benefitId, n);
        // 冻结量不足=账本与预约状态机不一致(预约层幂等应已挡掉重复核销)
        if (benefit.getFrozenLessons() == null || benefit.getFrozenLessons() < n
                || ptMemberPrivateBenefitDao.finishLessons(benefitId, n) == 0) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_STATUS_ABNORMAL);
        }
        // 同事务收口:remaining=0 且 frozen=0 → 2已用完(条件不满足时 0 行,无需判断)
        ptMemberPrivateBenefitDao.markUsedUp(benefitId);
    }

    @Override
    public void cancel(Long benefitId, int n) {
        PtMemberPrivateBenefitEntity benefit = lockAndGet(benefitId, n);
        if (benefit.getFrozenLessons() == null || benefit.getFrozenLessons() < n
                || ptMemberPrivateBenefitDao.cancelLessons(benefitId, n) == 0) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_STATUS_ABNORMAL);
        }
    }

    @Override
    public void refundDeduct(Long benefitId, int n) {
        PtMemberPrivateBenefitEntity benefit = lockAndGet(benefitId, n);
        // 只允许冲剩余:冻结(进行中预约)与已用不可冲
        if (benefit.getRemainingLessons() == null || benefit.getRemainingLessons() < n
                || ptMemberPrivateBenefitDao.refundDeductLessons(benefitId, n) == 0) {
            throw new RRException(CodeAndMsg.ERROR_LESSON_NOT_ENOUGH);
        }
        // 同事务收口:冲减后 remaining=0 且 frozen=0 → 4已退款(仍有冻结则待预约完结后收口)
        ptMemberPrivateBenefitDao.markRefunded(benefitId);
    }

    /** 公共前置:参数护栏 + 行锁 + 存在性校验 */
    private PtMemberPrivateBenefitEntity lockAndGet(Long benefitId, int n) {
        if (benefitId == null || n <= 0) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtMemberPrivateBenefitEntity benefit = ptMemberPrivateBenefitDao.selectForUpdate(benefitId);
        if (benefit == null) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_NOT_EXIST);
        }
        return benefit;
    }

    /** 权益编号:PE + yyyyMMddHHmmss + 随机(全局单号约定 §0.6.2) */
    private String genBenefitNo() {
        return "PE" + OrderNoGenerator.getOrderIdByTime();
    }

    /** 到期时间:validityDays=-1 长期为 NULL,否则生效时间 + validityDays 天 */
    private Date calcExpireAt(Date effectiveAt, Integer validityDays) {
        if (validityDays == -1) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(effectiveAt);
        cal.add(Calendar.DAY_OF_MONTH, validityDays);
        return cal.getTime();
    }
}
