package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.PtMemberPrivateBenefitDao;
import com.dlc.modules.api.dao.PtPrivateOrderDao;
import com.dlc.modules.sys.dao.*;
import com.dlc.modules.sys.entity.*;
import com.dlc.modules.sys.service.SysPtProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 私教商品 Service 实现。一次表单事务内维护主表 + 适用门店/指定教练关联 + 分期规则(1:1) +
 * 附赠团课权益规则(1:1，含指定团课商品关联)。事务由 sys.service.impl 切面统一管理（REQUIRED）。
 *
 * @author claude
 */
@Service("sysPtProductService")
public class SysPtProductServiceImpl implements SysPtProductService {

    @Autowired
    private PtProductDao ptProductDao;
    @Autowired
    private PtProductTypeDao ptProductTypeDao;
    @Autowired
    private PtProductStoreRelDao ptProductStoreRelDao;
    @Autowired
    private PtProductCoachRelDao ptProductCoachRelDao;
    @Autowired
    private PtInstallmentRuleDao ptInstallmentRuleDao;
    @Autowired
    private PtProductGroupBenefitDao ptProductGroupBenefitDao;
    @Autowired
    private PtProductGroupBenefitRelDao ptProductGroupBenefitRelDao;
    @Autowired
    private PtCoachDao ptCoachDao;
    @Autowired
    private PtCoachScheduleDao ptCoachScheduleDao;
    /** 跨模块注入 api dao(现有惯例):交易/权益引用护栏。预约必有订单,订单/权益两道计数已隐含覆盖预约引用 */
    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private PtMemberPrivateBenefitDao ptMemberPrivateBenefitDao;

    @Override
    public PtProductEntity queryObject(Long id) {
        PtProductEntity p = ptProductDao.queryObject(id);
        if (p == null) {
            return null;
        }
        fillSubConfigs(p);
        return p;
    }

    @Override
    public List<PtProductEntity> queryList(Map<String, Object> map) {
        return ptProductDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptProductDao.queryTotal(map);
    }

    @Override
    public void save(PtProductEntity entity) {
        validateBase(entity);
        applyDefaults(entity);
        Date now = new Date();
        entity.setSoldCount(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        if (Integer.valueOf(1).equals(entity.getListingStatus())) {
            entity.setListingAt(now);
        }
        int retry = 0;
        while (true) {
            entity.setProductNo(genProductNo());
            try {
                ptProductDao.save(entity);
                break;
            } catch (DuplicateKeyException e) {
                if (++retry >= 3) {
                    throw new RRException("商品编号生成冲突，请重试");
                }
            }
        }
        saveStoreRels(entity.getId(), entity.getStoreIds());
        saveCoachRels(entity.getId(), entity.getCoachIds());
        upsertSubConfigs(entity.getId(), entity, entity.getCreatedBy(), now);
        if (Integer.valueOf(1).equals(entity.getListingStatus())) {
            checkCanList(entity.getId());
        }
    }

    @Override
    public void update(PtProductEntity entity) {
        PtProductEntity old = ptProductDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("商品不存在");
        }
        validateBase(entity);
        applyDefaults(entity);
        Date now = new Date();
        entity.setUpdatedAt(now);
        ptProductDao.update(entity);
        ptProductStoreRelDao.deleteByProductId(entity.getId());
        saveStoreRels(entity.getId(), entity.getStoreIds());
        ptProductCoachRelDao.deleteByProductId(entity.getId());
        saveCoachRels(entity.getId(), entity.getCoachIds());
        upsertSubConfigs(entity.getId(), entity, entity.getUpdatedBy(), now);
        if (Integer.valueOf(1).equals(entity.getListingStatus())) {
            checkCanList(entity.getId());
        }
    }

