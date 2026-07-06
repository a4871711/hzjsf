package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 续费预警扫描 Dao（api 侧，第22步·运营域）。
 * 读 pt_renewal_warning_rule(status=1) + _store_rel，扫 pt_member_private_benefit(status=1) 命中阈值，
 * 去重靠 benefit_id + closed_at IS NULL 应用层查重，写 pt_renewal_warning_record。
 * ⚠️ api 目录 XML 改动必须重启 Tomcat 生效。
 *
 * @author claude
 */
@Mapper
@Repository
public interface RenewalWarningScanDao {

    /** 启用中的续费预警规则 */
    List<Map<String, Object>> selectEnabledRules();

    /** 规则适用门店ID（空=全部门店） */
    List<Long> selectRuleStoreIds(Long ruleId);

    /** 生效中(status=1)的会员私教权益，按门店过滤（storeIds 为空则不过滤） */
    List<PtMemberPrivateBenefitEntity> selectActiveBenefits(@Param("storeIds") List<Long> storeIds);

    /** 取某权益最近一次预约的教练ID（可空，便于教练跟进） */
    Long selectLatestCoachId(Long benefitId);

    /** 该权益是否已有未关闭预警记录（返回记录ID，无则 null） */
    Long selectOpenRecordId(Long benefitId);

    /** 命中已存在未关闭记录：刷新剩余课时/天数/预警类型/最近预警时间 */
    int updateOpenRecord(Map<String, Object> params);

    /** 新增预警记录 */
    int insertRecord(Map<String, Object> params);

    /**
     * 自动关闭不再命中的记录：权益已非生效中(status!=1)的未关闭预警一律 close。
     * （阈值维度回升的关闭由人工 markStatus 处理；此处只兜底权益本身失效。）
     */
    int autoCloseByBenefit(Long benefitId);
}
