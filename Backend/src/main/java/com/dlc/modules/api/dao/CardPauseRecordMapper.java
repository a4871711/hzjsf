package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.CardPauseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 停卡记录(card_pause_record)Mapper(移动端)
 */
public interface CardPauseRecordMapper {

    /** 申请停卡:建停卡中记录(status=0) */
    int insertSelective(CardPauseRecord record);

    /** 行锁被停的会员卡(并发串行化),只取校验所需字段 */
    CardOrder selectCardForUpdate(@Param("cardOrderId") Long cardOrderId);

    /** 该卡当前自然月已有停卡记录数(挡每月1次,与库层 uk_card_month 双保险) */
    int countByCardMonth(@Param("cardOrderId") Long cardOrderId, @Param("pauseMonth") String pauseMonth);

    /** 该卡当年停卡记录数(挡全年12次) */
    int countByCardYear(@Param("cardOrderId") Long cardOrderId, @Param("yearPrefix") String yearPrefix);

    /** 取本人某条停卡中记录(恢复用) */
    CardPauseRecord selectPausingById(@Param("pauseId") Long pauseId, @Param("userId") Long userId);

    /** 幂等恢复:仅 status=0 停卡中时置已恢复并回填 end_time/pause_days */
    int resume(@Param("pauseId") Long pauseId, @Param("endTime") Date endTime, @Param("pauseDays") Integer pauseDays);

    /** 恢复时按实际停卡天数顺延会员卡有效期 */
    int extendCardValidity(@Param("cardOrderId") Long cardOrderId, @Param("days") Integer days);

    /** 我的停卡记录分页(可按 card_order_id 过滤) */
    List<CardPauseRecord> selectMyList(Map<String, Object> params);

    /** 我的停卡记录总数 */
    int countMyList(Map<String, Object> params);
}
