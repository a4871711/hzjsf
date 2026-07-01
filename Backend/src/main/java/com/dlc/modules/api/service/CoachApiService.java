package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.vo.PtAvailableSlotVo;

import java.util.List;

/**
 * 私教教练会员端只读浏览 Service（可约教练计算 + 可约时段生成）
 *
 * @author claude
 */
public interface CoachApiService {

    /** 需求 8.3 交集：返回该商品当前可预约的教练列表 */
    List<PtCoachOption> listByProduct(Long productId);

    /**
     * 需求 5.4 时段生成规则：按教练在该门店该日期对应星期的启用排班窗口，
     * 结合商品单次服务时长 + 预约间隔切片，并按 latestBookingHours 过滤过近时段。
     */
    List<PtAvailableSlotVo> availableSlots(Long productId, Long coachId, Long storeId, String date);
}
