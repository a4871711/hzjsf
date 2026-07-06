package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.dao.PtMemberWalletDao;
import com.dlc.modules.api.dao.PtMemberWalletFlowDao;
import com.dlc.modules.api.entity.PtMemberWalletEntity;
import com.dlc.modules.api.entity.PtMemberWalletFlowEntity;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.PtMemberWalletService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.SortedMap;

/**
 * 会员储值账户 Service 实现(第19步资金域)。落在 api.service.impl,命中事务切面(REQUIRED)。
 * <p>类名 PtMemberWalletServiceImpl / 组件名 ptMemberWalletService,与旧提现钱包 WalletServiceImpl 严格区分。</p>
 * <p>api 目录 XML(PtMemberWalletDao.xml / PtMemberWalletFlowDao.xml)改动须重启 Tomcat。</p>
 *
 * @author claude
 */
@Service("ptMemberWalletService")
public class PtMemberWalletServiceImpl implements PtMemberWalletService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** 流水类型:1充值 */
    private static final int FLOW_RECHARGE = 1;
    /** 流水类型:2消费 */
    private static final int FLOW_CONSUME = 2;
    /** 流水类型:3退款 */
    private static final int FLOW_REFUND = 3;
    /** 业务类型:1订单 */
    private static final int BIZ_ORDER = 1;
    /** 业务类型:2退款单 */
    private static final int BIZ_REFUND = 2;

    /**
     * 充值单号→会员ID 的 Redis 锚点前缀。recharge 时写入,walletRechargeCallback 反查(读后删)。
     * 用 Redis 而非新建"充值中"流水,是为守住"充值不预改余额、不写占位流水"的口径;
     * TTL 取 2 小时(覆盖微信支付会话有效期)。若锚点过期丢失,回调按"取不到会员"记日志跳过,
     * 微信重试期内会员通常仍在有效期;真正的重复入账防线是 flow.out_order_no 唯一键。
     */
    private static final String RECHARGE_MEMBER_PREFIX = "hzjsf_pt_wallet_recharge_";
    private static final long RECHARGE_MEMBER_TTL = 2 * 60 * 60;

    @Autowired
    private PtMemberWalletDao ptMemberWalletDao;
    @Autowired
    private PtMemberWalletFlowDao ptMemberWalletFlowDao;
    @Autowired
    private PayService payService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public PtMemberWalletFlowEntity changeBalance(Long memberId, BigDecimal changeAmount, Integer flowType,
                                                  Integer bizType, Long bizId, String outOrderNo,
                                                  String remark, Long operatorId) {
        if (memberId == null || changeAmount == null || flowType == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        BigDecimal delta = scale(changeAmount);

        // 1) 行锁取账户;不存在则首充建户(uk_member_id 兜底并发首充只建一行)再锁
        PtMemberWalletEntity wallet = ptMemberWalletDao.selectByMemberIdForUpdate(memberId);
        if (wallet == null) {
            ptMemberWalletDao.insertOnDuplicate(memberId);
            wallet = ptMemberWalletDao.selectByMemberIdForUpdate(memberId);
        }
        if (wallet == null) {
            // 理论不可达(insertOnDuplicate 后必有行);兜底防 NPE
            throw new RRException(CodeAndMsg.ERROR_WALLET_NOT_EXIST);
        }

        // 2) 冻结账户拒绝一切余额变动
        if (wallet.getStatus() != null && wallet.getStatus() == 2) {
            throw new RRException(CodeAndMsg.ERROR_WALLET_FROZEN);
        }

        // 3) 行锁内基于 before 算 after,after<0 表示扣款余额不足
        BigDecimal before = scale(wallet.getBalanceAmount());
        BigDecimal after = scale(before.add(delta));
        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw new RRException(CodeAndMsg.ERROR_WALLET_BALANCE_NOT_ENOUGH);
        }

        // 4) 改账户:balance 直接写 after(与流水快照同源);累计充值/消费按流水类型增量
        BigDecimal rechargeInc = flowType == FLOW_RECHARGE ? delta : BigDecimal.ZERO;
        BigDecimal consumeInc = flowType == FLOW_CONSUME ? delta.abs() : BigDecimal.ZERO;
        int updated = ptMemberWalletDao.updateBalance(wallet.getId(), after, rechargeInc, consumeInc);
        if (updated == 0) {
            // 持有行锁仍更新失败=逻辑异常,回滚
            throw new RRException("储值余额更新失败");
        }

        // 5) 写不可变流水:before/after 快照;out_order_no 撞唯一键(并发重复)由事务回滚兜底
        PtMemberWalletFlowEntity flow = new PtMemberWalletFlowEntity();
        flow.setWalletId(wallet.getId());
        flow.setMemberId(memberId);
        flow.setFlowType(flowType);
        flow.setChangeAmount(delta);
        flow.setBeforeBalance(before);
        flow.setAfterBalance(after);
        flow.setBizType(bizType);
        flow.setBizId(bizId);
        flow.setRemark(remark);
        flow.setCreatedBy(operatorId);
        flow.setOutOrderNo(StringUtils.isBlank(outOrderNo) ? null : outOrderNo);
        ptMemberWalletFlowDao.save(flow);
        return flow;
    }

    @Override
    public SortedMap<String, String> recharge(Long userId, BigDecimal amount, HttpServletRequest request) {
        if (userId == null) {
            throw new RRException(CodeAndMsg.ERROR_USER_NOT_LOGIN);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 账户冻结时不允许充值(无账户视为首充,放行,待回调建户加钱)
        PtMemberWalletEntity wallet = ptMemberWalletDao.selectByMemberId(userId);
        if (wallet != null && wallet.getStatus() != null && wallet.getStatus() == 2) {
            throw new RRException(CodeAndMsg.ERROR_WALLET_FROZEN);
        }

        // 充值单号:OrderNoGenerator + 后缀"8"(§0.6 仲裁,回调按末位分发)
        String orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.WALLET_RECHARGE_TYPE;
        // 记 orderNo→memberId 供回调反查(不预改余额、不写占位流水)
        redisUtils.set(RECHARGE_MEMBER_PREFIX + orderNo, String.valueOf(userId), RECHARGE_MEMBER_TTL);

        try {
            // notifyType=null:沿用现有购卡回调地址 NOTIFY_WX_URL(/api/pay/wxNotify),回调后缀"8"分发到本 service
            SortedMap<String, String> payParams = payService.wxRechargePay(scale(amount), orderNo, null);
            if (payParams == null) {
                throw new RRException(CodeAndMsg.ERROR_PAY_ERROR);
            }
            return payParams;
        } catch (RRException e) {
            throw e;
        } catch (Exception e) {
            log.error("储值充值下单失败 orderNo={}", orderNo, e);
            throw new RRException(CodeAndMsg.ERROR_PAY_ERROR);
        }
    }

    @Override
    public void walletRechargeCallback(String orderNo, BigDecimal amount, String transactionId, Integer payType) {
        if (StringUtils.isBlank(orderNo) || amount == null) {
            log.warn("储值充值回调:参数缺失 orderNo={},amount={}", orderNo, amount);
            return;
        }
        // 幂等闸1:该充值单号已有流水=已处理,直接返回,不重复加钱/记账
        PtMemberWalletFlowEntity exist = ptMemberWalletFlowDao.selectByOutOrderNo(orderNo);
        if (exist != null) {
            log.info("储值充值回调:已处理过,幂等跳过 orderNo={}", orderNo);
            return;
        }
        // 反查会员:recharge 时写入 Redis 的 orderNo→memberId
        String memberIdStr = redisUtils.get(RECHARGE_MEMBER_PREFIX + orderNo);
        if (StringUtils.isBlank(memberIdStr)) {
            // 锚点过期/丢失:无法定位会员,记日志跳过(不抛异常,避免微信无限重试轰炸)
            log.error("储值充值回调:orderNo→memberId 锚点丢失,无法入账 orderNo={}", orderNo);
            return;
        }
        Long memberId = Long.valueOf(memberIdStr);

        // 先加余额+写充值流水(幂等闸2:changeBalance 内 flow.out_order_no 唯一键撞键=并发重复,事务回滚兜底);
        // 必须先于记账:IncomePayDetail 后缀"8"分支靠 flow.selectByOutOrderNo 反查会员,故流水要先落
        changeBalance(memberId, scale(amount), FLOW_RECHARGE, null, null, orderNo, "微信充值", null);
        // 再记收支明细(真实收款,与其它订单回调同口径),后缀"8"分支按 out_order_no 反查 memberId/storeId
        incomePayDetailService.saveIncomePayDetail(orderNo, transactionId, scale(amount), payType);
        // 入账成功,清锚点(失败也无妨:闸1会拦下重复回调)
        redisUtils.delete(RECHARGE_MEMBER_PREFIX + orderNo);
        log.info("储值充值回调:入账成功 orderNo={},memberId={},amount={}", orderNo, memberId, amount);
    }

    @Override
    public PtMemberWalletFlowEntity payByWallet(String orderNo, Long orderId, Long memberId, BigDecimal amount) {
        if (StringUtils.isBlank(orderNo) || memberId == null || amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 只做资金侧:扣余额+写消费流水;结算(库存/券/建权益/结清)由 updatePrivateOrder 复用同一口径
        // 消费为负;out_order_no=订单号做幂等(同单重复扣款撞唯一键回滚);余额不足由 changeBalance 抛 -102
        return changeBalance(memberId, scale(amount).negate(), FLOW_CONSUME,
                BIZ_ORDER, orderId, orderNo, "私教商品消费", null);
    }

    @Override
    public PtMemberWalletFlowEntity walletRefund(Long memberId, BigDecimal amount, String orderNo,
                                                 Long orderId, Long operatorId) {
        if (memberId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 退款为正,flowType=3;out_order_no 用退款单号(避免与购买流水的订单号撞唯一键);
        // changeBalance 内 consumeInc 仅对 flowType==2 生效,故退款不冲减 total_consume_amount(口径=历史发生额)
        String refundOutNo = StringUtils.isBlank(orderNo) ? null : ("R" + orderNo);
        return changeBalance(memberId, scale(amount), FLOW_REFUND,
                BIZ_REFUND, orderId, refundOutNo, "储值订单退款", operatorId);
    }

    @Override
    public boolean freeze(Long memberId) {
        PtMemberWalletEntity wallet = ptMemberWalletDao.selectByMemberId(memberId);
        if (wallet == null) {
            return false;
        }
        return ptMemberWalletDao.freeze(wallet.getId()) > 0;
    }

    @Override
    public boolean unfreeze(Long memberId) {
        PtMemberWalletEntity wallet = ptMemberWalletDao.selectByMemberId(memberId);
        if (wallet == null) {
            return false;
        }
        return ptMemberWalletDao.unfreeze(wallet.getId()) > 0;
    }

    /** 金额统一两位小数,HALF_UP(与 DECIMAL(10,2) 对齐,禁 double) */
    private BigDecimal scale(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP);
    }
}
