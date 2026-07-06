package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtGroupToPrivateRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 团课转私教识别规则 Dao（pt_group_to_private_rule，第22步·运营域）。
 * sys 目录 XML 热刷新免重启。出勤维度/购课维度至少配一组。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtGroupToPrivateRuleDao extends BaseDao<PtGroupToPrivateRuleEntity> {
}
