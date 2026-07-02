package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.MemberBlacklistMapper;
import com.dlc.modules.api.dao.VipBenefitCardMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.VipBenefitTransferMapper;
import com.dlc.modules.api.dao.VipFeeRuleMapper;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.entity.VipBenefitTransfer;
import com.dlc.modules.api.entity.VipFeeRule;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.api.service.VipTransferService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
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
}
