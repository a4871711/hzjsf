package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachScheduleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 教练固定周排班 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachScheduleDao extends BaseDao<PtCoachScheduleEntity> {

    /**
     * 重叠校验：同(教练,门店,星期)下，启用段中是否存在与[startTime,endTime)半开区间重叠的记录。
     * excludeId 为空表示新增。
     */
    int countOverlap(@Param("coachId") Long coachId, @Param("storeId") Long storeId,
                     @Param("weekday") Integer weekday, @Param("startTime") String startTime,
                     @Param("endTime") String endTime, @Param("excludeId") Long excludeId);

    /** 校验门店是否属于该教练（pt_coach_store_rel） */
    int countCoachStore(@Param("coachId") Long coachId, @Param("storeId") Long storeId);

    /**
     * 商品上架校验用：统计「适用门店(storeIds) 内、状态正常、有启用排班」的教练数。
     * coachIdsFilter 非空时限定在该教练集合内（对应商品指定教练场景）；为空表示不限教练。
     * 供商品域 checkCanList 跨域只读复用。
     */
    int countBookableCoaches(@Param("storeIds") String storeIds, @Param("coachIdsFilter") String coachIdsFilter);
}
