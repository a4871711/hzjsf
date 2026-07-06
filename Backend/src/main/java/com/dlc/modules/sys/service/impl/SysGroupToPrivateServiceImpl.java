package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.dao.PtGroupToPrivateFollowDao;
import com.dlc.modules.sys.dao.PtGroupToPrivateLeadDao;
import com.dlc.modules.sys.dao.PtGroupToPrivateRuleDao;
import com.dlc.modules.sys.entity.PtGroupToPrivateFollowEntity;
import com.dlc.modules.sys.entity.PtGroupToPrivateLeadEntity;
import com.dlc.modules.sys.entity.PtGroupToPrivateRuleEntity;
import com.dlc.modules.sys.service.SysGroupToPrivateService;
import com.dlc.modules.sys.service.SysMarketingCouponService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 团课转私教后台 Service 实现（第22步·运营域·团课转私教）。
 * 事务由 sys.service.impl 切面统一管理（REQUIRED）：sendCoupon（grant+置券态）、follow（插流水+回写名单）均为单事务。
 * 发券状态机防重：experience_coupon_status 0未发→1已发→2已用；已转化(follow_status=2)不再发券。
 * 复用营销域 SysMarketingCouponService.grant 发券（同款 mk_member_coupon insert 能力，第17步）。
 *
 * @author claude
 */
@Service("sysGroupToPrivateService")
public class SysGroupToPrivateServiceImpl implements SysGroupToPrivateService {

    @Autowired
    private PtGroupToPrivateRuleDao ptGroupToPrivateRuleDao;
    @Autowired
    private PtGroupToPrivateLeadDao ptGroupToPrivateLeadDao;
    @Autowired
    private PtGroupToPrivateFollowDao ptGroupToPrivateFollowDao;
    @Autowired
    private SysMarketingCouponService sysMarketingCouponService;

    /* ===== 规则五法 ===== */

