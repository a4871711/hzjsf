package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtExceptionWarningRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 异常预警规则 Dao（pt_exception_warning_rule，第22步·运营域）。
 * sys 目录 XML 热刷新免重启。applicable_store_ids 以 JSON 串持久（NULL=全部门店）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtExceptionWarningRuleDao extends BaseDao<PtExceptionWarningRuleEntity> {
}
