package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益后台 Dao(pt_member_private_benefit,第15步)。
 * 课时字段仍禁止后台写入;本 Dao 只处理到期日、权益门店和所属服务人的批量管理动作。
 * 对应 mapper/sys/SysMemberBenefitDao.xml(sys 目录热刷新)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysMemberBenefitDao {

    /** 分页列表:联会员/商品/门店/来源订单;门店隔离 storeAddrIds 空=超管不过滤 */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /**
     * 顶部统计卡(SQL 聚合一把出):生效中/已用完/已过期/已退款各数量 + 剩余/冻结课时合计。
     * 口径:沿用列表除 status 外的全部筛选(状态维度由 CASE 分桶,不吃 status 过滤)。
     */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 服务人候选:只返回启用且属于当前管理员门店范围的私教。 */
    List<Map<String, Object>> queryCoachOptions(@Param("keyword") String keyword,
                                                @Param("storeIds") String storeIds);

    /** 详情(权益+来源订单号+课时四态);storeAddrIds 过滤在 SQL 内收口,越权/不存在返回 null → 404 */
    Map<String, Object> queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 可调整到期日的权益数:必须存在、在门店权限内、非退款且有明确到期日。 */
    int countAdjustableBenefits(@Param("benefitIds") List<Long> benefitIds,
                                @Param("storeAddrIds") String storeAddrIds);

    /** 批量按每条记录原 expire_at 增减天数。 */
    int updateExpireDateBatch(@Param("benefitIds") List<Long> benefitIds,
                              @Param("offsetDays") Integer offsetDays,
                              @Param("storeAddrIds") String storeAddrIds);

    /** 可变更门店的权益数:存在、在门店权限内且不是已退款记录。 */
    int countChangeableBenefits(@Param("benefitIds") List<Long> benefitIds,
                                @Param("storeAddrIds") String storeAddrIds);

    /** 校验目标门店地址是否存在、启用且属于当前管理员权限。 */
    int countStoreAddressInScope(@Param("storeAddrId") Long storeAddrId,
                                 @Param("storeAddrIds") String storeAddrIds);

    /** 批量更新权益的未来归属门店。 */
    int updateStoreBatch(@Param("benefitIds") List<Long> benefitIds,
                         @Param("targetStoreAddrId") Long targetStoreAddrId,
                         @Param("storeAddrIds") String storeAddrIds);

    /** 校验目标服务人是否有效且覆盖所选权益当前所属门店。 */
    int countCoachChangeableBenefits(@Param("benefitIds") List<Long> benefitIds,
                                     @Param("coachId") Long coachId,
                                     @Param("storeAddrIds") String storeAddrIds);

    /** 批量变更权益的所属服务人;targetCoachId 为空时清空服务人。 */
    int updateCoachBatch(@Param("benefitIds") List<Long> benefitIds,
                         @Param("targetCoachId") Long targetCoachId,
                         @Param("storeAddrIds") String storeAddrIds);
}
