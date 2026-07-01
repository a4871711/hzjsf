package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.vo.PtScheduleWindowVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教教练会员端只读浏览 Dao（可约教练计算 + 排班读取）
 *
 * @author claude
 */
@Mapper
@Repository
public interface CoachApiDao {

    /**
     * 需求 8.3 交集：商品适用门店 ∩ 教练所属门店 ∩ 教练正常状态 ∩ 教练有排班 ∩ 商品指定教练范围(为空则不限)。
     */
    List<PtCoachOption> queryBookableCoaches(@Param("productId") Long productId);

    /** 教练在某门店某星期的启用排班窗口 */
    List<PtScheduleWindowVo> queryEnabledSchedule(@Param("coachId") Long coachId, @Param("storeId") Long storeId,
                                                  @Param("weekday") Integer weekday);

    /** 商品适用门店ID集合，用于校验前端传入 storeId 合法性 */
    List<Long> queryProductStoreIds(Long productId);

    /** 教练所属门店ID集合，用于校验前端传入 storeId 合法性 */
    List<Long> queryCoachStoreIds(Long coachId);
}
