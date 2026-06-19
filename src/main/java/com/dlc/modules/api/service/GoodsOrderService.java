package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.GoodsOrder;

import java.math.BigDecimal;
import java.util.Map;

public interface GoodsOrderService {
    Map<String,Object> addGoodsOrder(GoodsOrder goodsOrder);

    int balancePay(Long userId, Integer realPayment);

    BigDecimal queryUserMoney(Long userId);

//    void updateByOrderNo(String out_trade_no, String tradeNo,int status, Integer wxpay);

    void AddInPayDetail(Map<String, Object> map);

    Long queryUserIdByOrderNo(String out_trade_no);

    void delShoppingCar(String pkIds);

    GoodsOrder queryCouponMoneyByOrderNo(String orderNo);

    int updateByOrderNo(String orderNo, BigDecimal wallet, String transaction_id, int payType);

    int ifExistGoods(Long goodsId);
}
