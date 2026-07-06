package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.MkMemberCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员领券记录 mk_member_coupon 的 api 侧 Dao(第18步:领券写入/我的券/可用券/券过期任务用)。
 * 与 sys.dao.MkMemberCouponDao 同表不同包不同 XML(mapper/api/MkMemberCouponApiDao.xml,改后须重启)。
 * 占用/核销/释放 CAS 已在 PtPrivateOrderCouponRelDao(第12/13/16步),本 Dao 不重复实现,统一那一套。
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkMemberCouponApiDao {

    /**
     * 领券写入:快照 coupon_name/coupon_type;expire_time = now + valid_days 落库即算;use_status=0。
     * 与 sys.dao.MkMemberCouponDao.save 同口径(sys 侧后台发券)。
     */
    int receive(@Param("memberId") Long memberId,
                @Param("couponId") Long couponId,
                @Param("couponName") String couponName,
                @Param("couponType") Integer couponType,
                @Param("validDays") Integer validDays);

    /**
     * 我的券列表:按 use_status 过滤(为空则全部);联 mk_coupon 带出券模板信息供展示。
     * 已过期未置 2 的(use_status=0 且 expire_time<=now)在 SQL 内实时算成「已过期」标签,展示口径与任务一致。
     */
    List<MkMemberCouponEntity> myList(@Param("memberId") Long memberId,
                                      @Param("useStatus") Integer useStatus);

    /**
     * 下单可用券候选:该会员 use_status=0 未使用 + 未过期的券,联 mk_coupon 带出模板字段。
     * 门槛/门店/指定商品/新人在 service 层用 §5.1 算法逐张判并预算抵扣,这里只出候选集。
     */
    List<MkMemberCouponEntity> selectUsableCandidates(@Param("memberId") Long memberId);

    /** 单张会员券(领券幂等校验、以及需要模板信息时用);联 mk_coupon 带出模板字段。查不到返回 null */
    MkMemberCouponEntity selectByIdWithCoupon(@Param("memberCouponId") Long memberCouponId,
                                              @Param("memberId") Long memberId);

    /**
     * 券过期批量任务(§7):use_status=0 未使用 且 expire_time<now → 置 2已过期。
     * 单条批量 UPDATE 自带原子幂等,无账本增减,直调不进事务切面。
     */
    int expireBatch();
}
