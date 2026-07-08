package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.VipPauseRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * VIP 停卡规则 Dao
 */
@Mapper
@Repository
public interface VipPauseRuleDao extends BaseDao<VipPauseRuleEntity> {

    /** 统计某停卡规则被多少个权益卡引用(vip_benefit_card.pause_rule_id),>0 则不允许删除 */
    int countCardRefByRuleId(@Param("pauseRuleId") Long pauseRuleId);
}
