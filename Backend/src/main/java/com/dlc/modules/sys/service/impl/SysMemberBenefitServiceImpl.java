package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.SysMemberBenefitDao;
import com.dlc.modules.sys.service.SysMemberBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 会员私教权益后台 Service 实现。
 * 课时字段不在后台动作中修改;批量动作只调整到期日、权益未来归属门店和所属服务人。
 */
@Service("sysMemberBenefitService")
public class SysMemberBenefitServiceImpl implements SysMemberBenefitService {

    @Autowired
    private SysMemberBenefitDao sysMemberBenefitDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysMemberBenefitDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysMemberBenefitDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryStat(Map<String, Object> params) {
        return sysMemberBenefitDao.queryStat(params);
    }

    @Override
    public List<Map<String, Object>> queryCoachOptions(String keyword, String storeIds) {
        return sysMemberBenefitDao.queryCoachOptions(keyword, storeIds);
    }

    @Override
    public Map<String, Object> queryDetail(Long id, String storeIds) {
        return sysMemberBenefitDao.queryDetail(id, storeIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdjustExpireDate(List<Long> benefitIds, String operation, Integer days, String storeAddrIds) {
        List<Long> normalizedIds = normalizeBenefitIds(benefitIds);
        int offsetDays = toSignedOffset(operation, days);
        if (sysMemberBenefitDao.countAdjustableBenefits(normalizedIds, storeAddrIds) != normalizedIds.size()) {
            throw new RRException("所选权益中存在不存在、已退款或不在门店权限范围内的记录，批量操作已取消");
        }
        int updatedRows = sysMemberBenefitDao.updateExpireDateBatch(normalizedIds, offsetDays, storeAddrIds);
        if (updatedRows != normalizedIds.size()) {
            throw new RRException("批量调整到期时间失败，未修改任何不完整数据");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchChangeStore(List<Long> benefitIds, Long targetStoreAddrId, String storeAddrIds) {
        List<Long> normalizedIds = normalizeBenefitIds(benefitIds);
        if (targetStoreAddrId == null || targetStoreAddrId <= 0
                || sysMemberBenefitDao.countStoreAddressInScope(targetStoreAddrId, storeAddrIds) != 1) {
            throw new RRException("目标门店不存在或不在门店权限范围内");
        }
        if (sysMemberBenefitDao.countChangeableBenefits(normalizedIds, storeAddrIds) != normalizedIds.size()) {
            throw new RRException("所选权益中存在不存在、已退款或不在门店权限范围内的记录，批量操作已取消");
        }
        int updatedRows = sysMemberBenefitDao.updateStoreBatch(normalizedIds, targetStoreAddrId, storeAddrIds);
        if (updatedRows != normalizedIds.size()) {
            throw new RRException("批量变更门店失败，未修改任何不完整数据");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchChangeCoach(List<Long> benefitIds, Long targetCoachId, String storeAddrIds) {
        List<Long> normalizedIds = normalizeBenefitIds(benefitIds);
        if (targetCoachId != null && targetCoachId <= 0) {
            throw new RRException("目标服务人不合法");
        }
        if (sysMemberBenefitDao.countChangeableBenefits(normalizedIds, storeAddrIds)
                != normalizedIds.size()) {
            throw new RRException("所选权益中存在不存在、已退款或不在门店权限范围内的记录，批量操作已取消");
        }
        // 服务人必须同时属于每条权益当前门店;空值表示明确清空所属服务人。
        if (targetCoachId != null
                && sysMemberBenefitDao.countCoachChangeableBenefits(normalizedIds, targetCoachId, storeAddrIds)
                != normalizedIds.size()) {
            throw new RRException("目标服务人不存在、已停用或未覆盖所选权益的所属门店");
        }
        int updatedRows = sysMemberBenefitDao.updateCoachBatch(normalizedIds, targetCoachId, storeAddrIds);
        if (updatedRows != normalizedIds.size()) {
            throw new RRException("批量变更服务人失败，未修改任何不完整数据");
        }
    }

    /** 只接受明确操作,避免把未知值静默当成增加或减少。 */
    static int toSignedOffset(String operation, Integer days) {
        if (days == null || days <= 0) {
            throw new RRException("调整天数必须是大于 0 的整数");
        }
        if ("increase".equals(operation)) {
            return days;
        }
        if ("decrease".equals(operation)) {
            return -days;
        }
        throw new RRException("有效期调整类型不合法");
    }

    private List<Long> normalizeBenefitIds(List<Long> benefitIds) {
        if (benefitIds == null || benefitIds.isEmpty()) {
            throw new RRException("请至少选择一条权益");
        }
        Set<Long> distinctIds = new LinkedHashSet<Long>();
        for (Long benefitId : benefitIds) {
            if (benefitId == null || benefitId <= 0) {
                throw new RRException("权益编号不合法");
            }
            distinctIds.add(benefitId);
        }
        if (distinctIds.size() != benefitIds.size()) {
            throw new RRException("权益编号不能重复");
        }
        return new ArrayList<Long>(distinctIds);
    }
}