    @Override
    public void deleteBatch(Long[] ids) {
        // 第14步回填：存在交易引用(pt_private_order / pt_member_private_benefit)的商品不可删除——
        // 订单与权益是会员资产/账务凭证,商品行须保留供快照回溯(下架用 offCard,不要删)。
        for (Long id : ids) {
            if (ptPrivateOrderDao.countByProduct(id) > 0
                    || ptMemberPrivateBenefitDao.countByProduct(id) > 0) {
                throw new RRException("商品[" + id + "]已产生购买订单或会员权益，不可删除，请改用下架");
            }
        }
        for (Long id : ids) {
            ptProductStoreRelDao.deleteByProductId(id);
            ptProductCoachRelDao.deleteByProductId(id);
            ptInstallmentRuleDao.deleteByProductId(id);
            PtProductGroupBenefitEntity gb = ptProductGroupBenefitDao.queryByProductId(id);
            if (gb != null) {
                ptProductGroupBenefitRelDao.deleteByBenefitId(gb.getId());
                ptProductGroupBenefitDao.deleteByProductId(id);
            }
        }
        ptProductDao.deleteBatch(ids);
    }

    @Override
    public void onCard(Long[] ids) {
        if (ids == null) {
            return;
        }
        Date now = new Date();
        for (Long id : ids) {
            checkCanList(id);
            PtProductEntity u = new PtProductEntity();
            u.setId(id);
            u.setListingStatus(1);
            u.setListingAt(now);
            u.setUpdatedAt(now);
            ptProductDao.update(u);
        }
    }

    @Override
    public void offCard(Long[] ids) {
        if (ids == null) {
            return;
        }
        Date now = new Date();
        for (Long id : ids) {
            PtProductEntity u = new PtProductEntity();
            u.setId(id);
            u.setListingStatus(0);
            u.setUnlistingAt(now);
            u.setUpdatedAt(now);
            ptProductDao.update(u);
        }
    }

