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

    /** 按主键查权益实例(转让试算/校验用,不带状态过滤) */
    VipBenefit selectById(@Param("vipBenefitId") Long vipBenefitId);

    /** 按主键行锁查权益实例(发起转让 apply 串行化,防并发链式转让/回调读到旧持有人) */
    VipBenefit selectByIdForUpdate(@Param("vipBenefitId") Long vipBenefitId);

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

    /**
     * 过户改归属(第11步 transferEffect 用):user_id 换受让人 + transfer_count+1 + transferable=1;
     * store_id/store_addr_id 不动(附录D.3,归属门店保持原样)、expire_time 不动(继承剩余有效期,D-10)。
     * WHERE 带 fromUserId+status=0 双重幂等/并发保护,命中0行由调用方区分"已被本单处理"与"被他单抢先"(附录C.3)。
     */
    int changeOwner(@Param("vipBenefitId") Long vipBenefitId,
                     @Param("fromUserId") Long fromUserId,
                     @Param("toUserId") Long toUserId);

    /** 我的权益分页列表(按 userId + status,带卡名) */
    List<VipBenefit> selectMyBenefits(Map<String, Object> params);

    /** 我的权益总数 */
    int countMyBenefits(Map<String, Object> params);

    /** 该用户名下「正常且未过期」的有效权益卡数量(>0 即为权益会员) */
    int countValidByUser(@Param("userId") Long userId);
}
