package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.VipBenefitCardEntity;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡商品(vip_benefit_card)后台服务
 *
 * @date 2026-06-28
 */
public interface SysVipCardService {

    VipBenefitCardEntity queryObject(Long vipCardId);

    List<VipBenefitCardEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    /** 新增:强制 soldCount=0、补默认值、setCreatedDate */
    void save(VipBenefitCardEntity entity);

    /** 全量更新基础信息与动态定价字段;soldCount 不参与更新 */
    void update(VipBenefitCardEntity entity);

    void deleteBatch(Long[] vipCardIds);

    /** 该权益卡已产生的持有实例数(>0 表示已售出,不可删) */
    int benefitCount(Long vipCardId);

    /** 上/下架 1上架 2下架 */
    int updateOnOffCard(Long vipCardId, Integer status);
}
