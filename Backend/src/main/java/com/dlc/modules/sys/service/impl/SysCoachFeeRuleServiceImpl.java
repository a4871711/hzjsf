package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtCoachFeeRuleDao;
import com.dlc.modules.sys.dao.PtCoachMonthlyCommissionRuleDao;
import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import com.dlc.modules.sys.service.SysCoachFeeRuleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 教练课时费/分成规则 Service 实现。事务由 sys.service.impl 切面统一管理。
 *
 * @author claude
 */
@Service("sysCoachFeeRuleService")
public class SysCoachFeeRuleServiceImpl implements SysCoachFeeRuleService {

    @Autowired
    private PtCoachFeeRuleDao ptCoachFeeRuleDao;
    @Autowired
    private PtCoachMonthlyCommissionRuleDao ptCoachMonthlyCommissionRuleDao;

    @Override
    public PtCoachFeeRuleEntity queryObject(Long id) {
        return ptCoachFeeRuleDao.queryObject(id);
    }

    @Override
    public List<PtCoachFeeRuleEntity> queryList(Map<String, Object> map) {
        return ptCoachFeeRuleDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptCoachFeeRuleDao.queryTotal(map);
    }

    @Override
    public void save(PtCoachFeeRuleEntity entity) {
        validateType(entity);
        if (entity.getCoachId() == null) {
            throw new RRException("请选择教练");
        }
        // 同一教练的规则写入串行化，避免两种提成方式并发通过“先查再存”。
        ptCoachFeeRuleDao.lockCoachForUpdate(entity.getCoachId());
        // 门店×课程 笛卡尔展开（空集合 = 不限 = 0）
        List<Long> storeIds = (entity.getStoreIds() == null || entity.getStoreIds().isEmpty())
                ? singletonZero() : entity.getStoreIds();
        List<Long> productIds = (entity.getProductIds() == null || entity.getProductIds().isEmpty())
                ? singletonZero() : entity.getProductIds();
        Date now = new Date();
        for (Long storeId : storeIds) {
            for (Long productId : productIds) {
                Long sid = storeId == null ? 0L : storeId;
                Long pid = productId == null ? 0L : productId;
                validateOrdinaryProduct(pid);
                if (ptCoachFeeRuleDao.countByUk(entity.getCoachId(), pid, sid, entity.getRuleType(), null) > 0) {
                    throw new RRException("规则已存在（教练+门店+课程+类型重复）");
                }
                Integer status = entity.getStatus() == null ? 1 : entity.getStatus();
                if (Integer.valueOf(1).equals(status)) {
                    ensureOnlyOneEnabledType(entity.getCoachId(), pid, sid, entity.getRuleType(), null);
                }
                PtCoachFeeRuleEntity row = new PtCoachFeeRuleEntity();
                row.setCoachId(entity.getCoachId());
                row.setProductId(pid);
                row.setStoreId(sid);
                row.setRuleName(entity.getRuleName());
                row.setRuleType(entity.getRuleType());
                row.setLessonFee(entity.getLessonFee());
                row.setCommissionRate(entity.getCommissionRate());
                row.setEffectiveTime(entity.getEffectiveTime());
                row.setStatus(status);
                row.setCreatedBy(entity.getCreatedBy());
                row.setUpdatedBy(entity.getCreatedBy());
                row.setCreatedAt(now);
                row.setUpdatedAt(now);
                ptCoachFeeRuleDao.save(row);
            }
        }
    }

