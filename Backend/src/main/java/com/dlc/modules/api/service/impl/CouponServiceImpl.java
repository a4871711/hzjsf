package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.CardOrderMapper;
import com.dlc.modules.api.dao.CouponMapper;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.Coupon;
import com.dlc.modules.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/12 9:22
 */
@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CardOrderMapper cardOrderMapper;


    public List<Map<String, Object>> couponList(Long userId,Integer couponStatus, Long storeAddrId, BigDecimal paySum, Long fitCardId, List<Integer> couponTypeList) {
        List<Map<String, Object>> list = couponMapper.selectByUserId(userId,couponStatus, storeAddrId, paySum, fitCardId, couponTypeList, null);
        return list;
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  扣除优惠券
     */
    @Override
    public Map<String, Object> deductCoupon(String orderNo, Coupon coupon) {
        //查出订单
        CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
        //更新订单
        cardOrder.setRealPayment(cardOrder.getPaySum().subtract(coupon.getCouponPrice()));
        //删除优惠券
        couponMapper.deleteByPrimaryKey(coupon.getCouponId());
        //返会订单号和要付的金额
        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",cardOrder.getOrderNo());
        map.put("money",cardOrder.getRealPayment());
        return map;
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  健身卡绑定优惠券
     */
    @Override
    public void bindingCoupon(Map<String, Object> params) {
        //根据订单号查询订单
        CardOrder cardOrder = cardOrderMapper.selectByOrderNo((String) params.get("orderNo"));
        cardOrder.setCouponId(Long.valueOf((String) params.get("couponId")));
        cardOrderMapper.updateByPrimaryKeySelective(cardOrder);
    }
    
    @Override
    public Coupon selectByCouponId(Long couponId) {
    	return couponMapper.selectByCouponId(couponId);
    }
}
