package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.VipBenefitTransfer;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 权益转让单(vip_benefit_transfer)Mapper(移动端)
 * 第10步:发起建单、在途占用查重、服务费回调置待审核、记账反查、我的转让/受让记录。
 * 第11步:按主键查/行锁查、过户单事务推进状态机(40→70)。
 */
public interface VipBenefitTransferMapper {

    /** 发起转让:建转让单(费用>0→status=10待付费;=0→20待审核) */
    int insertSelective(VipBenefitTransfer transfer);

    /** 同一权益在途(10待付费/20待审核/40待确认)转让单数量,>0 即已有进行中的转让 */
    int countInProgress(@Param("vipBenefitId") Long vipBenefitId);

    /** 服务费支付成功回调:仅 status=10→20 并写微信交易号,幂等(重复回调命中0行) */
    int feePaid(@Param("feeOrderNo") String feeOrderNo,
                @Param("transactionNumber") String transactionNumber);

    /** 按服务费订单号反查转让单(记账反查转让人 userId/storeId 用) */
    VipBenefitTransfer selectByFeeOrderNo(@Param("orderNo") String orderNo);

    /** 我的转让/受让记录分页(role:1我发起 2我接收 空=全部;可按 status 过滤;带卡名) */
    List<VipBenefitTransfer> selectMyList(Map<String, Object> params);

    /** 我的转让/受让记录总数(与 selectMyList 同条件) */
    int countMyList(Map<String, Object> params);

    /** 按主键查转让单(不带锁;confirm 过户后重读最终态返回给前端用) */
    VipBenefitTransfer selectById(@Param("transferId") Long transferId);

    /** 按主键行锁查转让单(过户 transferEffect 用,串行化确认/超时/撤回三方竞争,附录C) */
    VipBenefitTransfer selectByIdForUpdate(@Param("transferId") Long transferId);

    /** 过户生效:幂等推进 40→70 并写生效时间,重复调用命中0行直接返回 */
    int effect(@Param("transferId") Long transferId, @Param("effectTime") Date effectTime);

    // ====================== 第12步:撤回/拒绝/超时/退费 ======================

    /** 转让人撤回(待审核阶段):20→60,退费。命中0行=状态已变 */
    int withdraw20(@Param("transferId") Long transferId);

    /** 转让人撤回(待受让人确认阶段):40→60,不退费。命中0行=状态已变 */
    int withdraw40(@Param("transferId") Long transferId);

    /** 受让人拒绝:40→51,退费。带 to_user_id 校验,命中0行=非本人或状态已变 */
    int toReject(@Param("transferId") Long transferId, @Param("toUserId") Long toUserId);

    /** 受让人确认超时(定时任务):40→52,退费。命中0行=已被确认/撤回 */
    int timeout(@Param("transferId") Long transferId);

    /** 幂等置已退费:refund_status 0→1。命中0行=已退过,不重复记退款流水 */
    int markRefunded(@Param("transferId") Long transferId);

    /** 扫描待确认超时单:status=40 且 confirm_deadline < now(定时任务逐笔处理) */
    List<VipBenefitTransfer> selectTimeout(@Param("now") Date now);

    // ====================== 第13步:后台审核 ======================

    /** 审核通过:20→40,写审核人/时间/备注/确认截止时间。命中0行=状态已变 */
    int auditPass(@Param("transferId") Long transferId,
                  @Param("auditUserId") Long auditUserId,
                  @Param("auditTime") Date auditTime,
                  @Param("auditRemark") String auditRemark,
                  @Param("confirmDeadline") Date confirmDeadline);

    /** 审核驳回:20→31,写审核人/时间/备注。命中0行=状态已变 */
    int auditReject(@Param("transferId") Long transferId,
                    @Param("auditUserId") Long auditUserId,
                    @Param("auditTime") Date auditTime,
                    @Param("auditRemark") String auditRemark);
}
