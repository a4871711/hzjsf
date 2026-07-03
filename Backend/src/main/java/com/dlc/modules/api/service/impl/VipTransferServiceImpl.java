package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.MemberBlacklistMapper;
import com.dlc.modules.api.dao.SystemMsgDao;
import com.dlc.modules.api.dao.VipBenefitCardMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.VipBenefitTransferMapper;
import com.dlc.modules.api.dao.VipFeeRuleMapper;
import com.dlc.modules.api.entity.SystemMsgEntity;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.entity.VipBenefitTransfer;
import com.dlc.modules.api.entity.VipFeeRule;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.api.service.VipTransferService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益转让 Service 实现(移动端,详细技术设计 §8 + 附录A/E)。
 * 落在 api.service.impl,命中事务切面(默认 REQUIRED)。
 * 校验集统一实现在此(附录E):后台 sys 审核复用时直接 @Autowired 注入本 bean,
 * 保证"发起 + 审核两处跑同一份规则"。
 */
@Service("vipTransferService")
public class VipTransferServiceImpl implements VipTransferService {

    private static final Logger log = LoggerFactory.getLogger(VipTransferServiceImpl.class);

    @Autowired
    private VipBenefitMapper vipBenefitMapper;
    @Autowired
    private VipBenefitCardMapper vipBenefitCardMapper;
    @Autowired
    private VipFeeRuleMapper vipFeeRuleMapper;
    @Autowired
    private MemberBlacklistMapper memberBlacklistMapper;
    @Autowired
    private VipBenefitTransferMapper vipBenefitTransferMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private SystemMsgDao systemMsgDao;
    @Autowired
    private PayService payService;

    // ====================== 8.2 前置校验集 ======================

