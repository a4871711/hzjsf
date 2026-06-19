package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.Coupon;
import com.dlc.modules.api.entity.GoodsOrder;
import com.dlc.modules.api.entity.OrderDetail;
import com.dlc.modules.api.entity.UserAddress;
import com.dlc.modules.api.service.GoodsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/14 9:00
 */
@Service
public class GoodsOrderServiceImpl implements GoodsOrderService {
    @Autowired
    private GoodsOrderMapper goodsOrderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;

    public Map<String,Object> addGoodsOrder(GoodsOrder goodsOrder) {
        Map<String,Object> map = new HashMap<>();
        BigDecimal realPayment =goodsOrder.getRealPayment();
        //生成订单号
        String orderNoTemp = OrderNoGenerator.getOrderIdByTime();
        String orderNo = orderNoTemp + ConfigConstant.GOODS_ORDER_TYPE;
        goodsOrder.setOrderNo(orderNo);

        Long userId = goodsOrder.getUserId();

        UserAddress ua = userAddressMapper.selectByPrimaryKey(goodsOrder.getReceiveAddrId());
        int addOrder = 0;
        if(null != ua){
            goodsOrder.setReceiveName(ua.getUserName());
            goodsOrder.setReceivePhone(ua.getPhone());
            goodsOrder.setReceiveAddr(ua.getProvince()+ua.getCity()+ua.getZone()+ua.getAddr());
            addOrder = goodsOrderMapper.insertSelective(goodsOrder);
        }


        //详情
        if(addOrder>0){
            List<OrderDetail> orderDetailList = goodsOrder.getOrderDetailList();
            for(OrderDetail detail :orderDetailList){
                detail.setOrderNo(orderNo);
                detail.setUserId(goodsOrder.getUserId());

                //商品价格
                BigDecimal goodsPrice  = detail.getGoodsPrice();
                detail.setGoodsPrice(goodsPrice);
                //商品数量goodsNum
                int goodsNum = detail.getGoodsNum();
                //totalPrice(数据库触发)
                int addDetail = orderDetailMapper.insertSelective(detail);

                /*Long goodsId =detail.getGoodsId();
                this.shoppingCarMapper.delShoppingCar(userId,goodsId);*/
            }
        }else {
            return null;
        }
        map.put("orderNo",orderNo);
        map.put("realPayment",realPayment+"");
        return map;
    }


    public int balancePay(Long userId, Integer realPayment) {
        return userWalletMapper.balancePay(userId,realPayment);
    }


    public BigDecimal queryUserMoney(Long userId) {
        return userWalletMapper.queryUserMoney(userId);
    }


    public void updateByOrderNo(String out_trade_no, String tradeNo, int status, Integer wxpay) {
        Map<String ,Object> map = new HashMap<>();
        map.put("orderNo",out_trade_no);
        map.put("status",status);
        map.put("payType",wxpay);
        this.goodsOrderMapper.updateByOrderNo(map);
    }


    public void AddInPayDetail(Map<String, Object> map) {
        this.incomePayDetailMapper.AddInPayDetail(map);
    }


    public Long queryUserIdByOrderNo(String out_trade_no) {
        return goodsOrderMapper.queryUserIdByOrderNo(out_trade_no);
    }


    public void delShoppingCar(String pkIds) {
        String[] arr = pkIds.split(",");
        int reult = 0;
        for(String temp:arr){
            reult = this.shoppingCarMapper.deleteByPrimaryKey(Long.valueOf(temp));
        }
    }


    public GoodsOrder queryCouponMoneyByOrderNo(String orderNo) {
        return this.goodsOrderMapper.queryCouponMoneyByOrderNo(orderNo);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  支付后更新商品订单
     */
    @Override
    public int updateByOrderNo(String orderNo, BigDecimal wallet, String transaction_id, int payType) {
        //根据订单号查出商品订单
        GoodsOrder goodsOrder = goodsOrderMapper.selectGoodOrderByOrder(orderNo);
        //流水号
        goodsOrder.setTransactionNo(transaction_id);
        //支付类型
        goodsOrder.setPayType((byte) payType);
        //订单状态
        goodsOrder.setStatus((byte) 1);
        //实付金额
        goodsOrder.setRealPayment(wallet);
        //完成时间
        //goodsOrder.setFinish(new Date());
        //支付时间payTime
        goodsOrder.setPayTime(new Date());

        int result = goodsOrderMapper.updateByPrimaryKeySelective(goodsOrder);

        //扣除优惠券
        if (goodsOrder.getCouponId() != null && goodsOrder.getCouponId() != 0 ){
            Coupon coupon = couponMapper.selectByCouponId(goodsOrder.getCouponId());
            coupon.setCouponStatus(1);
            couponMapper.updateByPrimaryKeySelective(coupon);
        }
        return result;
    }

    @Override
    public int ifExistGoods(Long goodsId) {
        return goodsOrderMapper.queryIfExistGoods(goodsId);
    }
}
