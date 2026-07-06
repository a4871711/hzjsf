package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.MkCouponDao;
import com.dlc.modules.sys.dao.MkCouponProductRelDao;
import com.dlc.modules.sys.dao.MkCouponStoreRelDao;
import com.dlc.modules.sys.dao.MkMemberCouponDao;
import com.dlc.modules.sys.entity.MkCouponEntity;
import com.dlc.modules.sys.entity.MkCouponProductRelEntity;
import com.dlc.modules.sys.entity.MkCouponStoreRelEntity;
import com.dlc.modules.sys.entity.MkMemberCouponEntity;
import com.dlc.modules.sys.service.SysMarketingCouponService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券 Service 实现。save/update 一次表单事务内写主表 + 门店/商品两张 rel（全删全插），
 * 事务由 sys.service.impl 切面统一管理（REQUIRED）。
 * 满减(1)/折扣(2)字段二选一：另一类字段强制置 NULL（详细实现文档营销域 §3.1）。
 * 状态枚举 1上架/0下架（新枚举，勿照抄旧 sys_coupon 的 2=下架）。
 *
 * @author claude
 */
@Service("sysMarketingCouponService")
public class SysMarketingCouponServiceImpl implements SysMarketingCouponService {
    private static final Logger logger = LoggerFactory.getLogger(SysMarketingCouponServiceImpl.class);

    @Autowired
    private MkCouponDao mkCouponDao;
    @Autowired
    private MkCouponStoreRelDao mkCouponStoreRelDao;
    @Autowired
    private MkCouponProductRelDao mkCouponProductRelDao;
    @Autowired
    private MkMemberCouponDao mkMemberCouponDao;

    @Override
    public MkCouponEntity queryObject(Long id) {
        MkCouponEntity c = mkCouponDao.queryObject(id);
        if (c == null) {
            return null;
        }
        c.setStoreIds(mkCouponStoreRelDao.queryStoreIds(id));
        c.setProductIds(mkCouponProductRelDao.queryProductIds(id));
        return c;
    }

    @Override
    public List<MkCouponEntity> queryList(Map<String, Object> map) {
        return mkCouponDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return mkCouponDao.queryTotal(map);
    }

    @Override
    public void save(MkCouponEntity entity) {
        validate(entity);
        applyDefaults(entity);
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        mkCouponDao.save(entity);
        saveStoreRels(entity.getId(), entity.getStoreIds());
        saveProductRels(entity.getId(), entity.getProductIds());
    }

