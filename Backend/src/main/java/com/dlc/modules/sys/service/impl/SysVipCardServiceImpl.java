package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.VipBenefitCardDao;
import com.dlc.modules.sys.entity.VipBenefitCardEntity;
import com.dlc.modules.sys.service.SysVipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡商品(vip_benefit_card)后台服务实现
 *
 * @date 2026-06-28
 */
@Service("sysVipCardService")
public class SysVipCardServiceImpl implements SysVipCardService {

    @Autowired
    private VipBenefitCardDao vipBenefitCardDao;

    @Override
    public VipBenefitCardEntity queryObject(Long vipCardId) {
        return vipBenefitCardDao.queryObject(vipCardId);
    }

    @Override
    public List<VipBenefitCardEntity> queryList(Map<String, Object> map) {
        return vipBenefitCardDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return vipBenefitCardDao.queryTotal(map);
    }

    @Override
    public void save(VipBenefitCardEntity entity) {
        applyDefaults(entity);
        // sold_count 系统维护,后台不可写:落库前强制 0(XML 亦硬编码 0 兜底)
        entity.setSoldCount(0);
        if (entity.getStatus() == null) {
            entity.setStatus(1); // 默认上架
        }
        entity.setCreatedDate(new Date());
        vipBenefitCardDao.save(entity);
    }

    @Override
    public void update(VipBenefitCardEntity entity) {
        applyDefaults(entity);
        // soldCount 不参与更新(XML update 不含该列),无需处理
        vipBenefitCardDao.update(entity);
    }

    @Override
    public void deleteBatch(Long[] vipCardIds) {
        vipBenefitCardDao.deleteBatch(vipCardIds);
    }

    @Override
    public int benefitCount(Long vipCardId) {
        return vipBenefitCardDao.benefitCount(vipCardId);
    }

    @Override
    public int updateOnOffCard(Long vipCardId, Integer status) {
        return vipBenefitCardDao.updateOnOffCard(vipCardId, status);
    }

    /**
     * 为 NOT NULL DEFAULT 字段补默认值,避免前端漏传导致 SQL 约束异常。
     * 动态定价字段缺省即"不动态涨价"。
     */
    private void applyDefaults(VipBenefitCardEntity entity) {
        if (entity.getValidityDays() == null) {
            entity.setValidityDays(365);
        }
        if (entity.getShowBuyCount() == null) {
            entity.setShowBuyCount(1);
        }
        if (entity.getBaseBuyCount() == null) {
            entity.setBaseBuyCount(0);
        }
        if (entity.getStepNum() == null) {
            entity.setStepNum(0);
        }
        if (entity.getStepAddPrice() == null) {
            entity.setStepAddPrice(BigDecimal.ZERO);
        }
    }
}
