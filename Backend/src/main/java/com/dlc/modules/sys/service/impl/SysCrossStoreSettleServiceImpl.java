package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtCrossStoreSettlementRuleDao;
import com.dlc.modules.sys.entity.PtCrossStoreSettlementRuleEntity;
import com.dlc.modules.sys.service.SysCrossStoreSettleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 跨店结算规则 Service 实现。事务由 sys.service.impl 切面统一管理（REQUIRED）。
 * 全系统单套规则，saveOrUpdate 走 upsert；比例校验用 BigDecimal 精确到分。
 * 本期仅配置不落账：收入报表不按本规则拆分（第23步 §17.1/§17.3，报表 SQL 见 PtPrivateReportDao.xml）。
 *
 * @author claude
 */
@Service("sysCrossStoreSettleService")
public class SysCrossStoreSettleServiceImpl implements SysCrossStoreSettleService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private PtCrossStoreSettlementRuleDao ptCrossStoreSettlementRuleDao;

    @Override
    public PtCrossStoreSettlementRuleEntity queryCurrent() {
        return ptCrossStoreSettlementRuleDao.queryCurrent();
    }

    @Override
    public void saveOrUpdate(PtCrossStoreSettlementRuleEntity entity, Long operatorId) {
        if (entity == null) {
            throw new RRException("规则数据不能为空");
        }
        if (StringUtils.isBlank(entity.getRuleName())) {
            throw new RRException("规则名称不能为空");
        }
        if (entity.getIncomeOwnerType() == null) {
            throw new RRException("请选择收入归属方式");
        }
        // 教练课时费归属固定为 1 授课教练
        entity.setCoachFeeOwnerType(1);
        // 是否启用跨店结算：默认启用
        if (entity.getCrossStoreEnabled() == null) {
            entity.setCrossStoreEnabled(1);
        }
        // 规则状态：默认启用
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        // 比例校验 + 强制值（按收入归属方式）
        normalizeAndValidateRatio(entity);

        PtCrossStoreSettlementRuleEntity current = ptCrossStoreSettlementRuleDao.queryCurrent();
        Date now = new Date();
        if (current == null) {
            // 无则 insert
            entity.setId(null);
            entity.setCreatedBy(operatorId);
            entity.setCreatedAt(now);
            entity.setUpdatedBy(operatorId);
            entity.setUpdatedAt(now);
            ptCrossStoreSettlementRuleDao.save(entity);
        } else {
            // 有则 update（更新当前唯一一条，保证全局一条）
            entity.setId(current.getId());
            entity.setUpdatedBy(operatorId);
            entity.setUpdatedAt(now);
            ptCrossStoreSettlementRuleDao.update(entity);
        }
    }

    /**
     * 分成比例校验与强制归一：
     * - incomeOwnerType=3 按比例分成：buyStoreRatio+lessonStoreRatio 必须精确等于 100（BigDecimal 比较），
     *   两值非空且 [0,100]。
     * - incomeOwnerType=1 购买门店：强制 (100, 0)。
     * - incomeOwnerType=2 上课门店：强制 (0, 100)。
     */
    private void normalizeAndValidateRatio(PtCrossStoreSettlementRuleEntity e) {
        int type = e.getIncomeOwnerType();
        if (type == 3) {
            BigDecimal buy = e.getBuyStoreRatio();
            BigDecimal lesson = e.getLessonStoreRatio();
            if (buy == null || lesson == null) {
                throw new RRException("按比例分成时购买门店与上课门店比例均不能为空");
            }
            if (buy.compareTo(ZERO) < 0 || buy.compareTo(HUNDRED) > 0
                    || lesson.compareTo(ZERO) < 0 || lesson.compareTo(HUNDRED) > 0) {
                throw new RRException("分成比例须在 0~100 之间");
            }
            // BigDecimal 用 compareTo 忽略标度（100 与 100.00 视为相等）
            if (buy.add(lesson).compareTo(HUNDRED) != 0) {
                throw new RRException("分成比例合计必须为100%");
            }
        } else if (type == 1) {
            // 购买门店：强制 (100, 0)
            e.setBuyStoreRatio(HUNDRED);
            e.setLessonStoreRatio(ZERO);
        } else if (type == 2) {
            // 上课门店：强制 (0, 100)
            e.setBuyStoreRatio(ZERO);
            e.setLessonStoreRatio(HUNDRED);
        } else {
            throw new RRException("收入归属方式非法");
        }
    }
}
