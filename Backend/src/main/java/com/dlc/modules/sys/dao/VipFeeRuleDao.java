package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.VipFeeRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * VIP 转让费用规则 Dao
 */
@Mapper
@Repository
public interface VipFeeRuleDao extends BaseDao<VipFeeRuleEntity> {

    /** 统计某费用规则被多少个权益卡引用(vip_benefit_card.fee_rule_id),>0 则不允许删除 */
    int countCardRefByRuleId(@Param("feeRuleId") Long feeRuleId);
}
