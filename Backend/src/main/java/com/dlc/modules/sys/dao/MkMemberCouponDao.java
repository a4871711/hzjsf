package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkMemberCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 会员优惠券领取记录 Dao（mk_member_coupon）。
 * sys 侧只负责后台发券 insert 与只读列表；核销/占用 CAS 在 api 侧（第18步）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkMemberCouponDao extends BaseDao<MkMemberCouponEntity> {

    /** 券是否已被领取（delete 校验：有领取记录则拒删，改用下架） */
    int countByCouponId(Long couponId);
}
