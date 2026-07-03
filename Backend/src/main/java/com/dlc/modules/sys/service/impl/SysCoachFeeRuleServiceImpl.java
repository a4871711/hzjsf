package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtCoachFeeRuleDao;
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
                if (ptCoachFeeRuleDao.countByUk(entity.getCoachId(), pid, sid, entity.getRuleType(), null) > 0) {
                    throw new RRException("规则已存在（教练+门店+课程+类型重复）");
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
                row.setStatus(entity.getStatus() == null ? 1 : entity.getStatus());
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
        PtCoachFeeRuleEntity old = ptCoachFeeRuleDao.queryObject(entity.getId());
        if (old == null) {
            throw new RRException("规则不存在");
        }
        // 单行编辑：以提交值与旧值合并后校验类型
        Integer ruleType = entity.getRuleType() != null ? entity.getRuleType() : old.getRuleType();
        Long storeId = entity.getStoreId() != null ? entity.getStoreId() : old.getStoreId();
        Long productId = entity.getProductId() != null ? entity.getProductId() : old.getProductId();
        entity.setRuleType(ruleType);
        validateType(entity);
        if (ptCoachFeeRuleDao.countByUk(old.getCoachId(), productId, storeId, ruleType, entity.getId()) > 0) {
            throw new RRException("规则已存在（教练+门店+课程+类型重复）");
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
        if (Integer.valueOf(1).equals(e.getRuleType())) {
            if (e.getLessonFee() == null || e.getLessonFee().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RRException("课时费规则的单次课时费必须大于0");
            }
        } else if (Integer.valueOf(2).equals(e.getRuleType())) {
            if (e.getCommissionRate() == null
                    || e.getCommissionRate().compareTo(BigDecimal.ZERO) <= 0
                    || e.getCommissionRate().compareTo(new BigDecimal("100")) > 0) {
                throw new RRException("销售提成比例必须在 (0,100] 之间");
            }
        } else {
            throw new RRException("规则类型非法");
        }
    }

    private List<Long> singletonZero() {
        List<Long> list = new ArrayList<>(1);
        list.add(0L);
        return list;
    }
}
