package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教练课时费/分成规则 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachFeeRuleDao extends BaseDao<PtCoachFeeRuleEntity> {

    /** 锁定教练主记录，串行化同一教练的分成规则写入，事务结束自动释放 */
    Long lockCoachForUpdate(@Param("coachId") Long coachId);

    /** 锁定待修改规则，避免编辑/启停并发覆盖 */
    PtCoachFeeRuleEntity queryObjectForUpdate(@Param("id") Long id);

    /** 唯一键预检：同 coach+product+store+ruleType 是否已存在（excludeId 为空=新增） */
    int countByUk(@Param("coachId") Long coachId, @Param("productId") Long productId,
                  @Param("storeId") Long storeId, @Param("ruleType") Integer ruleType,
                  @Param("excludeId") Long excludeId);

    /** 校验同一教练下，门店/课程作用范围有重叠时是否已启用另一种提成方式 */
    int countEnabledOtherType(@Param("coachId") Long coachId, @Param("productId") Long productId,
                              @Param("storeId") Long storeId, @Param("ruleType") Integer ruleType,
                              @Param("excludeId") Long excludeId);

    /** 取某教练全部启用规则，供 matchFeeRule 在内存按优先级匹配 */
    List<PtCoachFeeRuleEntity> queryEnabledByCoach(@Param("coachId") Long coachId,
                                                   @Param("ruleType") Integer ruleType);
}
