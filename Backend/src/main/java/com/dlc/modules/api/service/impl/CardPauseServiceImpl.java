package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CardPauseRecordMapper;
import com.dlc.modules.api.dao.VipBenefitCardMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.dao.VipPauseRuleMapper;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.CardPauseRecord;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.entity.VipPauseRule;
import com.dlc.modules.api.service.CardPauseService;
import com.dlc.modules.api.service.IncomePayDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员自助停卡 Service 实现(移动端,定期停卡版)
 * 落在 api.service.impl,命中事务切面(默认 REQUIRED):apply 的行锁、payCallback 的生效+顺延+记账、cancel 的取消+扣回均在事务内。
 * 规则:
 * - 前提:有效权益会员 + 被停卡须为权益卡性质(fit_card.cardNature=1)、属本人、生效中(status=4)、未过期
 * - 免费:按用户滚动30天1次,每次自选1~7天,立即生效并预顺延 validityDate N 天,end_time=start+N,到期零动作
 * - 付费:免费额度用完后按权益卡绑定的 vip_pause_rule 选档(金额/天数以后端规则为准),微信支付回调成功才生效
 * - 提前取消:生效中且未到 end_time 可取消,usedDays=max(1,ceil),多顺延的天数扣回;付费不退款
 * - 存量兼容:end_time IS NULL 的旧开放式记录,取消走旧 resume 语义(按实际天数顺延,status→1)
 */
@Service("cardPauseService")
public class CardPauseServiceImpl implements CardPauseService {

    private static final Logger log = LoggerFactory.getLogger(CardPauseServiceImpl.class);

    @Autowired
    private CardPauseRecordMapper cardPauseRecordMapper;
    @Autowired
    private VipBenefitMapper vipBenefitMapper;
    @Autowired
    private VipBenefitCardMapper vipBenefitCardMapper;
    @Autowired
    private VipPauseRuleMapper vipPauseRuleMapper;
    @Autowired
    private IncomePayDetailService incomePayDetailService;

    /** 会员卡生效中状态(参考 CardOrderMapper「查当前有效卡」均用 status=4) */
    private static final int CARD_STATUS_ACTIVE = 4;
    /** 会员卡商品性质:1=权益卡性质(仅此可停卡) */
    private static final int CARD_NATURE_BENEFIT = 1;
    /** 免费停卡单次最长天数 */
    private static final int FREE_MAX_DAYS = 7;
    /** 免费停卡滚动周期(毫秒):30天1次 */
    private static final long FREE_QUOTA_PERIOD_MS = 30L * 24 * 60 * 60 * 1000;
    /** 一天毫秒数(实际天数 ceil 用) */
    private static final double DAY_MS = 86400000.0;

    // ====================== 停卡预检 ======================

