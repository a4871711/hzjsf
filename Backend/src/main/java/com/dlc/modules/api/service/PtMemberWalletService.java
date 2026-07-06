package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.PtMemberWalletFlowEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.SortedMap;

/**
 * 会员储值账户 Service(第19步资金域)。
 * <p>严禁与旧提现钱包 {@link WalletService} 混淆:本类只服务私教专用储值余额主账户
 * (pt_member_wallet / pt_member_wallet_flow),旧 WalletService 走 UserWallet/WalletDetail 提现体系,两者不共用。</p>
 *
 * <p><b>余额红线(唯一资金入口 {@link #changeBalance}):</b>
 * 任何余额变动必须走 changeBalance —— 行锁 selectByMemberIdForUpdate → 校验非冻结 →
 * after=before+change(after&lt;0 拒绝) → updateBalance 写 after → 插不可变流水(before/after 快照)。
 * 禁止任何"先查后改"的无锁路径直改 balance_amount。</p>
 *
 * <p>落在 api.service.impl,命中事务切面(REQUIRED),单次方法调用即一个事务边界。</p>
 *
 * @author claude
 */
public interface PtMemberWalletService {

    /**
     * 唯一资金变动入口(充值/扣款/退款/冲正共用,行锁内原子完成)。
     * <ol>
     *   <li>selectByMemberIdForUpdate 锁账户行;不存在先 insertOnDuplicate 建户(uk_member_id 幂等)再锁;</li>
     *   <li>校验 status==1 正常,冻结抛 ERROR_WALLET_FROZEN;</li>
     *   <li>after = before + changeAmount(带符号);after&lt;0 抛 ERROR_WALLET_BALANCE_NOT_ENOUGH;</li>
     *   <li>updateBalance 写 after(rechargeInc=flowType==1?change:0、consumeInc=flowType==2?|change|:0);</li>
     *   <li>flowDao.save 写流水:outOrderNo 撞唯一键(并发重复回调/重复扣款)抛 DuplicateKeyException,由事务整体回滚做 DB 级幂等兜底。</li>
     * </ol>
     *
     * @param memberId    会员ID(= user_info.userId)
     * @param changeAmount 变动金额,带符号:充值/退款为正、消费为负
     * @param flowType    1充值 2消费 3退款 4冲正
     * @param bizType     关联业务类型:1订单 2退款单 3人工调整(充值可为 null)
     * @param bizId       关联业务ID(订单ID/退款单ID,充值可为 null)
     * @param outOrderNo  外部业务单号(充值单号/订单号/退款号),回调与重复提交幂等键(可为 null)
     * @param remark      备注(后台充值/冲正必填来源)
     * @param operatorId  操作人(后台充值/冲正记管理员ID;会员自助充值/消费为 null)
     * @return 写入的流水(含 before/after 快照)
     */
    PtMemberWalletFlowEntity changeBalance(Long memberId, BigDecimal changeAmount, Integer flowType,
                                           Integer bizType, Long bizId, String outOrderNo,
                                           String remark, Long operatorId);

    /**
     * 会员端发起储值充值(走微信,复用现有支付通道,不预改余额)。
     * <p>生成充值单号(OrderNoGenerator + 后缀"8") → 记 orderNo→memberId 到 Redis(供回调反查) →
     * 调 payService.wxRechargePay(amount, orderNo, null) 返回 App 支付调起参数。
     * 余额在支付成功回调 {@link #walletRechargeCallback} 里才加。</p>
     *
     * @param userId  当前登录会员ID
     * @param amount  充值金额(>0)
     * @param request 透传(与旧支付调用保持一致)
     * @return 微信 App 支付调起参数(wxRechargePay 的 SortedMap)
     */
    SortedMap<String, String> recharge(Long userId, BigDecimal amount, HttpServletRequest request);

    /**
     * 储值充值支付成功回调(微信,回调后缀"8")。金额取回调 total_fee(与其它订单类型一致,无需充值单表)。
     * <p><b>两道幂等闸:</b>闸1=flowDao.selectByOutOrderNo(orderNo) 已存在直接返回;
     * 闸2=changeBalance 内 flow.out_order_no 唯一键撞键回滚。</p>
     * <p>memberId 来源:recharge 时写入 Redis 的 orderNo→memberId(读后删)。</p>
     *
     * @param orderNo       充值单号(末位"8")
     * @param amount        到账金额(元,来自回调 total_fee)
     * @param transactionId 微信流水号(留痕,记入 income_pay_detail)
     * @param payType       收支方式(ConfigConstant.WXPAY 等)
     */
    void walletRechargeCallback(String orderNo, BigDecimal amount, String transactionId, Integer payType);

    /**
     * 储值支付私教订单(payMethod=3):同事务内 changeBalance(-应付金额,flowType=2消费)扣余额。
     * <p>本方法只负责"扣余额+写消费流水"这一资金侧动作,<b>不写第二套结算</b>;
     * 订单结清/扣库存/券核销/建权益复用第13步 updatePrivateOrder 的唯一结算口径(调用方扣款成功后 fall through)。
     * 余额不足抛 ERROR_WALLET_BALANCE_NOT_ENOUGH,整单回滚。</p>
     *
     * @param orderNo  私教订单号(消费流水 out_order_no,同单重复扣款撞唯一键回滚做幂等)
     * @param orderId  私教订单ID(记入 flow.biz_id 便于对账)
     * @param memberId 会员ID
     * @param amount   本单应付金额(订单快照 payableAmount)
     * @return 写入的消费流水
     */
    PtMemberWalletFlowEntity payByWallet(String orderNo, Long orderId, Long memberId, BigDecimal amount);

    /**
     * 储值支付订单退款原路回退(仅 payMethod=3):changeBalance(+退款额,flowType=3退款)。
     * <p>退款不冲减 total_consume_amount(统计口径=历史发生额)。</p>
     *
     * @param memberId   会员ID
     * @param amount     退款金额(>0)
     * @param orderNo    原订单号(退款流水 out_order_no 用退款单号避免与购买流水撞唯一键)
     * @param orderId    原订单ID(记入 flow.biz_id)
     * @param operatorId 操作人(后台退款记管理员ID)
     * @return 写入的退款流水
     */
    PtMemberWalletFlowEntity walletRefund(Long memberId, BigDecimal amount, String orderNo, Long orderId, Long operatorId);

    /** 后台冻结账户:status 1→2(CAS),命中0行=已冻结/不存在,返回 false 供上层提示 */
    boolean freeze(Long memberId);

    /** 后台解冻账户:status 2→1(CAS),命中0行=未冻结/不存在,返回 false 供上层提示 */
    boolean unfreeze(Long memberId);
}