    @Override
    public void copy(Long id) {
        PtProductEntity src = ptProductDao.queryObject(id);
        if (src == null || Integer.valueOf(1).equals(src.getDeleted())) {
            throw new RRException("商品不存在");
        }
        List<Long> storeIds = ptProductStoreRelDao.queryStoreIds(id);
        List<Long> coachIds = ptProductCoachRelDao.queryCoachIds(id);
        PtInstallmentRuleEntity ir = ptInstallmentRuleDao.queryByProductId(id);
        PtProductGroupBenefitEntity gb = ptProductGroupBenefitDao.queryByProductId(id);
        List<Long> groupProductIds = gb == null ? null : ptProductGroupBenefitRelDao.queryGroupProductIds(gb.getId());

        Date now = new Date();
        PtProductEntity copy = new PtProductEntity();
        copy.setProductName(src.getProductName() + "-副本");
        copy.setProductSubtitle(src.getProductSubtitle());
        copy.setProductTypeId(src.getProductTypeId());
        copy.setServiceType(src.getServiceType());
        copy.setCategoryName(src.getCategoryName());
        copy.setCoverUrl(src.getCoverUrl());
        copy.setProductIntro(src.getProductIntro());
        copy.setProductDetail(src.getProductDetail());
        copy.setTargetDesc(src.getTargetDesc());
        copy.setOriginalPrice(src.getOriginalPrice());
        copy.setSalePrice(src.getSalePrice());
        copy.setMemberPrice(src.getMemberPrice());
        copy.setNewUserPrice(src.getNewUserPrice());
        copy.setLessonCount(src.getLessonCount());
        copy.setDurationMinutes(src.getDurationMinutes());
        copy.setValidityDays(src.getValidityDays());
        copy.setRefundType(src.getRefundType());
        copy.setRefundRule(src.getRefundRule());
        copy.setVisibleGroups(src.getVisibleGroups());
        copy.setPurchaseLimit(src.getPurchaseLimit());
        copy.setSaleStock(src.getSaleStock());
        copy.setSoldCount(0);
        copy.setBookingGapMinutes(src.getBookingGapMinutes());
        copy.setBookingCapacity(src.getBookingCapacity());
        copy.setLatestBookingHours(src.getLatestBookingHours());
        copy.setLatestFreeCancelHours(src.getLatestFreeCancelHours());
        copy.setNoShowDeduct(src.getNoShowDeduct());
        copy.setCoachConfirmRequired(src.getCoachConfirmRequired());
        copy.setListingStatus(0);
        copy.setSortNo(src.getSortNo());
        copy.setRecommendPrivate(src.getRecommendPrivate());
        copy.setRecommendHome(src.getRecommendHome());
        copy.setCreatedBy(src.getUpdatedBy());
        copy.setUpdatedBy(src.getUpdatedBy());
        copy.setCreatedAt(now);
        copy.setUpdatedAt(now);
        int retry = 0;
        while (true) {
            copy.setProductNo(genProductNo());
            try {
                ptProductDao.save(copy);
                break;
            } catch (DuplicateKeyException e) {
                if (++retry >= 3) {
                    throw new RRException("商品编号生成冲突，请重试");
                }
            }
        }
        saveStoreRels(copy.getId(), storeIds);
        saveCoachRels(copy.getId(), coachIds);

        if (ir != null) {
            PtInstallmentRuleEntity newIr = new PtInstallmentRuleEntity();
            newIr.setProductId(copy.getId());
            newIr.setIsEnabled(ir.getIsEnabled());
            newIr.setDownPaymentAmount(ir.getDownPaymentAmount());
            newIr.setInstallmentCount(ir.getInstallmentCount());
            newIr.setInstallmentIntervalMonths(ir.getInstallmentIntervalMonths());
            newIr.setOverduePauseBooking(ir.getOverduePauseBooking());
            newIr.setStatus(ir.getStatus());
            newIr.setCreatedBy(copy.getCreatedBy());
            newIr.setUpdatedBy(copy.getCreatedBy());
            newIr.setCreatedAt(now);
            newIr.setUpdatedAt(now);
            ptInstallmentRuleDao.save(newIr);
        }
        if (gb != null) {
            PtProductGroupBenefitEntity newGb = new PtProductGroupBenefitEntity();
            newGb.setProductId(copy.getId());
            newGb.setIsEnabled(gb.getIsEnabled());
            newGb.setGiftCount(gb.getGiftCount());
            newGb.setValidityDays(gb.getValidityDays());
            newGb.setScopeType(gb.getScopeType());
            newGb.setStatus(gb.getStatus());
            newGb.setCreatedBy(copy.getCreatedBy());
            newGb.setUpdatedBy(copy.getCreatedBy());
            newGb.setCreatedAt(now);
            newGb.setUpdatedAt(now);
            ptProductGroupBenefitDao.save(newGb);
            if (groupProductIds != null) {
                for (Long gpId : groupProductIds) {
                    PtProductGroupBenefitRelEntity rel = new PtProductGroupBenefitRelEntity();
                    rel.setBenefitId(newGb.getId());
                    rel.setGroupProductId(gpId);
                    rel.setCreatedAt(now);
                    ptProductGroupBenefitRelDao.save(rel);
                }
            }
        }
    }

    @Override
    public List<TeamClassEntity> groupClassOptions() {
        return ptProductGroupBenefitDao.queryTeamClassOptions();
    }

    /* ============ 私有辅助方法 ============ */

    private void fillSubConfigs(PtProductEntity p) {
        p.setStoreIds(ptProductStoreRelDao.queryStoreIds(p.getId()));
        p.setCoachIds(ptProductCoachRelDao.queryCoachIds(p.getId()));
        PtInstallmentRuleEntity ir = ptInstallmentRuleDao.queryByProductId(p.getId());
        if (ir != null) {
            p.setInstallmentEnabled(ir.getIsEnabled());
            p.setInstallmentDownPaymentAmount(ir.getDownPaymentAmount());
            p.setInstallmentCount(ir.getInstallmentCount());
            p.setInstallmentIntervalMonths(ir.getInstallmentIntervalMonths());
            p.setInstallmentOverduePauseBooking(ir.getOverduePauseBooking());
        }
        PtProductGroupBenefitEntity gb = ptProductGroupBenefitDao.queryByProductId(p.getId());
        if (gb != null) {
            p.setGroupBenefitEnabled(gb.getIsEnabled());
            p.setGroupBenefitGiftCount(gb.getGiftCount());
            p.setGroupBenefitValidityDays(gb.getValidityDays());
            p.setGroupBenefitScopeType(gb.getScopeType());
            p.setGroupProductIds(ptProductGroupBenefitRelDao.queryGroupProductIds(gb.getId()));
        }
    }

