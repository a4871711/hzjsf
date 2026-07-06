package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkCouponProductRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优惠券指定商品关联 Dao（mk_coupon_product_rel）
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkCouponProductRelDao extends BaseDao<MkCouponProductRelEntity> {

    int deleteByCouponId(Long couponId);

    List<Long> queryProductIds(Long couponId);
}
