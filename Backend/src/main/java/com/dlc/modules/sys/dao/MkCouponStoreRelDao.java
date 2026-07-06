package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkCouponStoreRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优惠券可用门店关联 Dao（mk_coupon_store_rel）
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkCouponStoreRelDao extends BaseDao<MkCouponStoreRelEntity> {

    int deleteByCouponId(Long couponId);

    List<Long> queryStoreIds(Long couponId);
}