    private void validateBase(PtProductEntity e) {
        if (StringUtils.isBlank(e.getProductName())) {
            throw new RRException("商品名称不能为空");
        }
        int nameLen = e.getProductName().trim().length();
        if (nameLen < 2 || nameLen > 30) {
            throw new RRException("商品名称长度需为 2-30 字");
        }
        if (e.getProductTypeId() == null) {
            throw new RRException("请选择商品类型");
        }
        PtProductTypeEntity type = ptProductTypeDao.queryObject(e.getProductTypeId());
        if (type == null || Integer.valueOf(1).equals(type.getDeleted())) {
            throw new RRException("商品类型不存在");
        }
        if (!Integer.valueOf(1).equals(type.getStatus())) {
            throw new RRException("商品类型已停用，不能选择");
        }
        if (e.getServiceType() == null || (!Integer.valueOf(1).equals(e.getServiceType()) && !Integer.valueOf(2).equals(e.getServiceType()))) {
            throw new RRException("请选择服务类型");
        }
        if (e.getSalePrice() == null || e.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RRException("售价不合法");
        }
        if (e.getLessonCount() == null || e.getLessonCount() <= 0) {
            throw new RRException("课时数量必须大于0");
        }
        if (e.getValidityDays() == null || (e.getValidityDays() != -1 && e.getValidityDays() <= 0)) {
            throw new RRException("有效期不合法（长期请填 -1）");
        }
        if (Integer.valueOf(1).equals(e.getRefundType()) && StringUtils.isBlank(e.getRefundRule())) {
            throw new RRException("支持退款时必须填写退款规则");
        }
        if (e.getStoreIds() == null || e.getStoreIds().isEmpty()) {
            throw new RRException("请至少选择一个适用门店");
        }
        if (Integer.valueOf(1).equals(e.getInstallmentEnabled())) {
            if (e.getInstallmentDownPaymentAmount() == null || e.getInstallmentDownPaymentAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new RRException("请填写合法的首付金额");
            }
            if (e.getInstallmentCount() == null || e.getInstallmentCount() < 2) {
                throw new RRException("分期期数至少为2期");
            }
        }
        if (Integer.valueOf(1).equals(e.getGroupBenefitEnabled())) {
            if (e.getGroupBenefitGiftCount() == null || e.getGroupBenefitGiftCount() <= 0) {
                throw new RRException("请填写赠送团课次数");
            }
            if (e.getGroupBenefitValidityDays() == null || e.getGroupBenefitValidityDays() <= 0) {
                throw new RRException("请填写团课权益有效期");
            }
            if (Integer.valueOf(2).equals(e.getGroupBenefitScopeType())
                    && (e.getGroupProductIds() == null || e.getGroupProductIds().isEmpty())) {
                throw new RRException("适用范围为指定团课商品时必须选择至少一个团课");
            }
        }
        if (e.getCoachIds() != null) {
            for (Long coachId : e.getCoachIds()) {
                PtCoachEntity coach = ptCoachDao.queryObject(coachId);
                if (coach == null || Integer.valueOf(1).equals(coach.getDeleted())) {
                    throw new RRException("指定教练不存在");
                }
                if (!Integer.valueOf(1).equals(coach.getStatus())) {
                    throw new RRException("指定教练已停用/离职：" + coach.getCoachName());
                }
            }
        }
    }