    @Override
    public Map<String, Object> precheck(Long userId, Long cardOrderId) {
        // 权益会员校验:仅持有有效 VIP 权益卡(正常且未过期)的会员可停卡
        if (vipBenefitMapper.countValidByUser(userId) <= 0) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_NOT_VIP_MEMBER);
        }
        // 指定卡时(申请弹层打开前的按卡预检):须为权益卡性质,否则该卡不支持停卡,与 apply() 的校验保持一致
        if (cardOrderId != null) {
            Integer cardNature = cardPauseRecordMapper.selectCardNatureByOrderId(cardOrderId, userId);
            if (cardNature == null || cardNature != CARD_NATURE_BENEFIT) {
                throw new RRException(CodeAndMsg.ERROR_PAUSE_CARD_NOT_BENEFIT);
            }
        }
        Date now = new Date();
        // 免费停卡权益:由该会员权益卡的 free_pause_enabled 决定;无权益则无免费停卡(仅付费)
        boolean freeEntitled = isFreePauseEntitled(userId);
        // 免费额度:滚动30天1次(不加锁,展示口径;真正判定在 apply 锁内重算);无免费权益则恒不可用
        Date lastFree = cardPauseRecordMapper.selectLastFreePause(userId);
        boolean freeAvailable = freeEntitled && (lastFree == null
                || lastFree.getTime() + FREE_QUOTA_PERIOD_MS <= now.getTime());
        Date nextFreeDate = (freeEntitled && !freeAvailable && lastFree != null)
                ? new Date(lastFree.getTime() + FREE_QUOTA_PERIOD_MS) : null;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("freeEntitled", freeEntitled);
        data.put("freeAvailable", freeAvailable);
        data.put("nextFreeDate", nextFreeDate);
        data.put("maxFreeDays", FREE_MAX_DAYS);
        data.put("tiers", resolveTiers(userId));
        return data;
    }

    /**
     * 载入付费停卡档位:最新有效权益 → 来源权益卡 pause_rule_id → vip_pause_rule(须启用) → 解析 tiers_json。
     * 未绑定/规则停用/格式无效档 → 返回空列表(前端据此隐藏付费入口)。
     */
    private List<Map<String, Object>> resolveTiers(Long userId) {
        List<Map<String, Object>> tiers = new ArrayList<Map<String, Object>>();
        VipBenefit benefit = vipBenefitMapper.selectLatestValidByUser(userId);
        if (benefit == null || benefit.getVipCardId() == null) {
            return tiers;
        }
        VipBenefitCard card = vipBenefitCardMapper.selectByIdIgnoreStatus(benefit.getVipCardId());
        if (card == null || card.getPauseRuleId() == null) {
            return tiers;
        }
        VipPauseRule rule = vipPauseRuleMapper.selectById(card.getPauseRuleId());
        // 规则不存在/停用(status!=1) → 无付费档位
        if (rule == null || rule.getStatus() == null || rule.getStatus() != 1) {
            return tiers;
        }
        String tiersJson = rule.getTiersJson();
        if (tiersJson == null || tiersJson.trim().isEmpty()) {
            return tiers;
        }
        // FastJSON 解析分档(照 VipTransferServiceImpl 的 tiers 解析,逐项兜底跳过无效档)
        // 整串非法/非数组 JSON(手工改库或表单半写)会抛 JSONException,兜住降级为"无付费档",
        // 避免 precheck 因某规则 tiers_json 写坏而整体 500(连免费额度都看不到)
        List<PauseTier> parsed;
        try {
            parsed = JSONArray.parseArray(tiersJson, PauseTier.class);
        } catch (Exception e) {
            log.warn("停卡规则 tiers_json 解析失败,视为无付费档,pauseRuleId={}", card.getPauseRuleId());
            return tiers;
        }
        if (parsed == null) {
            return tiers;
        }
        int index = 0;
        for (PauseTier t : parsed) {
            // 付费档 price 必须 >0(与后台 validateTiersJson 一致);price<=0 的档跳过不展示,避免建 paySum=0 单被微信拒
            if (t == null || t.getDays() == null || t.getDays() <= 0 || t.getPrice() == null
                    || t.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                index++;
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("index", index);
            item.put("days", t.getDays());
            item.put("price", t.getPrice());
            tiers.add(item);
            index++;
        }
        return tiers;
    }

    /**
     * 该会员是否享有免费停卡权益:最新有效权益 → 来源权益卡 free_pause_enabled=1。
     * NULL 兜底为有(向后兼容,列默认1);无有效权益/无权益卡记录返回 false。
     */
    private boolean isFreePauseEntitled(Long userId) {
        VipBenefit benefit = vipBenefitMapper.selectLatestValidByUser(userId);
        if (benefit == null || benefit.getVipCardId() == null) {
            return false;
        }
        VipBenefitCard card = vipBenefitCardMapper.selectByIdIgnoreStatus(benefit.getVipCardId());
        if (card == null) {
            return false;
        }
        Integer flag = card.getFreePauseEnabled();
        return flag == null || flag == 1;
    }

    /** tiers_json 元素结构,FastJSON 反序列化用 */
    public static class PauseTier {
        private Integer days;      // 该档停卡天数
        private BigDecimal price;  // 该档定额费用

        public Integer getDays() {
            return days;
        }

        public void setDays(Integer days) {
            this.days = days;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    // ====================== 申请停卡 ======================

    @Override
    public Map<String, Object> apply(Long userId, Long cardOrderId, Integer pauseType, Integer pauseDays, Integer tierIndex) {
        // 权益会员校验
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
        // 仅权益卡性质(fit_card.cardNature=1)的会员卡可停卡
        Integer cardNature = cardPauseRecordMapper.selectCardNature(card.getCardId());
        if (cardNature == null || cardNature != CARD_NATURE_BENEFIT) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_CARD_NOT_BENEFIT);
        }
        // 清掉该卡遗留的待支付单(用户弃付后重新申请),再查占用
        cardPauseRecordMapper.closeUnpaidByCard(cardOrderId);
        // 同卡已有生效中/待支付的停卡 → 拦(锁定读规避 RR 快照;本单尚未插入,excludePauseId 传 0=不排除)
        if (cardPauseRecordMapper.selectActivePauseIdForUpdate(cardOrderId, 0L) != null) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_STATE);
        }
        if (pauseType != null && pauseType == 0) {
            return applyFree(userId, cardOrderId, pauseDays);
        } else if (pauseType != null && pauseType == 1) {
            return applyPaid(userId, cardOrderId, tierIndex);
        }
        throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
    }

    /** 免费停卡:滚动30天1次,自选1~7天,立即生效并预顺延有效期 */
    private Map<String, Object> applyFree(Long userId, Long cardOrderId, Integer pauseDays) {
        // 免费停卡权益校验:该会员权益卡未开通免费停卡则拒绝(仅能付费停卡)
        if (!isFreePauseEntitled(userId)) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_FREE_NOT_ENTITLED);
        }
        if (pauseDays == null || pauseDays < 1 || pauseDays > FREE_MAX_DAYS) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_FREE_DAYS_LIMIT);
        }
        // 行锁用户行,串行化同用户跨卡并发领免费额度
        cardPauseRecordMapper.lockUserForFreeQuota(userId);
        Date now = new Date();
        // 锁定读判定(非普通快照读),确保能看到并发已提交的免费单,配合上面的用户行锁真正串行化额度
        Date lastFree = cardPauseRecordMapper.selectLastFreePauseForUpdate(userId);
        if (lastFree != null && lastFree.getTime() + FREE_QUOTA_PERIOD_MS > now.getTime()) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_FREE_QUOTA_USED);
        }
        Date endTime = new Date(now.getTime() + (long) pauseDays * 24 * 60 * 60 * 1000);
        CardPauseRecord record = new CardPauseRecord();
        record.setUserId(userId);
        record.setCardOrderId(cardOrderId);
        record.setPauseType(0);
        record.setAmount(BigDecimal.ZERO);
        record.setPauseDays(pauseDays);
        record.setStartTime(now);
        record.setEndTime(endTime);
        record.setStatus(0);
        cardPauseRecordMapper.insertSelective(record);
        // 立即生效:预顺延会员卡有效期 N 天,到期零动作
        cardPauseRecordMapper.extendCardValidity(cardOrderId, pauseDays);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("pauseId", record.getPauseId());
        data.put("needPay", false);
        return data;
    }

    /** 付费停卡:按权益卡绑定停卡规则选档建待支付单(金额/天数以后端规则为准,不信前端) */
    private Map<String, Object> applyPaid(Long userId, Long cardOrderId, Integer tierIndex) {
        List<Map<String, Object>> tiers = resolveTiers(userId);
        // 未绑定规则/规则停用/无有效档 → 该卡未开通付费停卡
        if (tiers.isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_RULE_NOT_CONFIG);
        }
        // 按 index 匹配档位(resolveTiers 已跳过无效档,index 不连续,须精确匹配)
        Map<String, Object> tier = null;
        if (tierIndex != null) {
            for (Map<String, Object> t : tiers) {
                if (tierIndex.equals(t.get("index"))) {
                    tier = t;
                    break;
                }
            }
        }
        if (tier == null) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_TIER_INVALID);
        }
        Integer days = (Integer) tier.get("days");
        BigDecimal price = (BigDecimal) tier.get("price");
        // 取规则ID留痕(resolveTiers 已校验链路,此处再取一次;防御性判空,极小概率并发过期时不 NPE)
        VipBenefit benefit = vipBenefitMapper.selectLatestValidByUser(userId);
        if (benefit == null || benefit.getVipCardId() == null) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_RULE_NOT_CONFIG);
        }
        VipBenefitCard vipCard = vipBenefitCardMapper.selectByIdIgnoreStatus(benefit.getVipCardId());
        if (vipCard == null || vipCard.getPauseRuleId() == null) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_RULE_NOT_CONFIG);
        }

        // 末位后缀c的停卡费用单号(pay_order_no 唯一键兜底)
        String orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.CARD_PAUSE_FEE_TYPE;
        Date now = new Date();
        CardPauseRecord record = new CardPauseRecord();
        record.setUserId(userId);
        record.setCardOrderId(cardOrderId);
        record.setPauseType(1);
        record.setPauseRuleId(vipCard.getPauseRuleId());
        record.setAmount(price);
        record.setPauseDays(days);
        record.setPayOrderNo(orderNo);
        // start_time 占位建单时间,回调生效时以支付成功时刻覆盖
        record.setStartTime(now);
        record.setStatus(10);
        cardPauseRecordMapper.insertSelective(record);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("pauseId", record.getPauseId());
        data.put("needPay", true);
        // 前端据此调 /wx/proPay 拉起微信支付,成功后 proPayNotify(后缀c) 置生效
        data.put("orderNo", orderNo);
        data.put("paySum", price);
        return data;
    }

    // ====================== 付费停卡支付回调 ======================

    @Override
    public int payCallback(String orderNo, BigDecimal money, String transactionId, Integer payType) {
        // 先不加锁取单拿 cardOrderId,再按"先锁卡、后锁停卡单"的顺序加锁——与 apply(selectCardForUpdate 在前)一致,规避死锁
        CardPauseRecord probe = cardPauseRecordMapper.selectByPayOrderNo(orderNo);
        if (probe == null) {
            log.warn("停卡费回调:支付单号不存在,orderNo={}", orderNo);
            return 0;
        }
        // 先行锁会员卡行(与 apply 同序),供生效前复查卡状态
        CardOrder card = cardPauseRecordMapper.selectCardForUpdate(probe.getCardOrderId());
        // 再行锁本支付单
        CardPauseRecord record = cardPauseRecordMapper.selectByPayOrderNoForUpdate(orderNo);
        if (record == null) {
            log.warn("停卡费回调:支付单号不存在,orderNo={}", orderNo);
            return 0;
        }
        // 已生效(0)/已取消或已支付作废(2)/存量已恢复(1) → 重复回调,幂等返回,不重复顺延不重复记账
        if (record.getStatus() == null || record.getStatus() == 0
                || record.getStatus() == 1 || record.getStatus() == 2) {
            return 0;
        }
        // 金额一致性:实付必须等于建单时后端锁定的档位价(合法流程必然相等);少付/篡改回调不生效不记账
        // (照 VipTransferServiceImpl.payFeeCallback 的既有防线,proPay 的 paySum 取自前端不可信)
        if (money == null || record.getAmount() == null || money.compareTo(record.getAmount()) != 0) {
            log.warn("停卡费回调金额与建单不符,拒绝生效,orderNo={}, 应收={}, 实收={}", orderNo, record.getAmount(), money);
            return 0;
        }
        Date now = new Date();
        // 生效前置校验:卡须仍生效中且未过期(防顺延已过期死卡);同卡不得已有其它活跃停卡
        // (用户弃付后已改用其它停卡,本单被 closeUnpaidByCard 置3,迟到回调若再生效会双重顺延)
        boolean cardOk = card != null
                && card.getStatus() != null && card.getStatus() == CARD_STATUS_ACTIVE
                && card.getValidityDate() != null && card.getValidityDate().after(now);
        Long otherActive = cardPauseRecordMapper.selectActivePauseIdForUpdate(record.getCardOrderId(), record.getPauseId());
        if (!cardOk || otherActive != null) {
            // 钱已实收:认账但不生效为停卡、不顺延有效期,置已支付作废(status→2, actual_days=0)
            int voidRows = cardPauseRecordMapper.voidPaidByPayOrderNo(record.getPauseId(), now, transactionId);
            if (voidRows == 0) {
                return 0;
            }
            incomePayDetailService.saveIncomePayDetail(orderNo, transactionId, money, payType);
            log.warn("停卡费回调但卡失效或已被其它停卡顶替,仅认账不顺延,orderNo={}, cardOk={}, otherActivePauseId={}",
                    orderNo, cardOk, otherActive);
            return 1;
        }
        Date endTime = new Date(now.getTime() + (long) record.getPauseDays() * 24 * 60 * 60 * 1000);
        // 幂等生效:status IN (10,3)→0(弃付关闭后的迟到回调仍认账);命中0行=并发已处理
        int rows = cardPauseRecordMapper.activateByPayOrderNo(record.getPauseId(), now, endTime, transactionId);
        if (rows == 0) {
            return 0;
        }
        // 同事务预顺延会员卡有效期
        cardPauseRecordMapper.extendCardValidity(record.getCardOrderId(), record.getPauseDays());
        // 同事务记账(用途按末位后缀c固定=15,付费人=停卡会员)
        incomePayDetailService.saveIncomePayDetail(orderNo, transactionId, money, payType);
        return 1;
    }

    // ====================== 提前取消 ======================

    @Override
    public int cancel(Long userId, Long pauseId) {
        // 先不加锁探记录拿 cardOrderId,再按"卡→单"顺序加锁——与 apply/payCallback 同序,防同卡并发 AB-BA 死锁
        CardPauseRecord probe = cardPauseRecordMapper.selectByPauseId(pauseId);
        if (probe == null || probe.getUserId() == null || !probe.getUserId().equals(userId)) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_STATE);
        }
        // 先行锁会员卡行(与 apply/payCallback 同序)
        cardPauseRecordMapper.selectCardForUpdate(probe.getCardOrderId());
        // 再行锁本人生效中记录;不存在/不属于本人/非生效中 → 状态异常
        CardPauseRecord record = cardPauseRecordMapper.selectPausingById(pauseId, userId);
        if (record == null) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_STATE);
        }
        Date now = new Date();
        if (record.getEndTime() == null) {
            // 存量开放式停卡(旧数据):走旧 resume 语义,按实际天数顺延,status→1
            long ms = now.getTime() - record.getStartTime().getTime();
            int actualDays = ms <= 0 ? 0 : (int) Math.ceil(ms / DAY_MS);
            int rows = cardPauseRecordMapper.resume(pauseId, now, actualDays);
            if (rows == 0) {
                return 0;
            }
            if (actualDays > 0) {
                cardPauseRecordMapper.extendCardValidity(record.getCardOrderId(), actualDays);
            }
            return 1;
        }
        // 定期停卡:已到计划结束时间的不可取消(到期零动作,列表展示为已完成)
        if (!record.getEndTime().after(now)) {
            throw new RRException(CodeAndMsg.ERROR_PAUSE_STATE);
        }
        // 实际使用天数不足一天按一天算;多顺延的部分扣回(付费停卡不退款)
        int usedDays = Math.max(1, (int) Math.ceil((now.getTime() - record.getStartTime().getTime()) / DAY_MS));
        int refundDays = (record.getPauseDays() == null ? 0 : record.getPauseDays()) - usedDays;
        // 幂等取消:status=0 且未到 end_time 才命中,并发/重复取消命中0行直接返回
        int rows = cardPauseRecordMapper.cancelPause(pauseId, now, usedDays);
        if (rows == 0) {
            return 0;
        }
        if (refundDays > 0) {
            cardPauseRecordMapper.shrinkCardValidity(record.getCardOrderId(), refundDays);
        }
        return 1;
    }

    // ====================== 我的停卡记录 ======================

    @Override
    public PageUtils myList(Map<String, Object> params) {
        Query query = new Query(params);
        List<CardPauseRecord> list = cardPauseRecordMapper.selectMyList(query);
        int total = cardPauseRecordMapper.countMyList(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }
}
