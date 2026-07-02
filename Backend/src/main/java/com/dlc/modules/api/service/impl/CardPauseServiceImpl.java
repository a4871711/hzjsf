package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CardPauseRecordMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.CardPauseRecord;
import com.dlc.modules.api.service.CardPauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员自助停卡 Service 实现(移动端)
 * 落在 api.service.impl,命中事务切面(默认 REQUIRED):apply 的 FOR UPDATE 行锁、resume 的恢复+顺延均在事务内。
 */
@Service("cardPauseService")
public class CardPauseServiceImpl implements CardPauseService {

    @Autowired
    private CardPauseRecordMapper cardPauseRecordMapper;
    @Autowired
    private VipBenefitMapper vipBenefitMapper;

    /** 会员卡生效中状态(参考 CardOrderMapper「查当前有效卡」均用 status=4) */
    private static final int CARD_STATUS_ACTIVE = 4;
    /** 全年停卡次数上限 */
    private static final int YEAR_LIMIT = 12;

    @Override
    public CardPauseRecord apply(Long userId, Long cardOrderId, Integer pauseDays) {
        // 权益会员校验:仅持有有效 VIP 权益卡(正常且未过期)的会员可停卡
        if (vipBenefitMapper.countValidByUser(userId) <= 0) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_NOT_VIP_MEMBER);
        }
        // 行锁被停的卡,串行化同卡并发停卡
        CardOrder card = cardPauseRecordMapper.selectCardForUpdate(cardOrderId);
        // 卡不存在 / 不属于本人 / 非生效中 / 已过期 → 不可停卡
        if (card == null
                || card.getUserId() == null || !card.getUserId().equals(userId)
                || card.getStatus() == null || card.getStatus() != CARD_STATUS_ACTIVE
                || card.getValidityDate() == null || !card.getValidityDate().after(new Date())) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_STATE);
        }
        Date now = new Date();
        String pauseMonth = new SimpleDateFormat("yyyy-MM").format(now);
        // 每月1次(应用层先拦,库层 uk_card_month 兜底)
        if (cardPauseRecordMapper.countByCardMonth(cardOrderId, pauseMonth) > 0) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_LIMIT_MONTH);
        }
        // 全年12次
        String yearPrefix = new SimpleDateFormat("yyyy").format(now) + "-%";
        if (cardPauseRecordMapper.countByCardYear(cardOrderId, yearPrefix) >= YEAR_LIMIT) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_LIMIT_YEAR);
        }
        // 开放式:仅记停卡中,pause_days/end_time 留空,恢复时回填并顺延
        CardPauseRecord record = new CardPauseRecord();
        record.setUserId(userId);
        record.setCardOrderId(cardOrderId);
        record.setPauseMonth(pauseMonth);
        record.setStartTime(now);
        record.setStatus(0);
        cardPauseRecordMapper.insertSelective(record);
        return record;
    }

    @Override
    public int resume(Long userId, Long pauseId) {
        CardPauseRecord record = cardPauseRecordMapper.selectPausingById(pauseId, userId);
        if (record == null) {
            // 不存在 / 不属于本人 / 非停卡中
            throw new RRException(CodeAndMsg.ERROR_PAUSE_STATE);
        }
        Date now = new Date();
        // 实际停卡天数(不足一天按一天算,补偿停卡期)
        long ms = now.getTime() - record.getStartTime().getTime();
        int pauseDays = ms <= 0 ? 0 : (int) Math.ceil(ms / 86400000.0);
        // 幂等恢复:并发/重复恢复命中 0 行直接返回,不重复顺延
        int rows = cardPauseRecordMapper.resume(pauseId, now, pauseDays);
        if (rows == 0) {
            return 0;
        }
        // 同一事务按实际天数顺延会员卡有效期
        if (pauseDays > 0) {
            cardPauseRecordMapper.extendCardValidity(record.getCardOrderId(), pauseDays);
        }
        return 1;
    }

    @Override
    public PageUtils myList(Map<String, Object> params) {
        Query query = new Query(params);
        List<CardPauseRecord> list = cardPauseRecordMapper.selectMyList(query);
        int total = cardPauseRecordMapper.countMyList(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }
}
