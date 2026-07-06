package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtGroupToPrivateLeadEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 团课转私教转化名单 Dao（pt_group_to_private_lead，第22步·运营域）。
 * sys 侧做后台名单列表 + 发券/跟进/转化处理；扫描 upsert 在 api 侧 GroupToPrivateScanDao。
 * 门店隔离：storeIds 为逗号分隔串，空=超管看全部。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtGroupToPrivateLeadDao extends BaseDao<PtGroupToPrivateLeadEntity> {

    /** 详情（门店隔离收口，越权/不存在返回 null） */
    PtGroupToPrivateLeadEntity queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 判存（发券/跟进/转化前越权校验：0=不存在或不在管辖门店） */
    int countInScope(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 发券状态机推进：experience_coupon_status 0→1（仅当当前为0且未转化时命中，防重发） */
    int markCouponSent(@Param("leadId") Long leadId);

    /** 跟进：回写 follow_status/follow_by/last_follow_time */
    int updateFollow(@Param("leadId") Long leadId, @Param("followStatus") Integer followStatus,
                     @Param("followBy") Long followBy);

    /** 标记已转化：follow_status→2 + last_follow_time=now（幂等，已转化再点无副作用） */
    int markConverted(@Param("leadId") Long leadId);
}