    @Override
    public void update(PtCoachFeeRuleEntity entity) {
        if (entity.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        PtCoachFeeRuleEntity old = ptCoachFeeRuleDao.queryObjectForUpdate(entity.getId());
        if (old == null) {
            throw new RRException("规则不存在");
        }
        // 单行编辑：以提交值与旧值合并后校验类型
        Integer ruleType = entity.getRuleType() != null ? entity.getRuleType() : old.getRuleType();
        Long coachId = entity.getCoachId() != null ? entity.getCoachId() : old.getCoachId();
        Long storeId = entity.getStoreId() != null ? entity.getStoreId() : old.getStoreId();
        Long productId = entity.getProductId() != null ? entity.getProductId() : old.getProductId();
        Integer status = entity.getStatus() != null ? entity.getStatus() : old.getStatus();
        lockCoachScope(old.getCoachId(), coachId);
        entity.setRuleType(ruleType);
        entity.setCoachId(coachId);
        entity.setStoreId(storeId);
        entity.setProductId(productId);
        entity.setStatus(status);
        if (entity.getRuleName() == null) {
            entity.setRuleName(old.getRuleName());
        }
        validateType(entity);
        validateOrdinaryProduct(productId);
        if (ptCoachFeeRuleDao.countByUk(coachId, productId, storeId, ruleType, entity.getId()) > 0) {
            throw new RRException("规则已存在（教练+门店+课程+类型重复）");
        }
        if (Integer.valueOf(1).equals(status)) {
            ensureOnlyOneEnabledType(coachId, productId, storeId, ruleType, entity.getId());
        }
        entity.setUpdatedAt(new Date());
        ptCoachFeeRuleDao.update(entity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        ptCoachFeeRuleDao.deleteBatch(ids);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        if (id == null || (status == null || (status != 0 && status != 1))) {
            throw new RRException("规则状态非法");
        }
        PtCoachFeeRuleEntity old = ptCoachFeeRuleDao.queryObjectForUpdate(id);
        if (old == null) {
            throw new RRException("规则不存在");
        }
        ptCoachFeeRuleDao.lockCoachForUpdate(old.getCoachId());
        if (Integer.valueOf(1).equals(status)) {
            validateOrdinaryProduct(old.getProductId());
            ensureOnlyOneEnabledType(old.getCoachId(), old.getProductId(), old.getStoreId(),
                    old.getRuleType(), old.getId());
        }
        PtCoachFeeRuleEntity u = new PtCoachFeeRuleEntity();
        u.setId(id);
        u.setStatus(status);
        u.setUpdatedAt(new Date());
        ptCoachFeeRuleDao.update(u);
    }

    @Override
    public PtCoachFeeRuleEntity matchFeeRule(Long coachId, Long productId, Long storeId, Integer ruleType) {
        if (coachId == null) { return null; }
        long pid = productId == null ? 0L : productId;
        long sid = storeId == null ? 0L : storeId;
        List<PtCoachFeeRuleEntity> rules = ptCoachFeeRuleDao.queryEnabledByCoach(coachId, ruleType);
        Date now = new Date();
        PtCoachFeeRuleEntity best = null;
        int bestRank = -1;
        for (PtCoachFeeRuleEntity r : rules) {
            long rp = r.getProductId() == null ? 0L : r.getProductId();
            long rs = r.getStoreId() == null ? 0L : r.getStoreId();
            // 适配：规则的课程/门店要么不限(0)，要么与目标一致
            if (rp != 0L && rp != pid) { continue; }
            if (rs != 0L && rs != sid) { continue; }
            // 生效时间校验
            if (r.getEffectiveTime() != null && r.getEffectiveTime().after(now)) { continue; }
            // 优先级：课程精确(2) 权重高于门店精确(1)，L1=3 > L2=2 > L3=1 > L4=0
            int rank = (rp != 0L ? 2 : 0) + (rs != 0L ? 1 : 0);
            if (rank > bestRank) {
                bestRank = rank;
                best = r;
            }
        }
        return best;
    }

    private void validateType(PtCoachFeeRuleEntity e) {
        if (StringUtils.isBlank(e.getRuleName())) {
            throw new RRException("规则名称不能为空");
        }
        if (e.getRuleType() == null) {
            throw new RRException("请选择规则类型");
        }
        if (!Integer.valueOf(1).equals(e.getRuleType()) && !Integer.valueOf(2).equals(e.getRuleType())) {
            throw new RRException("规则类型非法");
        }
        if (e.getCommissionRate() == null
                || e.getCommissionRate().compareTo(BigDecimal.ZERO) <= 0
                || e.getCommissionRate().compareTo(new BigDecimal("100")) > 0) {
            throw new RRException("提成比例必须在 (0,100] 之间");
        }
        // 新规则统一按比例结算，固定课时费字段仅保留用于读取历史数据。
        e.setLessonFee(BigDecimal.ZERO);
    }

    /** 普通提成规则不能直接绑定包月商品，包月商品必须走教练自己的包月配置。 */
    private void validateOrdinaryProduct(Long productId) {
        if (productId == null || productId <= 0) {
            return;
        }
        if (ptCoachMonthlyCommissionRuleDao.countMonthlyProduct(productId) > 0) {
            throw new RRException("普通提成规则不适用于包月课程，请在教练资料中配置包月课程提成");
        }
    }

    private void ensureOnlyOneEnabledType(Long coachId, Long productId, Long storeId,
                                          Integer ruleType, Long excludeId) {
        if (ptCoachFeeRuleDao.countEnabledOtherType(coachId, productId, storeId, ruleType, excludeId) > 0) {
            throw new RRException("同一教练下作用范围重叠的门店和课程只能启用一种提成方式");
        }
    }

    /** 按ID顺序锁定涉及的教练行，避免规则迁移教练时出现交叉死锁。 */
    private void lockCoachScope(Long oldCoachId, Long newCoachId) {
        if (oldCoachId == null && newCoachId == null) {
            return;
        }
        if (oldCoachId == null || oldCoachId.equals(newCoachId)) {
            ptCoachFeeRuleDao.lockCoachForUpdate(newCoachId);
            return;
        }
        if (newCoachId == null) {
            ptCoachFeeRuleDao.lockCoachForUpdate(oldCoachId);
            return;
        }
        Long first = oldCoachId < newCoachId ? oldCoachId : newCoachId;
        Long second = oldCoachId < newCoachId ? newCoachId : oldCoachId;
        ptCoachFeeRuleDao.lockCoachForUpdate(first);
        ptCoachFeeRuleDao.lockCoachForUpdate(second);
    }

    private List<Long> singletonZero() {
        List<Long> list = new ArrayList<>(1);
        list.add(0L);
        return list;
    }
}
