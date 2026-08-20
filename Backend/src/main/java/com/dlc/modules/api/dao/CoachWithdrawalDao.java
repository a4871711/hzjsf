package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtCoachWithdrawalEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/** 私教提现共享 Dao；会员端申请和后台审核共用同一张提现台账。 */
@Mapper
@Repository
public interface CoachWithdrawalDao {

    /** 锁住教练主表，串行化该教练的申请、驳回和审核通过。 */
    Long lockCoach(@Param("coachId") Long coachId);

    /** 收入、已结算和待审核冻结金额。 */
    Map<String, Object> queryBalance(@Param("coachId") Long coachId);

    List<Map<String, Object>> queryCoachList(@Param("coachId") Long coachId,
                                             @Param("offset") Integer offset,
                                             @Param("limit") Integer limit);

    int countCoachList(@Param("coachId") Long coachId);

    int save(PtCoachWithdrawalEntity entity);

    /** 必须在事务内调用，读取待审核记录并加行锁。 */
    Map<String, Object> queryForUpdate(@Param("id") Long id);

    List<Map<String, Object>> queryAdminList(Map<String, Object> params);

    int countAdminList(Map<String, Object> params);

    Map<String, Object> queryAdminObject(@Param("id") Long id, @Param("storeIds") String storeIds);

    int approve(PtCoachWithdrawalEntity entity);

    int reject(PtCoachWithdrawalEntity entity);
}