    private void applyDefaults(PtProductEntity e) {
        if (e.getSoldCount() == null) { e.setSoldCount(0); }
        if (e.getListingStatus() == null) { e.setListingStatus(0); }
        if (e.getSortNo() == null) { e.setSortNo(0); }
        if (e.getRecommendPrivate() == null) { e.setRecommendPrivate(0); }
        if (e.getRecommendHome() == null) { e.setRecommendHome(0); }
        if (e.getRefundType() == null) { e.setRefundType(2); }
        if (e.getBookingGapMinutes() == null) { e.setBookingGapMinutes(0); }
        if (e.getLatestBookingHours() == null) { e.setLatestBookingHours(2); }
        if (e.getLatestFreeCancelHours() == null) { e.setLatestFreeCancelHours(2); }
        if (e.getNoShowDeduct() == null) { e.setNoShowDeduct(1); }
        if (e.getCoachConfirmRequired() == null) { e.setCoachConfirmRequired(0); }
        if (e.getDurationMinutes() == null || e.getDurationMinutes() <= 0) { e.setDurationMinutes(60); }
        // 服务类型为一对一时，单时段可预约人数固定为1；一对多时默认至少为1
        if (Integer.valueOf(1).equals(e.getServiceType())) {
            e.setBookingCapacity(1);
        } else if (e.getBookingCapacity() == null || e.getBookingCapacity() < 1) {
            e.setBookingCapacity(1);
        }
    }

    /** 商品上架前校验：需求 8.2（名称/售价/课时/有效期/门店/指定教练状态/可约教练/预约规则完整） */
    private void checkCanList(Long productId) {
        PtProductEntity p = ptProductDao.queryObject(productId);
        if (p == null || Integer.valueOf(1).equals(p.getDeleted())) {
            throw new RRException("商品不存在");
        }
        if (StringUtils.isBlank(p.getProductName())) {
            throw new RRException("商品名称未填写，不能上架");
        }
        if (p.getSalePrice() == null || p.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RRException("售价不合法，不能上架");
        }
        if (p.getLessonCount() == null || p.getLessonCount() <= 0) {
            throw new RRException("课时数量不合法，不能上架");
        }
        if (p.getValidityDays() == null || (p.getValidityDays() != -1 && p.getValidityDays() <= 0)) {
            throw new RRException("有效期不合法，不能上架");
        }
        if (p.getDurationMinutes() == null || p.getDurationMinutes() <= 0) {
            throw new RRException("单次服务时长不合法，不能上架");
        }
        if (p.getBookingCapacity() == null || p.getBookingCapacity() < 1) {
            throw new RRException("单时段可预约人数不合法，不能上架");
        }
        List<Long> storeIds = ptProductStoreRelDao.queryStoreIds(productId);
        if (storeIds.isEmpty()) {
            throw new RRException("至少需要一个适用门店，不能上架");
        }
        List<Long> coachIds = ptProductCoachRelDao.queryCoachIds(productId);
        if (!coachIds.isEmpty()) {
            for (Long coachId : coachIds) {
                PtCoachEntity coach = ptCoachDao.queryObject(coachId);
                if (coach == null || !Integer.valueOf(1).equals(coach.getStatus())) {
                    throw new RRException("指定教练已停用/离职，不能上架");
                }
            }
        }
        int bookable = ptCoachScheduleDao.countBookableCoaches(joinIds(storeIds), coachIds.isEmpty() ? null : joinIds(coachIds));
        if (bookable <= 0) {
            throw new RRException("不存在有排班的可约教练，不能上架");
        }
    }

