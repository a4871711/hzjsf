package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitTransfer;
import com.dlc.modules.api.entity.VipFeeRule;
import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益转让 Service(移动端)。
 * 第9步只交付两段可复用构件 + 一个试算接口:
 *   - checkTransferable:转让前置校验集(发起 apply 与后台 audit 两处复用同一份,见附录E)
 *   - calcTransferFee:按转让次数分档算服务费
 *   - quote:试算费用接口(只读,不落单不收费)
 * 发起/撤回/确认/拒绝/回调/过户等状态机在第10~13步实现。
 */
public interface VipTransferService {

    /**
     * 转让前置校验集(详细技术设计 §8.2 + 附录A 码表)。
     * 任一规则命中即抛 RRException(对应 CodeAndMsg),无返回即视为通过。
     * 发起(apply)与审核(audit)两处都必须调用同一份:其间权益可能过期、双方可能被拉黑/封禁、
     * 权益卡可能下架、受让人门店可能变动,审核时需以审核当下数据重跑。
     *
     * @param benefit  被转让的权益实例(调用方按 vip_benefit_id 加锁查出后传入,可能为 null)
     * @param fromUser 转让人
     * @param toUser   受让人(按 toUserId 查 user_info,可能为 null=受让人不存在)
     */
    void checkTransferable(VipBenefit benefit, UserInfoVo fromUser, UserInfoVo toUser);

    /**
     * 计算本次转让费用(转让人缴,D-3/D-4;详细技术设计 §8.4)。
     * 本次为该权益第 (transfer_count+1) 次转让,在 tiers 中取 fromCount<=本次 的最大档 fee。
     * 空规则/规则停用/无匹配档 → 返回 0(免费)。
     *
     * @param benefit 被转让权益,取其 transfer_count
     * @param feeRule 关联的 vip_fee_rule(可能为 null=权益卡未配规则=免费)
     * @return 本次应缴服务费,DECIMAL(10,2) 口径
     */
    BigDecimal calcTransferFee(VipBenefit benefit, VipFeeRule feeRule);

    /**
     * 试算转让费用(GET/POST /api/vipTransfer/quote,只读)。
     * 校验权益存在且属当前用户,按 transfer_count+1 命中分档算费用,不落单、不收费。
     *
     * @param userId       当前登录用户
     * @param vipBenefitId 待转让权益实例ID
     * @return {vipBenefitId, transferCount, thisTransferNo, serviceFee}
     */
    Map<String, Object> quote(Long userId, Long vipBenefitId);

    /**
     * 发起转让(POST /api/vipTransfer/apply,§5.6 + §7.3.1 + 附录C.1)。
     * 单事务内:行锁权益 → checkTransferable 全量前置校验 → 在途占用查重(同权益仅一笔在途) →
     * 按 transfer_count+1 算服务费 → 建转让单。
     * 服务费>0 → status=10待付费,生成末位后缀7的服务费单号,返回 {orderNo,paySum} 供前端调 /wx/proPay;
     * 服务费=0 → status=20直接待审核。
     *
     * @param fromUser 转让人(getUserVo,已校验封禁)
     * @param vipBenefitId 待转让权益实例ID
     * @param toUserId 受让人 userId
     * @return {transferId, status, serviceFee[, orderNo, paySum]}
     */
    Map<String, Object> apply(UserInfoVo fromUser, Long vipBenefitId, Long toUserId);

    /**
     * 我的转让/受让记录(GET /api/vipTransfer/myList)。
     * params:userId(必)、role(1我发起 2我接收 空=全部)、status(可选)、page/limit。
     */
    PageUtils myList(Map<String, Object> params);

    /**
     * 服务费支付成功回调(§7.3.3 + 附录B.3):单事务内 status=10→20待审核 + 记账(用途=7),幂等。
     * @return 受影响行数(0=重复回调/状态已变,不重复记账)
     */
    int payFeeCallback(String feeOrderNo, BigDecimal money, String transactionNumber, Integer payType);

