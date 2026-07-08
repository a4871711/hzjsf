package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.CardPauseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 停卡记录(card_pause_record)Mapper(移动端)
 * 定期停卡:免费(滚动30天1次,自选1~7天)立即生效并预顺延;付费(status=10待支付)回调成功后生效。
 */
public interface CardPauseRecordMapper {

    /** 建停卡记录(免费直接 status=0 生效;付费 status=10 待支付) */
    int insertSelective(CardPauseRecord record);

    /** 行锁被停的会员卡(并发串行化),只取校验所需字段(含 cardId 供查 fit_card.cardNature) */
    CardOrder selectCardForUpdate(@Param("cardOrderId") Long cardOrderId);

    /** 查会员卡商品性质(fit_card.cardNature,1=权益卡性质才可停卡);卡不存在返回 null */
    Integer selectCardNature(@Param("cardId") Long cardId);

    /** 行锁用户行,串行化同一用户的免费额度判定(滚动30天1次) */
    Long lockUserForFreeQuota(@Param("userId") Long userId);

    /** 该用户最近一次免费停卡的开始时间(已取消的免费停卡仍占额度,故含 status 0/1/2);无则 null。不加锁,仅 precheck 展示用 */
    Date selectLastFreePause(@Param("userId") Long userId);

    /** 同上但锁定读:RR 隔离下规避普通读的旧快照,配合用户行锁真正串行化免费额度判定(applyFree 用) */
    Date selectLastFreePauseForUpdate(@Param("userId") Long userId);

    /** 该卡当前占用中的停卡 pause_id(生效中或待支付),锁定读取一条;无则 null。
     *  excludePauseId 排除自身(apply 传 0=不排除;payCallback 传本单 id=查是否已被顶替) */
    Long selectActivePauseIdForUpdate(@Param("cardOrderId") Long cardOrderId, @Param("excludePauseId") Long excludePauseId);

    /** 关闭该卡遗留的待支付停卡单(status 10→3),为新申请让路 */
    int closeUnpaidByCard(@Param("cardOrderId") Long cardOrderId);

    /** 按支付单号行锁取停卡记录(付费回调幂等入口) */
    CardPauseRecord selectByPayOrderNoForUpdate(@Param("orderNo") String orderNo);

    /** 按支付单号取停卡记录(不加锁,记账反查 userId 用) */
    CardPauseRecord selectByPayOrderNo(@Param("orderNo") String orderNo);

    /** 按 pause_id 取记录(不加锁,cancel 先探 cardOrderId 以"卡→单"序加锁防死锁) */
    CardPauseRecord selectByPauseId(@Param("pauseId") Long pauseId);

    /** 付费停卡生效:status(10待支付/3已关闭)→0,回填起止/支付时间/交易号;命中0行=已处理过 */
    int activateByPayOrderNo(@Param("pauseId") Long pauseId,
                             @Param("now") Date now,
                             @Param("endTime") Date endTime,
                             @Param("transactionId") String transactionId);

    /** 付费回调时卡失效/已被顶替:认账但不顺延,status(10/3)→2 已支付作废(actual_days=0);命中0行=已处理过 */
    int voidPaidByPayOrderNo(@Param("pauseId") Long pauseId,
                             @Param("now") Date now,
                             @Param("transactionId") String transactionId);

    /** 提前取消:仅生效中且未到计划结束时间命中,回填取消时间/实际天数 */
    int cancelPause(@Param("pauseId") Long pauseId,
                    @Param("now") Date now,
                    @Param("usedDays") Integer usedDays);

    /** 取本人某条生效中(status=0)停卡记录并行锁(取消用) */
    CardPauseRecord selectPausingById(@Param("pauseId") Long pauseId, @Param("userId") Long userId);

    /** 存量开放式记录恢复:仅 status=0 时置已恢复并回填 end_time/pause_days(旧 resume 语义) */
    int resume(@Param("pauseId") Long pauseId, @Param("endTime") Date endTime, @Param("pauseDays") Integer pauseDays);

    /** 停卡生效时预顺延会员卡有效期 */
    int extendCardValidity(@Param("cardOrderId") Long cardOrderId, @Param("days") Integer days);

    /** 提前取消时按未使用天数扣回会员卡有效期 */
    int shrinkCardValidity(@Param("cardOrderId") Long cardOrderId, @Param("days") Integer days);

    /** 我的停卡记录分页(可按 card_order_id 过滤,带派生列 displayStatus) */
    List<CardPauseRecord> selectMyList(Map<String, Object> params);

    /** 我的停卡记录总数 */
    int countMyList(Map<String, Object> params);
}
