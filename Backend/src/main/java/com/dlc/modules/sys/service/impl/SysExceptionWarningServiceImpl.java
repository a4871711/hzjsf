package com.dlc.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.dao.PtExceptionWarningRecordDao;
import com.dlc.modules.sys.dao.PtExceptionWarningRuleDao;
import com.dlc.modules.sys.dao.SysStoreDao;
import com.dlc.modules.sys.entity.PtExceptionWarningRuleEntity;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.service.SysExceptionWarningService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异常预警后台 Service 实现（第22步·运营域·异常预警）。
 * applicable_store_ids JSON 化：空数组=全部门店存 NULL；info 反序列化为数组、并回填 storeNames 展示。
 * 记录只读，不做处理态流转（§18.4）。事务由 sys.service.impl 切面统一管理（REQUIRED）。
 * 门店名解析复用 SysStoreDao.queryObject(逗号分隔ids)（只读、best-effort）。
 *
 * @author claude
 */
@Service("sysExceptionWarningService")
public class SysExceptionWarningServiceImpl implements SysExceptionWarningService {

    @Autowired
    private PtExceptionWarningRuleDao ptExceptionWarningRuleDao;
    @Autowired
    private PtExceptionWarningRecordDao ptExceptionWarningRecordDao;
    @Autowired
    private SysStoreDao sysStoreDao;

    @Override
    public PageUtils queryRulePage(Query query) {
        List<PtExceptionWarningRuleEntity> list = ptExceptionWarningRuleDao.queryList(query);
        for (PtExceptionWarningRuleEntity rule : list) {
            fillStoreNames(rule);
        }
        int total = ptExceptionWarningRuleDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public PtExceptionWarningRuleEntity queryRule(Long id) {
        PtExceptionWarningRuleEntity rule = ptExceptionWarningRuleDao.queryObject(id);
        if (rule == null) {
            return null;
        }
        rule.setApplicableStoreIdList(parseStoreIds(rule.getApplicableStoreIds()));
        fillStoreNames(rule);
        return rule;
    }

    @Override
    public void saveRule(PtExceptionWarningRuleEntity rule) {
        validate(rule);
        serializeStoreIds(rule);
        applyDefaults(rule);
        Date now = new Date();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        ptExceptionWarningRuleDao.save(rule);
    }

    @Override
    public void updateRule(PtExceptionWarningRuleEntity rule) {
        if (rule.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        PtExceptionWarningRuleEntity old = ptExceptionWarningRuleDao.queryObject(rule.getId());
        if (old == null) {
            throw new RRException("异常预警规则不存在");
        }
        validate(rule);
        serializeStoreIds(rule);
        applyDefaults(rule);
        rule.setUpdatedAt(new Date());
        ptExceptionWarningRuleDao.update(rule);
    }

    @Override
    public void deleteRule(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        ptExceptionWarningRuleDao.deleteBatch(ids);
    }

    @Override
    public PageUtils queryRecordPage(Query query) {
        List<?> list = ptExceptionWarningRecordDao.queryList(query);
        int total = ptExceptionWarningRecordDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public Map<String, Object> queryStat(Map<String, Object> params) {
        return ptExceptionWarningRecordDao.queryStat(params);
    }

    /* ============ 私有辅助 ============ */

    private void validate(PtExceptionWarningRuleEntity e) {
        if (StringUtils.isBlank(e.getRuleName())) {
            throw new RRException("规则名称不能为空");
        }
        if (e.getRuleName().trim().length() > 100) {
            throw new RRException("规则名称不能超过100字");
        }
        if (e.getWarningType() == null
                || (!Integer.valueOf(1).equals(e.getWarningType()) && !Integer.valueOf(2).equals(e.getWarningType()))) {
            throw new RRException("预警类型须为 1频繁取消预约 或 2课时消耗异常");
        }
        if (e.getPeriodDays() == null || e.getPeriodDays() <= 0) {
            throw new RRException("统计周期天数必须大于0");
        }
        if (e.getTriggerThreshold() == null || e.getTriggerThreshold() <= 0) {
            throw new RRException("触发阈值必须大于0");
        }
    }

    private void applyDefaults(PtExceptionWarningRuleEntity e) {
        if (e.getStatus() == null) { e.setStatus(1); }
        if (e.getStatus() != 0 && e.getStatus() != 1) {
            throw new RRException("状态不合法（1启用/0停用）");
        }
    }

    /** 前端传 applicableStoreIdList → 序列化到持久列 applicableStoreIds；空数组=全部门店=NULL */
    private void serializeStoreIds(PtExceptionWarningRuleEntity e) {
        List<Long> ids = e.getApplicableStoreIdList();
        if (ids == null || ids.isEmpty()) {
            e.setApplicableStoreIds(null);
        } else {
            e.setApplicableStoreIds(JSON.toJSONString(ids));
        }
    }

    private List<Long> parseStoreIds(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(json, Long.class);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /** 回填门店名展示（空=全部门店，storeNames 留空由前端显示"全部门店"） */
    private void fillStoreNames(PtExceptionWarningRuleEntity rule) {
        List<Long> ids = parseStoreIds(rule.getApplicableStoreIds());
        if (ids == null || ids.isEmpty()) {
            return;
        }
        StringBuilder idStr = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) { idStr.append(','); }
            idStr.append(ids.get(i));
        }
        List<SysStoreEntity> stores = sysStoreDao.queryObject(idStr.toString());
        if (stores == null || stores.isEmpty()) {
            return;
        }
        StringBuilder names = new StringBuilder();
        for (SysStoreEntity s : stores) {
            if (names.length() > 0) { names.append(','); }
            names.append(s.getStoreName());
        }
        rule.setStoreNames(names.toString());
    }
}
