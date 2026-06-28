package com.dlc.modules.sys.dao;


import com.dlc.modules.sys.entity.VipBenefitCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * VIP 权益卡商品(vip_benefit_card)
 * 基础增删改查继承 BaseDao(对应 SQL 在 mapper/sys/VipBenefitCardDao.xml)。
 *
 * @date 2026-06-28
 */
@Mapper
@Repository
public interface VipBenefitCardDao extends BaseDao<VipBenefitCardEntity> {

    /**
     * 统计该权益卡已产生的持有实例(vip_benefit)数量,用于删除前拦截已售出的卡
     */
    int benefitCount(Long vipCardId);

    /**
     * 批量上/下架
     *
     * @param status 1上架 2下架
     */
    int updateOnOffCard(@Param("vipCardId") Long vipCardId,
                        @Param("status") Integer status);
}