    /** 分期规则、附赠团课权益规则 1:1 upsert（不存在则插入，存在则更新） */
    private void upsertSubConfigs(Long productId, PtProductEntity e, Long operatorId, Date now) {
        PtInstallmentRuleEntity existingIr = ptInstallmentRuleDao.queryByProductId(productId);
        Integer installmentEnabled = e.getInstallmentEnabled() == null ? 0 : e.getInstallmentEnabled();
        Integer installmentIntervalMonths = e.getInstallmentIntervalMonths() == null ? 1 : e.getInstallmentIntervalMonths();
        if (existingIr == null) {
            PtInstallmentRuleEntity ir = new PtInstallmentRuleEntity();
            ir.setProductId(productId);
            ir.setIsEnabled(installmentEnabled);
            ir.setDownPaymentAmount(e.getInstallmentDownPaymentAmount());
            ir.setInstallmentCount(e.getInstallmentCount());
            ir.setInstallmentIntervalMonths(installmentIntervalMonths);
            ir.setOverduePauseBooking(1);
            ir.setStatus(1);
            ir.setCreatedBy(operatorId);
            ir.setUpdatedBy(operatorId);
            ir.setCreatedAt(now);
            ir.setUpdatedAt(now);
            ptInstallmentRuleDao.save(ir);
        } else {
            PtInstallmentRuleEntity u = new PtInstallmentRuleEntity();
            u.setId(existingIr.getId());
            u.setIsEnabled(installmentEnabled);
            u.setDownPaymentAmount(e.getInstallmentDownPaymentAmount());
            u.setInstallmentCount(e.getInstallmentCount());
            u.setInstallmentIntervalMonths(installmentIntervalMonths);
            u.setUpdatedBy(operatorId);
            u.setUpdatedAt(now);
            ptInstallmentRuleDao.update(u);
        }

        PtProductGroupBenefitEntity existingGb = ptProductGroupBenefitDao.queryByProductId(productId);
        Integer groupBenefitEnabled = e.getGroupBenefitEnabled() == null ? 0 : e.getGroupBenefitEnabled();
        Integer scopeType = e.getGroupBenefitScopeType() == null ? 1 : e.getGroupBenefitScopeType();
        Long benefitId;
        if (existingGb == null) {
            PtProductGroupBenefitEntity gb = new PtProductGroupBenefitEntity();
            gb.setProductId(productId);
            gb.setIsEnabled(groupBenefitEnabled);
            gb.setGiftCount(e.getGroupBenefitGiftCount());
            gb.setValidityDays(e.getGroupBenefitValidityDays());
            gb.setScopeType(scopeType);
            gb.setStatus(1);
            gb.setCreatedBy(operatorId);
            gb.setUpdatedBy(operatorId);
            gb.setCreatedAt(now);
            gb.setUpdatedAt(now);
            ptProductGroupBenefitDao.save(gb);
            benefitId = gb.getId();
        } else {
            PtProductGroupBenefitEntity u = new PtProductGroupBenefitEntity();
            u.setId(existingGb.getId());
            u.setIsEnabled(groupBenefitEnabled);
            u.setGiftCount(e.getGroupBenefitGiftCount());
            u.setValidityDays(e.getGroupBenefitValidityDays());
            u.setScopeType(scopeType);
            u.setUpdatedBy(operatorId);
            u.setUpdatedAt(now);
            ptProductGroupBenefitDao.update(u);
            benefitId = existingGb.getId();
        }
        // 指定团课商品：全删全插
        ptProductGroupBenefitRelDao.deleteByBenefitId(benefitId);
        if (Integer.valueOf(2).equals(scopeType) && e.getGroupProductIds() != null) {
            for (Long gpId : e.getGroupProductIds()) {
                if (gpId == null) { continue; }
                PtProductGroupBenefitRelEntity rel = new PtProductGroupBenefitRelEntity();
                rel.setBenefitId(benefitId);
                rel.setGroupProductId(gpId);
                rel.setCreatedAt(now);
                ptProductGroupBenefitRelDao.save(rel);
            }
        }
    }

    private void saveStoreRels(Long productId, List<Long> storeIds) {
        if (storeIds == null) { return; }
        Date now = new Date();
        for (Long storeId : storeIds) {
            if (storeId == null) { continue; }
            PtProductStoreRelEntity rel = new PtProductStoreRelEntity();
            rel.setProductId(productId);
            rel.setStoreId(storeId);
            rel.setCreatedAt(now);
            ptProductStoreRelDao.save(rel);
        }
    }

    private void saveCoachRels(Long productId, List<Long> coachIds) {
        if (coachIds == null || coachIds.isEmpty()) { return; }
        Date now = new Date();
        for (Long coachId : coachIds) {
            if (coachId == null) { continue; }
            PtProductCoachRelEntity rel = new PtProductCoachRelEntity();
            rel.setProductId(productId);
            rel.setCoachId(coachId);
            rel.setCreatedAt(now);
            ptProductCoachRelDao.save(rel);
        }
    }

    private String genProductNo() {
        String prefix = "SJ" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = new Random().nextInt(9000) + 1000;
        return prefix + rand;
    }

    private String joinIds(List<Long> ids) {
        return StringUtils.join(ids, ",");
    }
}