    @Override
    public void checkTransferable(VipBenefit benefit, UserInfoVo fromUser, UserInfoVo toUser) {
        Date now = new Date();

        // 1. 权益存在
        if (benefit == null) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_EXIST);
        }
        // 2. 归属转让人(以当前持有人 user_id 为准,链式转让后持有人已变更)
        if (fromUser == null || benefit.getUserId() == null
                || !benefit.getUserId().equals(fromUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_OWNER);
        }
        // 3. status=0 正常(9待支付/1已转出/2已冻结/3已过期 一律拦截)
        if (benefit.getStatus() == null || benefit.getStatus() != 0) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_STATUS_ABNORMAL);
        }
        // 4. expire_time > now(实时判断,不依赖定时任务,R-03)
        if (benefit.getExpireTime() == null || !benefit.getExpireTime().after(now)) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_EXPIRED);
        }
        // 5. transferable=1(被冻结/拉黑/违规时置0;受让所得默认仍为1可链式再转)
        if (benefit.getTransferable() == null || benefit.getTransferable() != 1) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_TRANSFERABLE);
        }
        // 6. 转让人未封禁(auditStatus=2);附录A 将封禁与黑名单统一为 ERROR_VIP_USER_BLACKLIST
        if (fromUser.getAuditStatus() != null && fromUser.getAuditStatus() == 2) {
            throw new RRException(CodeAndMsg.ERROR_VIP_USER_BLACKLIST);
        }
        // 7. 转让人不在黑名单
        if (isInBlacklist(fromUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_USER_BLACKLIST);
        }
        // 8. 受让人存在
        if (toUser == null) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TO_USER_NOT_EXIST);
        }
        // 9. 不能转给自己
        if (toUser.getUserId() != null && toUser.getUserId().equals(fromUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TO_USER_SELF);
        }
        // 10. 受让人未封禁
        if (toUser.getAuditStatus() != null && toUser.getAuditStatus() == 2) {
            throw new RRException(CodeAndMsg.ERROR_VIP_USER_BLACKLIST);
        }
        // 11. 受让人不在黑名单
        if (isInBlacklist(toUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_USER_BLACKLIST);
        }
        // 12. 欠费【口径待对齐:占位,默认不拦截】
        if (hasArrears(fromUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_ARREARS);
        }
        // 13. 是否办理退卡【占位,默认不拦截】
        if (hasCardRefund(fromUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_REFUNDED_CARD);
        }
        // 14. 重大违规【占位,默认不拦截】
        if (hasSeriousViolation(fromUser.getUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_VIOLATION);
        }
        // 15. 权益卡未下架(status=1 上架)
        VipBenefitCard card = vipBenefitCardMapper.selectByIdIgnoreStatus(benefit.getVipCardId());
        if (card == null || card.getStatus() == null || card.getStatus() != 1) {
            throw new RRException(CodeAndMsg.ERROR_VIP_CARD_OFF_SHELF);
        }
        // 16. 适用门店:受让人当前所属门店 ∈ 该权益来源权益卡的适用门店(R-01,D-3)
        //     nowStoreId 是基本类型 int(即 store_addr_id,未归属门店为0);
        //     比对口径对齐系统多值门店匹配:逐项 trim 后精确匹配
        if (!storeApplicable(toUser.getNowStoreId(), card.getStoreAddrIds())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_STORE_NOT_APPLICABLE);
        }
    }

    /** 受让人当前门店是否在权益卡适用门店集合内(含未归属门店 nowStoreId<=0 → 不在范围,拦截) */
    private boolean storeApplicable(int toStoreAddrId, String applicableAddrIds) {
        if (toStoreAddrId <= 0 || applicableAddrIds == null || applicableAddrIds.trim().isEmpty()) {
            return false;
        }
        for (String addrId : applicableAddrIds.split(",")) {
            if (addrId.trim().equals(String.valueOf(toStoreAddrId))) {
                return true;
            }
        }
        return false;
    }

    /** 在 member_blacklist 中存在 status=1(生效)的记录即视为黑名单 */
    private boolean isInBlacklist(Long userId) {
        if (userId == null) {
            return false;
        }
        return memberBlacklistMapper.countActiveByUserId(userId) > 0;
    }

    // ===== 以下三项口径待对齐,占位先一律不拦截,待业务确认后实现 =====
    /** 欠费判定【口径待对齐】 */
    private boolean hasArrears(Long userId) {
        return false;
    }

    /** 是否办理退卡【口径待对齐】 */
    private boolean hasCardRefund(Long userId) {
        return false;
    }

    /** 重大违规【口径待对齐】 */
    private boolean hasSeriousViolation(Long userId) {
        return false;
    }

    // ====================== 8.4 转让费用分档 ======================

    @Override
    public BigDecimal calcTransferFee(VipBenefit benefit, VipFeeRule feeRule) {
        // 空规则 或 规则停用(status!=1) → 免费
        if (feeRule == null || feeRule.getStatus() == null || feeRule.getStatus() != 1) {
            return BigDecimal.ZERO;
        }
        String tiersJson = feeRule.getTiersJson();
        if (tiersJson == null || tiersJson.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 本次=第 (transfer_count + 1) 次转让
        int currentCount = (benefit.getTransferCount() == null ? 0 : benefit.getTransferCount()) + 1;

        List<FeeTier> tiers = JSONArray.parseArray(tiersJson, FeeTier.class);
        if (tiers == null || tiers.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 取 fromCount<=currentCount 中 fromCount 最大的那一档 fee(遍历求最大命中,不依赖入库顺序)
        BigDecimal fee = BigDecimal.ZERO;
        int matchedFrom = -1;
        for (FeeTier t : tiers) {
            if (t == null || t.getFromCount() == null || t.getFee() == null) {
                continue;
            }
            if (t.getFromCount() <= currentCount && t.getFromCount() > matchedFrom) {
                matchedFrom = t.getFromCount();
                fee = t.getFee();
            }
        }
        return fee; // 无任何 fromCount<=currentCount 的档 → 保持 ZERO
    }

    /** tiers_json 元素结构,FastJSON 反序列化用 */
    public static class FeeTier {
        private Integer fromCount; // 从第几次转让起适用
        private BigDecimal fee;    // 该档定额费用

        public Integer getFromCount() {
            return fromCount;
        }

        public void setFromCount(Integer fromCount) {
            this.fromCount = fromCount;
        }

        public BigDecimal getFee() {
            return fee;
        }

        public void setFee(BigDecimal fee) {
            this.fee = fee;
        }
    }

    // ====================== 5.5 试算费用 ======================

    @Override
    public Map<String, Object> quote(Long userId, Long vipBenefitId) {
        VipBenefit benefit = vipBenefitMapper.selectById(vipBenefitId);
        // 权益存在 + 属当前用户(试算只查归属,不跑完整 checkTransferable)
        if (benefit == null) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_EXIST);
        }
        if (benefit.getUserId() == null || !benefit.getUserId().equals(userId)) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_OWNER);
        }
        int transferCount = benefit.getTransferCount() == null ? 0 : benefit.getTransferCount();
        BigDecimal serviceFee = resolveFee(benefit);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("vipBenefitId", benefit.getVipBenefitId());
        data.put("transferCount", transferCount);
        data.put("thisTransferNo", transferCount + 1);
        data.put("serviceFee", serviceFee);
        return data;
    }

    /** 载入权益来源卡的费用规则并按 transfer_count+1 算本次服务费(未配规则=免费,见 D-4) */
    private BigDecimal resolveFee(VipBenefit benefit) {
        VipFeeRule feeRule = null;
        VipBenefitCard card = vipBenefitCardMapper.selectByIdIgnoreStatus(benefit.getVipCardId());
        if (card != null && card.getFeeRuleId() != null) {
            feeRule = vipFeeRuleMapper.selectById(card.getFeeRuleId());
        }
        return calcTransferFee(benefit, feeRule);
    }

    // ====================== 5.6 / 7.3.1 发起转让 ======================

    @Override
    public Map<String, Object> apply(UserInfoVo fromUser, Long vipBenefitId, Long toUserId) {
        // 行锁权益,串行化同权益并发发起(附录C.1)
        VipBenefit benefit = vipBenefitMapper.selectByIdForUpdate(vipBenefitId);
        // 受让人(selectByUserId 走 SELECT *,带出 nowStoreId/auditStatus;不存在=null,由校验拦)
        UserInfoVo toUser = (toUserId == null) ? null
                : userInfoService.selectByUserId(String.valueOf(toUserId));
        // 全量前置校验(命中抛附录A码;含权益归属/状态/有效期/双方封禁黑名单/适用门店/卡下架)
        checkTransferable(benefit, fromUser, toUser);
        // 在途唯一占用:同权益只允许一笔在途(10/20/40),否则并发两笔"收费不退"
        if (vipBenefitTransferMapper.countInProgress(vipBenefitId) > 0) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_IN_PROGRESS);
        }
        // 后端重算服务费(按 transfer_count+1 命中分档),不信前端
        BigDecimal fee = resolveFee(benefit);

        VipBenefitTransfer t = new VipBenefitTransfer();
        t.setVipBenefitId(vipBenefitId);
        t.setFromUserId(fromUser.getUserId());
        t.setToUserId(toUserId);
        // 门店留痕(过户不改归属):双方统一取门店 store_id 口径,避免 from(store_id)/to(store_addr_id)混用
        t.setFromStoreId(benefit.getStoreId());
        t.setToStoreId(userInfoMapper.queryStoreIdByUserId(toUserId));
        t.setServiceFee(fee);
        t.setRefundStatus(0);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            // 服务费>0:待付费,生成末位后缀7的服务费单号(uk_fee_order 唯一)
            String feeOrderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.VIP_TRANSFER_FEE_TYPE;
            t.setServiceFeeOrderNo(feeOrderNo);
            t.setStatus(10);
            vipBenefitTransferMapper.insertSelective(t);
            data.put("transferId", t.getTransferId());
            data.put("status", 10);
            data.put("serviceFee", fee);
            // 前端据此调 /wx/proPay 拉起微信支付,成功后 proPayNotify(后缀7) 置待审核
            data.put("orderNo", feeOrderNo);
            data.put("paySum", fee);
        } else {
            // 免费(首档免费/未配规则):直接进待审核
            t.setStatus(20);
            vipBenefitTransferMapper.insertSelective(t);
            data.put("transferId", t.getTransferId());
            data.put("status", 20);
            data.put("serviceFee", fee);
        }
        return data;
    }

    // ====================== 5.7 我的转让/受让记录 ======================

    @Override
    public PageUtils myList(Map<String, Object> params) {
        Query query = new Query(params);
        List<VipBenefitTransfer> list = vipBenefitTransferMapper.selectMyList(query);
        int total = vipBenefitTransferMapper.countMyList(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    // ====================== 7.3.3 服务费支付回调 ======================

    @Override
    public int payFeeCallback(String feeOrderNo, BigDecimal money, String transactionNumber, Integer payType) {
        VipBenefitTransfer transfer = vipBenefitTransferMapper.selectByFeeOrderNo(feeOrderNo);
        // 不存在 / 非待付费(重复回调或状态已变) → 幂等返回,不记账
        if (transfer == null || transfer.getStatus() == null || transfer.getStatus() != 10) {
            return 0;
        }
        // 金额一致性:实付必须等于建单时后端重算的服务费(合法流程必然相等);少付/篡改回调不推进不记账
        if (money == null || transfer.getServiceFee() == null
                || money.compareTo(transfer.getServiceFee()) != 0) {
            return 0;
        }
        // 幂等推进 10→20 待审核 + 留痕交易号;并发/重复回调命中0行直接返回,不重复记账
        int rows = vipBenefitTransferMapper.feePaid(feeOrderNo, transactionNumber);
        if (rows == 0) {
            return 0;
        }
        // 同事务记账(用途按末位后缀自动=7,付费人=转让人)
        incomePayDetailService.saveIncomePayDetail(feeOrderNo, transactionNumber, money, payType);
        return rows;
    }

    // ====================== 7.3.5 受让人确认(过户入口) ======================

    @Override
    public Map<String, Object> confirm(UserInfoVo toUser, Long transferId) {
        VipBenefitTransfer t = vipBenefitTransferMapper.selectById(transferId);
        if (t == null) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_NOT_EXIST);
        }
        // 仅受让人本人、仅"40待受让人确认"可确认;不是本单受让人或状态不对一律按"状态不允许该操作"处理
        if (toUser == null || toUser.getUserId() == null || !toUser.getUserId().equals(t.getToUserId())
                || t.getStatus() == null || t.getStatus() != 40) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        // 已超确认截止时间:是否置52已超时由定时任务(第12步)统一处理,这里只拦截、不越权改状态
        if (t.getConfirmDeadline() != null && t.getConfirmDeadline().before(new Date())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        transferEffect(transferId);
        // 重读最终态:transferEffect 因并发(超时/撤回抢先把单改成52/60)在加锁后 no-op 返回时本单并未过户;
        // 仅当最终态=70(本次或并发确认幂等完成)才算成功,否则回报"状态已变",避免向受让人误报"确认成功却没拿到权益"
        VipBenefitTransfer effected = vipBenefitTransferMapper.selectById(transferId);
        if (effected == null || effected.getStatus() == null || effected.getStatus() != 70) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("transferId", transferId);
        data.put("status", effected.getStatus());
        data.put("effectTime", effected.getEffectTime());
        return data;
    }

    // ====================== 7.4 过户单事务 ======================

    @Override
    public void transferEffect(Long transferId) {
        // 锁转让单:非"40待受让人确认"直接幂等返回(已生效/已终止/不存在),此判断同时挡掉本单的重复确认重入
        VipBenefitTransfer t = vipBenefitTransferMapper.selectByIdForUpdate(transferId);
        if (t == null || t.getStatus() == null || t.getStatus() != 40) {
            return;
        }
        // 锁权益行,防并发双改;仍需为"0正常"才可过户
        VipBenefit vb = vipBenefitMapper.selectByIdForUpdate(t.getVipBenefitId());
        if (vb == null || vb.getStatus() == null || vb.getStatus() != 0) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_STATUS_ABNORMAL);
        }
        // 1) 改归属:user_id 换受让人 + transfer_count+1 + transferable=1;
        //    store_id/store_addr_id 不动(附录D.3),expire_time 不动(继承剩余有效期,D-10)
        int rows1 = vipBenefitMapper.changeOwner(vb.getVipBenefitId(), t.getFromUserId(), t.getToUserId());
        if (rows1 == 0) {
            // 方法开头已用"本单status!=40直接return"挡掉自身重入,能走到这里说明本单确为首次执行,
            // 命中0行只可能是该权益被其他转让单抢先过户(附录C.3"否则"分支)。不静默返回:抛异常回滚,
            // 本单转让停在40可重试,避免受让人误以为"确认成功"却没拿到权益。
            // 兜底闭环:此单卡在40后,到 confirm_deadline 由超时任务 timeoutOne 扫为52并退服务费(第12步),
            // 无需在此回滚事务里直接退费(避免退费与回滚的事务边界不一致导致重复退款)。
            log.error("VIP过户失败:权益已被其他转让单抢先,transferId={}, vipBenefitId={}, fromUserId={}, toUserId={}",
                    transferId, vb.getVipBenefitId(), t.getFromUserId(), t.getToUserId());
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_STATUS_ABNORMAL);
        }
        // 2) 幂等推进转让单状态机:仅 40->70(此刻已持有 transferId 行锁,理论上必然命中,双保险)
        Date now = new Date();
        int rows2 = vipBenefitTransferMapper.effect(transferId, now);
        if (rows2 == 0) {
            log.error("VIP过户异常:权益已改归属但转让单状态推进0行,transferId={}, vipBenefitId={}", transferId, vb.getVipBenefitId());
            return;
        }
        // 3) 服务费已在 payFeeCallback 记账一次(附录B.6),过户不重复插入流水,仅补 anotherId(天然幂等)
        if (t.getServiceFeeOrderNo() != null) {
            incomePayDetailService.updateAnotherId(t.getServiceFeeOrderNo(), t.getToUserId());
        }
        // 4) 推送双方:站内信(system_msg)。会员端以微信小程序为主,收不到友盟APP推送(UMengPush 仅App构建可达),
        //    且发推送需 deviceToken/机型信息;故转让各节点统一走站内信覆盖全端,不接 UMengPush。
        pushSystemMsg(t.getFromUserId(), "您的权益转让已完成,权益已成功转出");
        pushSystemMsg(t.getToUserId(), "您已成功接收权益转让,权益已到账");
    }

    // ====================== 7.3.7 转让人撤回 ======================

    @Override
    public Map<String, Object> withdraw(Long fromUserId, Long transferId) {
        VipBenefitTransfer t = vipBenefitTransferMapper.selectByIdForUpdate(transferId);
        if (t == null) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_NOT_EXIST);
        }
        // 仅转让人本人可撤回
        if (fromUserId == null || !fromUserId.equals(t.getFromUserId())) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        Integer st = t.getStatus();
        if (st != null && st == 20) {
            // 待审核阶段撤回:20→60 且全额退服务费(附录D.1)
            int rows = vipBenefitTransferMapper.withdraw20(transferId);
            if (rows == 0) {
                throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
            }
            doRefund(t);
        } else if (st != null && st == 40) {
            // 审核通过后撤回:40→60 不退费(附录D.1 / D-3)
            int rows = vipBenefitTransferMapper.withdraw40(transferId);
            if (rows == 0) {
                throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
            }
        } else {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        pushSystemMsg(t.getToUserId(), "对方已撤回一笔权益转让");
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("transferId", transferId);
        data.put("status", 60);
        return data;
    }

    // ====================== 7.3.6 受让人拒绝 ======================

    @Override
    public Map<String, Object> reject(Long toUserId, Long transferId) {
        VipBenefitTransfer t = vipBenefitTransferMapper.selectByIdForUpdate(transferId);
        if (t == null) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_NOT_EXIST);
        }
        // 仅受让人本人、仅 status=40 可拒绝(带 to_user_id + status 的条件 UPDATE 兜住并发与越权)
        int rows = vipBenefitTransferMapper.toReject(transferId, toUserId);
        if (rows == 0) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        // 全额退服务费,不动权益归属
        doRefund(t);
        pushSystemMsg(t.getFromUserId(), "受让人已拒绝接收,服务费将原路退回");
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("transferId", transferId);
        data.put("status", 51);
        return data;
    }

    // ====================== 7.6 受让人确认超时(定时任务逐笔) ======================

    @Override
    public List<VipBenefitTransfer> listConfirmTimeout() {
        return vipBenefitTransferMapper.selectTimeout(new Date());
    }

    @Override
    public void timeoutOne(Long transferId) {
        VipBenefitTransfer t = vipBenefitTransferMapper.selectByIdForUpdate(transferId);
        // 已被确认/撤回/拒绝 → 幂等跳过
        if (t == null || t.getStatus() == null || t.getStatus() != 40) {
            return;
        }
        // 未真正超时(扫描后被顺延/时钟误差)→ 不处理
        if (t.getConfirmDeadline() == null || !t.getConfirmDeadline().before(new Date())) {
            return;
        }
        int rows = vipBenefitTransferMapper.timeout(transferId);
        if (rows == 0) {
            return;
        }
        doRefund(t);
        pushSystemMsg(t.getFromUserId(), "受让人超时未确认,转让已关闭,服务费将原路退回");
        pushSystemMsg(t.getToUserId(), "您有一笔待确认的权益转让已超时关闭");
    }

    // ====================== 7.3.4 后台审核(sys 复用,逻辑落在 api 单事务) ======================

    @Override
    public void audit(Long transferId, Long auditUserId, Integer pass, String remark) {
        VipBenefitTransfer t = vipBenefitTransferMapper.selectByIdForUpdate(transferId);
        // 仅处理"20待审核"单
        if (t == null || t.getStatus() == null || t.getStatus() != 20) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        Date now = new Date();
        if (pass != null && pass == 1) {
            // 复核:以审核当下数据重跑前置校验集(其间权益可能过期/双方被拉黑封禁/卡下架/受让人门店变动)
            VipBenefit benefit = vipBenefitMapper.selectById(t.getVipBenefitId());
            UserInfoVo fromUser = userInfoService.selectByUserId(String.valueOf(t.getFromUserId()));
            UserInfoVo toUser = userInfoService.selectByUserId(String.valueOf(t.getToUserId()));
            String recheckFail = null;
            try {
                checkTransferable(benefit, fromUser, toUser);
            } catch (RRException e) {
                recheckFail = e.getMsg();
            }
            if (recheckFail != null) {
                // 复核失败统一走驳回+退费,remark 标"系统复核:xxx"(附录E)
                doAuditReject(t, auditUserId, now, "系统复核:" + recheckFail);
                return;
            }
            // 通过:20→40,写 confirm_deadline = now + N天
            Date deadline = new Date(now.getTime()
                    + (long) ConfigConstant.VIP_TRANSFER_CONFIRM_DAYS * 24 * 60 * 60 * 1000);
            int rows = vipBenefitTransferMapper.auditPass(transferId, auditUserId, now, remark, deadline);
            if (rows == 0) {
                throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
            }
            pushSystemMsg(t.getToUserId(),
                    "您有一份权益待确认接收,请在" + ConfigConstant.VIP_TRANSFER_CONFIRM_DAYS + "天内确认");
        } else {
            // 人工驳回:20→31 + 退费
            doAuditReject(t, auditUserId, now, remark);
        }
    }

    /** 驳回收敛:20→31 + 全额退服务费 + 推送转让人(人工驳回 / 系统复核失败共用) */
    private void doAuditReject(VipBenefitTransfer t, Long auditUserId, Date now, String remark) {
        int rows = vipBenefitTransferMapper.auditReject(t.getTransferId(), auditUserId, now, remark);
        if (rows == 0) {
            throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_STATUS);
        }
        doRefund(t);
        pushSystemMsg(t.getFromUserId(),
                "您的权益转让申请被驳回,服务费将原路退回" + (remark == null ? "" : ("。原因:" + remark)));
    }

    // ====================== 7.5 退费封装(驳回/拒绝/超时/20撤回共用) ======================

    /**
     * 全额退服务费:仅在收过费(serviceFee>0 且有支付单)且未退过时执行。
     * 幂等:refund_status 已=1 直接返回;wxRefund 以 out_refund_no=orderNo 天然幂等,微信不会重复退。
     * wxRefund 失败(返回 null)抛 RRException 回滚,连带调用方的状态推进一并回滚、可重试。
     */
    private void doRefund(VipBenefitTransfer t) {
        if (t.getRefundStatus() != null && t.getRefundStatus() == 1) {
            return; // 已退,幂等
        }
        if (t.getServiceFee() == null || t.getServiceFee().compareTo(BigDecimal.ZERO) <= 0
                || t.getServiceFeeOrderNo() == null) {
            return; // 免费单 / 未产生支付单,无需退费
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderNo", t.getServiceFeeOrderNo());   // = 微信 out_trade_no/out_refund_no
        params.put("realPayment", t.getServiceFee());       // BigDecimal 元,wxRefund 内部 *100 转分
        String res;
        try {
            res = payService.wxRefund(params);
        } catch (Exception e) {
            log.error("VIP转让退款调用异常 transferId={}, orderNo={}", t.getTransferId(), t.getServiceFeeOrderNo(), e);
            throw new RRException(CodeAndMsg.ERROR_VIP_REFUND_FAIL);
        }
        if (res == null) {
            // 退款受理失败:抛异常回滚本次操作(状态不推进),保留可重试,避免"已改状态却没退钱"
            throw new RRException(CodeAndMsg.ERROR_VIP_REFUND_FAIL);
        }
        // 幂等置已退;命中0行=并发已退,不重复记退款流水
        int rows = vipBenefitTransferMapper.markRefunded(t.getTransferId());
        if (rows == 0) {
            return;
        }
        // 退款负向流水(payType=9退款,记在转让人名下,对方=受让人)
        incomePayDetailService.saveTransferRefund(
                t.getServiceFeeOrderNo(), t.getServiceFee(), t.getFromUserId(), t.getToUserId());
    }

    /** 站内信通知(system_msg),msgType=0 正常消息 */
    private void pushSystemMsg(Long userId, String record) {
        SystemMsgEntity msg = new SystemMsgEntity();
        msg.setUserId(userId);
        msg.setRecord(record);
        msg.setMsgType(0);
        msg.setSendTime(new Date());
        systemMsgDao.save(msg);
    }
}
