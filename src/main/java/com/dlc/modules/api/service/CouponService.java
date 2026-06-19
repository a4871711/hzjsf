package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CouponService {
    List<Map<String,Object>> couponList(Long userId,Integer couponStatus, Long storeAddrId, BigDecimal paySum, Long fitCardId, List<Integer> couponTypeList);

    Map<String,Object> deductCoupon(String orderNo, Coupon coupon);

    void bindingCoupon(Map<String, Object> params);

	Coupon selectByCouponId(Long couponId);
}