    @Override
    public PageUtils queryRulePage(Query query) {
        List<PtGroupToPrivateRuleEntity> list = ptGroupToPrivateRuleDao.queryList(query);
        int total = ptGroupToPrivateRuleDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public PtGroupToPrivateRuleEntity queryRule(Long id) {
        return ptGroupToPrivateRuleDao.queryObject(id);
    }

    @Override
    public void saveRule(PtGroupToPrivateRuleEntity rule) {
        validate(rule);
        applyDefaults(rule);
        Date now = new Date();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        ptGroupToPrivateRuleDao.save(rule);
    }

    @Override
    public void updateRule(PtGroupToPrivateRuleEntity rule) {
        if (rule.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        PtGroupToPrivateRuleEntity old = ptGroupToPrivateRuleDao.queryObject(rule.getId());
        if (old == null) {
            throw new RRException("团课转私教规则不存在");
        }
        validate(rule);
        applyDefaults(rule);
        rule.setUpdatedAt(new Date());
        ptGroupToPrivateRuleDao.update(rule);
    }

    @Override
    public void deleteRule(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        ptGroupToPrivateRuleDao.deleteBatch(ids);
    }

    /* ===== 转化名单与跟进 ===== */

    @Override
    public PageUtils queryLeadPage(Query query) {
        List<PtGroupToPrivateLeadEntity> list = ptGroupToPrivateLeadDao.queryList(query);
        int total = ptGroupToPrivateLeadDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public PtGroupToPrivateLeadEntity queryLeadDetail(Long id, String storeIds) {
        PtGroupToPrivateLeadEntity lead = ptGroupToPrivateLeadDao.queryDetail(id, storeIds);
        if (lead == null) {
            return null;
        }
        lead.setFollowList(ptGroupToPrivateFollowDao.listByLeadId(id));
        return lead;
    }

    @Override
    public boolean existsInScope(Long id, String storeIds) {
        return ptGroupToPrivateLeadDao.countInScope(id, storeIds) > 0;
    }

    @Override
    public void sendCoupon(Long leadId, Long couponId) {
        if (leadId == null) {
            throw new RRException("缺少参数：leadId");
        }
        if (couponId == null) {
            throw new RRException("请选择要发放的体验券");
        }
        // 门店隔离在 controller 前置判存已收口，这里再取名单校验状态机
        PtGroupToPrivateLeadEntity lead = ptGroupToPrivateLeadDao.queryDetail(leadId, null);
        if (lead == null) {
            throw new RRException("转化名单不存在");
        }
        // §16.4：已转化不再发券
        if (Integer.valueOf(2).equals(lead.getFollowStatus())) {
            throw new RRException("该会员已转化，无需再发体验券");
        }
        // 发券状态机：仅未发放可发（防重发）
        if (!Integer.valueOf(0).equals(lead.getExperienceCouponStatus())) {
            throw new RRException("体验券已发放，不可重复发放");
        }
        // 状态机推进：条件 UPDATE 0→1，命中0行=并发下已被抢先发放，拦住幂等
        if (ptGroupToPrivateLeadDao.markCouponSent(leadId) == 0) {
            throw new RRException("体验券已发放，不可重复发放");
        }
        // 调营销域发券给该会员（同事务；grant 内单条失败仅记日志，返回成功数）
        int granted = sysMarketingCouponService.grant(couponId, Collections.singletonList(lead.getMemberId()));
        if (granted <= 0) {
            // 发券未成功（券下架/不存在等）→ 抛异常整单回滚，券态回退为未发放
            throw new RRException("体验券发放失败，请检查券是否有效");
        }
    }

    @Override
    public void follow(Long leadId, Integer followStatus, String followRemark, Long operatorId) {
        if (leadId == null) {
            throw new RRException("缺少参数：leadId");
        }
        if (followStatus == null || followStatus < 0 || followStatus > 3) {
            throw new RRException("跟进状态须为 0待跟进/1已跟进/2已转化/3已放弃");
        }
        PtGroupToPrivateLeadEntity lead = ptGroupToPrivateLeadDao.queryDetail(leadId, null);
        if (lead == null) {
            throw new RRException("转化名单不存在");
        }
        // 单事务：插跟进流水 + 回写名单
        PtGroupToPrivateFollowEntity follow = new PtGroupToPrivateFollowEntity();
        follow.setLeadId(leadId);
        follow.setMemberId(lead.getMemberId());
        follow.setFollowStatus(followStatus);
        follow.setFollowRemark(followRemark);
        follow.setOperatorId(operatorId);
        ptGroupToPrivateFollowDao.save(follow);
        ptGroupToPrivateLeadDao.updateFollow(leadId, followStatus, operatorId);
    }

    @Override
    public void markConverted(Long leadId) {
        if (leadId == null) {
            throw new RRException("缺少参数：leadId");
        }
        ptGroupToPrivateLeadDao.markConverted(leadId);
    }

    /* ============ 私有辅助 ============ */

    private void validate(PtGroupToPrivateRuleEntity e) {
        if (StringUtils.isBlank(e.getRuleName())) {
            throw new RRException("规则名称不能为空");
        }
        if (e.getRuleName().trim().length() > 100) {
            throw new RRException("规则名称不能超过100字");
        }
        boolean attendanceSet = e.getAttendanceDays() != null && e.getAttendanceDays() > 0
                && e.getAttendanceThreshold() != null && e.getAttendanceThreshold() > 0;
        boolean purchaseSet = e.getPurchaseDays() != null && e.getPurchaseDays() > 0
                && e.getPurchaseThreshold() != null && e.getPurchaseThreshold() > 0;
        if (!attendanceSet && !purchaseSet) {
            throw new RRException("出勤维度、购课维度至少配置一组（周期天数与次数阈值均需大于0）");
        }
    }

    private void applyDefaults(PtGroupToPrivateRuleEntity e) {
        if (e.getStatus() == null) { e.setStatus(1); }
        if (e.getStatus() != 0 && e.getStatus() != 1) {
            throw new RRException("状态不合法（1启用/0停用）");
        }
        // 未完整配置的维度置 NULL，避免脏值参与扫描
        if (!(e.getAttendanceDays() != null && e.getAttendanceDays() > 0
                && e.getAttendanceThreshold() != null && e.getAttendanceThreshold() > 0)) {
            e.setAttendanceDays(null);
            e.setAttendanceThreshold(null);
        }
        if (!(e.getPurchaseDays() != null && e.getPurchaseDays() > 0
                && e.getPurchaseThreshold() != null && e.getPurchaseThreshold() > 0)) {
            e.setPurchaseDays(null);
            e.setPurchaseThreshold(null);
        }
    }
}
