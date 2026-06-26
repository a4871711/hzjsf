package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CouponMapper {
    int deleteByPrimaryKey(Long couponId);

    int insert(Coupon record);

    int insertSelective(Coupon record);

    Coupon selectByPrimaryKey(Long couponId);

    int updateByPrimaryKeySelective(Coupon record);

    int updateByPrimaryKey(Coupon record);

    int updateCouponStatus(Map<String, Object> updateMap);

    List<Map<String,Object>> selectByUserId(@Param("userId") Long userId,@Param("couponStatus") Integer couponStatus, 
    		@Param("storeAddrId") Long storeAddrId, @Param("paySum") BigDecimal paySum, @Param("fitCardId") Long fitCardId, @Param("couponTypeList") List<Integer> couponTypeList, @Param("couponNew") Long couponNew);

    /**
     * 查询我的全部优惠券列表
     * @return
     */
    List<Map<String,Object>> queryCouponByUserId(Map<String, Object> params);

    int queryCouponCount(Map<String, Object> params);

    Coupon selectByCouponId(Long couponId);

    List<Coupon> selectAllCoupons();

    /** 同一手机号或同一 openid（含公众号/支付宝/代扣）是否已领取过新人券 */
    int countNewUserCouponByUserId(@Param("userId") Long userId);
}