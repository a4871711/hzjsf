package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.dao.PtRenewalWarningRecordDao;
import com.dlc.modules.sys.dao.PtRenewalWarningRuleDao;
import com.dlc.modules.sys.dao.PtRenewalWarningRuleStoreRelDao;
import com.dlc.modules.sys.entity.PtRenewalWarningRecordEntity;
import com.dlc.modules.sys.entity.PtRenewalWarningRuleEntity;
import com.dlc.modules.sys.entity.PtRenewalWarningRuleStoreRelEntity;
import com.dlc.modules.sys.service.SysRenewalWarningService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 续费预警后台 Service 实现（第22步·运营域·续费预警）。
 * save/update 一次表单事务内写 rule 主表 + _store_rel（全删全插），事务由 sys.service.impl 切面统一管理（REQUIRED）。
 * 规则「空门店=全部门店」：storeIds 空则不插 rel；扫描端读不到 rel 即视为全部门店。
 * 记录只读；跟进/标记状态门店隔离在 SQL 内收口（越权影响0行，service 不额外判存）。
 *
 * @author claude
 */
@Service("sysRenewalWarningService")
public class SysRenewalWarningServiceImpl implements SysRenewalWarningService {

    @Autowired
    private PtRenewalWarningRuleDao ptRenewalWarningRuleDao;
    @Autowired
    private PtRenewalWarningRuleStoreRelDao ptRenewalWarningRuleStoreRelDao;
    @Autowired
    private PtRenewalWarningRecordDao ptRenewalWarningRecordDao;

    @Override
    public PageUtils queryRulePage(Query query) {
        List<PtRenewalWarningRuleEntity> list = ptRenewalWarningRuleDao.queryList(query);
        int total = ptRenewalWarningRuleDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public PtRenewalWarningRuleEntity queryRule(Long id) {
        PtRenewalWarningRuleEntity rule = ptRenewalWarningRuleDao.queryObject(id);
        if (rule == null) {
            return null;
        }
        rule.setStoreIds(ptRenewalWarningRuleStoreRelDao.queryStoreIds(id));
        return rule;
    }

    @Override
    public void saveRule(PtRenewalWarningRuleEntity rule) {
        validate(rule);
        applyDefaults(rule);
        Date now = new Date();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        ptRenewalWarningRuleDao.save(rule);
        saveStoreRels(rule.getId(), rule.getStoreIds());
    }

    @Override
    public void updateRule(PtRenewalWarningRuleEntity rule) {
        if (rule.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        PtRenewalWarningRuleEntity old = ptRenewalWarningRuleDao.queryObject(rule.getId());
        if (old == null) {
            throw new RRException("续费预警规则不存在");
        }
        validate(rule);
        applyDefaults(rule);
        rule.setUpdatedAt(new Date());
        ptRenewalWarningRuleDao.update(rule);
        ptRenewalWarningRuleStoreRelDao.deleteByRuleId(rule.getId());
        saveStoreRels(rule.getId(), rule.getStoreIds());
    }

    @Override
    public void deleteRule(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        for (Long id : ids) {
            ptRenewalWarningRuleStoreRelDao.deleteByRuleId(id);
        }
        ptRenewalWarningRuleDao.deleteBatch(ids);
    }

    @Override
    public PageUtils queryRecordPage(Query query) {
        List<PtRenewalWarningRecordEntity> list = ptRenewalWarningRecordDao.queryList(query);
        int total = ptRenewalWarningRecordDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public void follow(Long recordId, String followRemark, String storeIds) {
        if (recordId == null) {
            throw new RRException("缺少参数：recordId");
        }
        ptRenewalWarningRecordDao.follow(recordId, followRemark, storeIds);
    }

    @Override
    public void markStatus(Long recordId, Integer followStatus, String storeIds) {
        if (recordId == null) {
            throw new RRException("缺少参数：recordId");
        }
        if (followStatus == null || (followStatus != 2 && followStatus != 3)) {
            throw new RRException("跟进状态须为 2已续费 或 3已忽略");
        }
        ptRenewalWarningRecordDao.markStatus(recordId, followStatus, storeIds);
    }

    /* ============ 私有辅助 ============ */

    private void validate(PtRenewalWarningRuleEntity e) {
        if (StringUtils.isBlank(e.getRuleName())) {
            throw new RRException("规则名称不能为空");
        }
        if (e.getRuleName().trim().length() > 100) {
            throw new RRException("规则名称不能超过100字");
        }
        boolean lessonOn = Integer.valueOf(1).equals(e.getLessonWarningEnabled());
        boolean daysOn = Integer.valueOf(1).equals(e.getDaysWarningEnabled());
        if (!lessonOn && !daysOn) {
            throw new RRException("剩余课时预警与剩余天数预警至少启用一个");
        }
        if (lessonOn && (e.getLessonThreshold() == null || e.getLessonThreshold() <= 0)) {
            throw new RRException("启用剩余课时预警时，课时阈值必须大于0");
        }
        if (daysOn && (e.getDaysThreshold() == null || e.getDaysThreshold() <= 0)) {
            throw new RRException("启用剩余天数预警时，天数阈值必须大于0");
        }
    }

    private void applyDefaults(PtRenewalWarningRuleEntity e) {
        if (e.getLessonWarningEnabled() == null) { e.setLessonWarningEnabled(0); }
        if (e.getDaysWarningEnabled() == null) { e.setDaysWarningEnabled(0); }
        if (e.getStatus() == null) { e.setStatus(1); }
        if (e.getStatus() != 0 && e.getStatus() != 1) {
            throw new RRException("状态不合法（1启用/0停用）");
        }
        // 未启用的维度阈值置 NULL，避免脏值参与扫描
        if (!Integer.valueOf(1).equals(e.getLessonWarningEnabled())) { e.setLessonThreshold(null); }
        if (!Integer.valueOf(1).equals(e.getDaysWarningEnabled())) { e.setDaysThreshold(null); }
    }

    private void saveStoreRels(Long ruleId, List<Long> storeIds) {
        // 空=全部门店，不插 rel
        if (storeIds == null || storeIds.isEmpty()) {
            return;
        }
        Date now = new Date();
        for (Long storeId : storeIds) {
            if (storeId == null) { continue; }
            PtRenewalWarningRuleStoreRelEntity rel = new PtRenewalWarningRuleStoreRelEntity();
            rel.setRuleId(ruleId);
            rel.setStoreId(storeId);
            rel.setCreatedAt(now);
            ptRenewalWarningRuleStoreRelDao.save(rel);
        }
    }
}