    @Override
    public void update(MkCouponEntity entity) {
        MkCouponEntity old = mkCouponDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("优惠券不存在");
        }
        validate(entity);
        applyDefaults(entity);
        entity.setUpdatedAt(new Date());
        // 已发放的券改名不回溯：mk_member_coupon 用快照，此处不回写已领记录
        mkCouponDao.update(entity);
        mkCouponStoreRelDao.deleteByCouponId(entity.getId());
        saveStoreRels(entity.getId(), entity.getStoreIds());
        mkCouponProductRelDao.deleteByCouponId(entity.getId());
        saveProductRels(entity.getId(), entity.getProductIds());
    }

    @Override
    public void deleteBatch(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        // 已有领取记录的券拒删（改用下架）：删除会断掉领券记录/订单券快照的追溯链
        for (Long id : ids) {
            MkCouponEntity c = mkCouponDao.queryObject(id);
            if (c == null || Integer.valueOf(1).equals(c.getDeleted())) {
                continue;
            }
            if (mkMemberCouponDao.countByCouponId(id) > 0) {
                throw new RRException("优惠券[" + c.getCouponName() + "]已有会员领取，不可删除，请改用下架");
            }
        }
        for (Long id : ids) {
            mkCouponStoreRelDao.deleteByCouponId(id);
            mkCouponProductRelDao.deleteByCouponId(id);
        }
        mkCouponDao.deleteBatch(ids);
    }

    @Override
    public void changeStatus(Long id, Integer status, Long userId) {
        if (id == null || status == null || (status != 0 && status != 1)) {
            throw new RRException("参数不合法");
        }
        MkCouponEntity c = mkCouponDao.queryObject(id);
        if (c == null || Integer.valueOf(1).equals(c.getDeleted())) {
            throw new RRException("优惠券不存在");
        }
        // 下架不影响已领券（仍可用到 expire_time），仅用户端不可再领
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("updatedBy", userId);
        mkCouponDao.changeStatus(map);
    }

    @Override
    public int grant(Long couponId, List<Long> memberIds) {
        if (couponId == null) {
            throw new RRException("请选择优惠券");
        }
        if (memberIds == null || memberIds.isEmpty()) {
            throw new RRException("请选择发放会员");
        }
        MkCouponEntity coupon = mkCouponDao.queryObject(couponId);
        if (coupon == null || Integer.valueOf(1).equals(coupon.getDeleted())) {
            throw new RRException("优惠券不存在");
        }
        if (!Integer.valueOf(1).equals(coupon.getStatus())) {
            throw new RRException("优惠券已下架，不能发放");
        }
        // 本期允许同会员同券重复发放（无每人限领约束，前端二次确认）；逐条 insert，单条失败不影响其余
        int grantCount = 0;
        for (Long memberId : memberIds) {
            if (memberId == null) {
                continue;
            }
            try {
                MkMemberCouponEntity mc = new MkMemberCouponEntity();
                mc.setMemberId(memberId);
                mc.setCouponId(couponId);
                mc.setCouponName(coupon.getCouponName());
                mc.setCouponType(coupon.getCouponType());
                mc.setValidDays(coupon.getValidDays());
                mkMemberCouponDao.save(mc);
                grantCount++;
            } catch (Exception e) {
                logger.error("发券失败 couponId={} memberId={}", couponId, memberId, e);
            }
        }
        return grantCount;
    }

    /* ============ 私有辅助方法 ============ */

    private void validate(MkCouponEntity e) {
        if (StringUtils.isBlank(e.getCouponName())) {
            throw new RRException("券名称不能为空");
        }
        if (e.getCouponName().trim().length() > 100) {
            throw new RRException("券名称不能超过100字");
        }
        if (e.getCouponType() == null
                || (!Integer.valueOf(1).equals(e.getCouponType()) && !Integer.valueOf(2).equals(e.getCouponType()))) {
            throw new RRException("请选择券类型");
        }
        if (Integer.valueOf(1).equals(e.getCouponType())) {
            // 满减券：优惠金额必填且>0；折扣字段置 NULL（二选一互斥收口）
            if (e.getDiscountAmount() == null || e.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RRException("满减券必须填写大于0的优惠金额");
            }
            e.setDiscountRate(null);
            e.setMaxDiscountAmount(null);
        } else {
            // 折扣券：折扣值必填且 0<rate<10（8.50=8.5折，DDL 注释口径）；封顶金额选填；满减字段置 NULL
            if (e.getDiscountRate() == null
                    || e.getDiscountRate().compareTo(BigDecimal.ZERO) <= 0
                    || e.getDiscountRate().compareTo(BigDecimal.TEN) >= 0) {
                throw new RRException("折扣券必须填写0到10之间的折扣值（如8.5表示8.5折）");
            }
            if (e.getMaxDiscountAmount() != null && e.getMaxDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RRException("最高优惠金额必须大于0");
            }
            e.setDiscountAmount(null);
        }
        if (e.getUseThresholdAmount() != null && e.getUseThresholdAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RRException("使用门槛金额不合法");
        }
        if (e.getValidDays() == null || e.getValidDays() < 1) {
            throw new RRException("有效天数必须大于0");
        }
        if (e.getStoreIds() == null || e.getStoreIds().isEmpty()) {
            throw new RRException("请至少选择一个可用门店");
        }
        Integer scopeType = e.getScopeType() == null ? 1 : e.getScopeType();
        if (!Integer.valueOf(1).equals(scopeType) && !Integer.valueOf(2).equals(scopeType)) {
            throw new RRException("适用范围不合法");
        }
        if (Integer.valueOf(2).equals(scopeType)) {
            if (e.getProductIds() == null || e.getProductIds().isEmpty()) {
                throw new RRException("指定商品范围时必须选择至少一个商品");
            }
        } else {
            // 全部商品：忽略并清空指定商品
            e.setProductIds(null);
        }
    }

    private void applyDefaults(MkCouponEntity e) {
        if (e.getScopeType() == null) { e.setScopeType(1); }
        if (e.getIsNewUserCoupon() == null) { e.setIsNewUserCoupon(0); }
        if (e.getStatus() == null) { e.setStatus(1); }
        if (e.getStatus() != 0 && e.getStatus() != 1) {
            throw new RRException("状态不合法（1上架/0下架）");
        }
    }

    private void saveStoreRels(Long couponId, List<Long> storeIds) {
        if (storeIds == null) { return; }
        Date now = new Date();
        for (Long storeId : storeIds) {
            if (storeId == null) { continue; }
            MkCouponStoreRelEntity rel = new MkCouponStoreRelEntity();
            rel.setCouponId(couponId);
            rel.setStoreId(storeId);
            rel.setCreatedAt(now);
            mkCouponStoreRelDao.save(rel);
        }
    }

    private void saveProductRels(Long couponId, List<Long> productIds) {
        if (productIds == null) { return; }
        Date now = new Date();
        for (Long productId : productIds) {
            if (productId == null) { continue; }
            MkCouponProductRelEntity rel = new MkCouponProductRelEntity();
            rel.setCouponId(couponId);
            rel.setProductId(productId);
            rel.setCreatedAt(now);
            mkCouponProductRelDao.save(rel);
        }
    }
}
