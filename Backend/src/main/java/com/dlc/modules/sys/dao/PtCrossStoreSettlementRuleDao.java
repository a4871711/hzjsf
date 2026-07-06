package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCrossStoreSettlementRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 跨店结算规则 Dao（全系统单套规则，get/upsert）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCrossStoreSettlementRuleDao extends BaseDao<PtCrossStoreSettlementRuleEntity> {

    /** 取当前唯一一条规则（id 最小）；无则返回 null。 */
    PtCrossStoreSettlementRuleEntity queryCurrent();
}