    /**
     * 受让人确认接收(过户入口,POST /api/vipTransfer/confirm,§7.3.5)。
     * 仅受让人本人、仅 status=40 且 now&lt;=confirm_deadline 可确认(不付费);
     * 校验通过后调用 {@link #transferEffect} 完成过户,返回过户后的最新状态。
     *
     * @param toUser     受让人(getUserVo,已校验封禁)
     * @param transferId 转让单ID
     * @return {transferId, status, effectTime}
     */
    Map<String, Object> confirm(UserInfoVo toUser, Long transferId);

    /**
     * 过户单事务(§7.4 + 附录B.6/C.3):由 {@link #confirm} 调用,同一方法即同一事务。
     * 行锁转让单(非40态直接幂等返回)→行锁权益(校验仍为0正常)→changeOwner(user_id 换受让人、
     * transfer_count+1、transferable=1;store_id/store_addr_id 不动即附录D.3、expire_time 不动即D-10继承)→
     * 转让单 status 40→70 + effect_time→服务费单补记 anotherId(不重复入账,附录B.6)→推送双方(站内信)。
     * changeOwner 命中0行且本单仍是首次执行(说明权益被其他转让单抢先过户,附录C.3"否则"分支)时,
     * 抛异常回滚,本单转让保持40可重试,不让受让人得到"确认成功"却未获得权益的假象。
     *
     * @param transferId 转让单ID
     */
    void transferEffect(Long transferId);

    /**
     * 转让人撤回(POST /api/vipTransfer/withdraw,§5.8 + §7.3.7 + 附录D.1)。
     * 单事务、行锁转让单:20待审核→60且全额退服务费;40待受让人确认→60但不退费;其余状态报错。
     * @param fromUserId 转让人(getUserId)
     * @param transferId 转让单ID
     * @return {transferId, status:60}
     */
    Map<String, Object> withdraw(Long fromUserId, Long transferId);

    /**
     * 受让人拒绝(POST /api/vipTransfer/reject,§5.10 + §7.3.6)。
     * 单事务:仅本人、仅 status=40 可拒绝,40→51 并全额退服务费,不动权益归属。
     * @param toUserId   受让人(getUserId)
     * @param transferId 转让单ID
     * @return {transferId, status:51}
     */
    Map<String, Object> reject(Long toUserId, Long transferId);

    /**
     * 扫描待受让人确认超时单(§7.6,供定时任务逐笔处理):status=40 且 confirm_deadline < now。
     * 只读查询,不改状态;实际关单退费由 {@link #timeoutOne} 逐笔单事务完成。
     */
    List<VipBenefitTransfer> listConfirmTimeout();

    /**
     * 逐笔处理一个超时单(§7.6,单事务):40→52 已超时 + 全额退服务费 + 推送双方。
     * 幂等:命中0行(已被确认/撤回)直接返回,不退费。单笔失败由调用方(定时任务)捕获、不影响其余。
     * @param transferId 转让单ID
     */
    void timeoutOne(Long transferId);

    /**
     * 后台审核(§7.3.4 + §6.3 + 附录E,由 sys SysVipTransferController 调用,单事务落在本 api service)。
     * 仅处理 status=20 待审核单。pass=1 通过:审核当下重跑 checkTransferable 复核,
     * 复核过→20→40 写 confirm_deadline 并推送受让人;复核不过→按驳回处理(20→31+退费),remark 标"系统复核:xxx"。
     * pass!=1 人工驳回:20→31 + 全额退服务费 + 推送转让人。任一失败(状态已变/退款失败)抛 RRException 回滚。
     * @param transferId  转让单ID
     * @param auditUserId 审核管理员ID(ShiroUtils.getUserId())
     * @param pass        1=通过 其余=驳回
     * @param remark      审核备注/驳回原因
     */
    void audit(Long transferId, Long auditUserId, Integer pass, String remark);
}
