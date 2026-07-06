package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtRenewalWarningRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 续费预警规则 Dao（pt_renewal_warning_rule，第22步·运营域）。
 * sys 目录 XML 热刷新免重启。save/update 单事务写主表 + 门店 rel（全删全插）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtRenewalWarningRuleDao extends BaseDao<PtRenewalWarningRuleEntity> {
}
