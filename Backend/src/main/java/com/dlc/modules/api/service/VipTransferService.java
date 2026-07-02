package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipFeeRule;
import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
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
}
