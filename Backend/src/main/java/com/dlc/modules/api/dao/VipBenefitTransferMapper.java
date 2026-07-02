package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.VipBenefitTransfer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 权益转让单(vip_benefit_transfer)Mapper(移动端)
 * 第10步:发起建单、在途占用查重、服务费回调置待审核、记账反查、我的转让/受让记录。
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
}
