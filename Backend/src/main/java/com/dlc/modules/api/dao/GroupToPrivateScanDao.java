package com.dlc.modules.api.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 团课转私教扫描 Dao（api 侧，第22步·运营域，依据 §5.3）。
 * 读 pt_group_to_private_rule(status=1)，按周期统计团课出勤/购课达阈值的会员，
 * upsert pt_group_to_private_lead（uk_member_id，UPDATE 不覆盖人工 follow_status/experience_coupon_status）。
 * 依赖现有团课系统（只读）：出勤取 stu_teamclass_ship.classStatus=1 已完成、购课取 team_class_order 已支付(payTime 非空)。
 * autoConvertOnPaid 挂私教订单支付回调，标记名单已转化 + 追加跟进流水。
 * ⚠️ api 目录 XML 改动必须重启 Tomcat 生效。
 *
 * @author claude
 */
@Mapper
@Repository
public interface GroupToPrivateScanDao {

    /** 启用中的团课转私教识别规则 */
    List<Map<String, Object>> selectEnabledRules();

    /**
     * 出勤维度：近 attendanceDays 天内团课出勤(已完成 classStatus=1)次数 >= 阈值的会员。
     * 返回 memberId/storeId/cnt。（团课表未接入则返回空=名单为空，属正常）
     */
    List<Map<String, Object>> countAttendance(@Param("days") Integer days,
                                               @Param("threshold") Integer threshold);

    /**
     * 购课维度：近 purchaseDays 天内团课购课(已支付 payTime 非空)次数 >= 阈值的会员。
     * 返回 memberId/storeId/cnt。
     */
    List<Map<String, Object>> countPurchase(@Param("days") Integer days,
                                            @Param("threshold") Integer threshold);

    /**
     * 幂等落名单：INSERT ... ON DUPLICATE KEY UPDATE（撞 uk_pt_group_to_private_lead_member_id）。
     * UPDATE 只刷 attendance_count/purchase_count/intention_reason/rule_id，
     * 不动 follow_status / experience_coupon_status（避免覆盖人工跟进结果）。
     */
    int upsertLead(Map<String, Object> params);

    /* ===== 自动转化（挂私教订单支付成功回调） ===== */

    /** 该会员未转化(follow_status<2)的名单ID，无则 null */
    Long selectUnconvertedLeadId(Long memberId);

    /** 标记名单已转化：follow_status→2 + last_follow_time=now（仅当 follow_status<2 命中，幂等） */
    int autoMarkConverted(Long leadId);

    /** 追加自动转化跟进流水（operator_id=null 表示系统自动） */
    int insertAutoFollow(@Param("leadId") Long leadId, @Param("memberId") Long memberId);
}
