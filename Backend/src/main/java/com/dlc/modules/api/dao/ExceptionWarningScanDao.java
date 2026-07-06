package com.dlc.modules.api.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 异常预警扫描 Dao（api 侧，第22步·运营域，依据 §5.2）。
 * 读 pt_exception_warning_rule(status=1)，按周期聚合 pt_private_appointment 命中阈值的会员，
 * 去重靠 uk_exc_warn_dedup + ON DUPLICATE KEY UPDATE。
 * ⚠️ api 目录 XML 改动必须重启 Tomcat 生效。
 *
 * @author claude
 */
@Mapper
@Repository
public interface ExceptionWarningScanDao {

    /** 启用中的异常预警规则 */
    List<Map<String, Object>> selectEnabledRules();

    /**
     * 频繁取消预约(warning_type=1)：周期内 [periodStart, periodEnd] 按会员统计取消次数 >= 阈值。
     * storeIds 为空则不过滤门店。返回 memberId/storeId/coachId/cnt。
     */
    List<Map<String, Object>> countFrequentCancel(@Param("periodStart") String periodStart,
                                                   @Param("periodEnd") String periodEnd,
                                                   @Param("threshold") Integer threshold,
                                                   @Param("storeIds") List<Long> storeIds);

    /**
     * 课时消耗异常(warning_type=2)：周期内按会员统计已完成预约次数 >= 阈值。
     */
    List<Map<String, Object>> countLessonAbnormal(@Param("periodStart") String periodStart,
                                                   @Param("periodEnd") String periodEnd,
                                                   @Param("threshold") Integer threshold,
                                                   @Param("storeIds") List<Long> storeIds);

    /** 幂等落记录：INSERT ... ON DUPLICATE KEY UPDATE（撞 uk_exc_warn_dedup 则更新触发值/说明/预警时间） */
    int upsertRecord(Map<String, Object> params);
}
