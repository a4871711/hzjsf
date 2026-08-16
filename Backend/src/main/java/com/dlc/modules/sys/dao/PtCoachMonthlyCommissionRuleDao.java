package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachMonthlyCommissionRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 教练包月课程提成配置 Dao。 */
@Mapper
@Repository
public interface PtCoachMonthlyCommissionRuleDao {

    List<PtCoachMonthlyCommissionRuleEntity> queryByCoachId(@Param("coachId") Long coachId);

    PtCoachMonthlyCommissionRuleEntity queryByCoachAndProduct(@Param("coachId") Long coachId,
                                                               @Param("productId") Long productId);

    List<PtCoachMonthlyCommissionRuleEntity> queryMonthlyProductOptions();

    int countMonthlyProduct(@Param("productId") Long productId);

    int countByProduct(@Param("productId") Long productId);

    int save(PtCoachMonthlyCommissionRuleEntity entity);

    int deleteByCoachId(@Param("coachId") Long coachId);
}
