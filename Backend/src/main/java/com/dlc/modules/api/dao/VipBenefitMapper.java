package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitCard;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户持有权益卡实例(vip_benefit)Mapper(移动端)
 */
public interface VipBenefitMapper {

    /** 下单时建待支付权益占位(status=9) */
    int insertSelective(VipBenefit vipBenefit);

    /** 按购买订单号反查权益实例(记账反查 userId 用) */
    VipBenefit selectByOrderNo(@Param("orderNo") String orderNo);

    /** 按购买订单号关联取来源权益卡(只取 vipCardId / validityDays,供激活算到期) */
    VipBenefitCard selectCardByOrderNo(@Param("orderNo") String orderNo);

    /** 幂等激活:仅 status=9 待支付时置正常并写生效/到期,返回受影响行数 */
    int activate(@Param("orderNo") String orderNo,
                 @Param("startTime") Date startTime,
                 @Param("expireTime") Date expireTime);

    /** 真实购买人数自增(仅购买激活时 +1,转让不动) */
    int incrSoldCount(@Param("vipCardId") Long vipCardId);

    /** 我的权益分页列表(按 userId + status,带卡名) */
    List<VipBenefit> selectMyBenefits(Map<String, Object> params);

    /** 我的权益总数 */
    int countMyBenefits(Map<String, Object> params);
}
