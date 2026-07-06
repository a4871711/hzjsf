package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtRenewalWarningRuleStoreRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 续费预警规则适用门店关联 Dao（pt_renewal_warning_rule_store_rel，第22步·运营域）。
 * 编辑全删全插；无记录=全部门店。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtRenewalWarningRuleStoreRelDao extends BaseDao<PtRenewalWarningRuleStoreRelEntity> {

    int deleteByRuleId(Long ruleId);

    List<Long> queryStoreIds(Long ruleId);
}
