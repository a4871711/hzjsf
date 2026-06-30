package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.entity.CardPauseRecord;

import java.util.Map;

/**
 * 会员自助停卡 Service(移动端)
 * 开放式:apply 仅置停卡中、暂不顺延;resume 时按实际停卡天数顺延会员卡有效期
 */
public interface CardPauseService {

    /** 申请停卡:校验「每月1次/全年12次」,建停卡中记录 */
    CardPauseRecord apply(Long userId, Long cardOrderId, Integer pauseDays);

    /** 恢复停卡:回填实际天数 + 顺延 card_order.validityDate,幂等;返回 1 首次恢复成功 */
    int resume(Long userId, Long pauseId);

    /** 我的停卡记录分页 */
    PageUtils myList(Map<String, Object> params);
}
