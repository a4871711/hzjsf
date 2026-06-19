package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.dc.pr.PRError;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/18/018
 */
@Service
@Transactional
public class IncomePayDetailServiceImpl implements IncomePayDetailService {

    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;
    @Autowired
    private TeamClassOrderMapper teamClassOrderMapper;
    @Autowired
    private GoodsOrderMapper goodsOrderMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    /**
     *  @Auther:YD
     *  @parameters:
     *  支付后添加收支明细
     */
    @Override
    public int saveIncomePayDetail(String orderNo, String transaction_id, BigDecimal wallet, Integer payType) {


        IncomePayDetail ipd = new IncomePayDetail();
        //支付用途
        ipd.setPayType(Integer.valueOf(orderNo.substring(orderNo.length()-1)));
        //用户id
        if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_ORDER_TYPE)){
            CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
            //健身卡订单
            ipd.setUserId(cardOrder.getUserId());
            //查询当前用户所属门店id
            Long storeId = cardOrderMapper.queryStoreIdByAddress(cardOrder.getStoreAddressId());
            ipd.setStoreId(storeId);
            if (cardOrder.getAutoPay() > 0) {//自动续费
                //支付用途
                //ipd.setPayType(13);
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PRIVATE_CLASS_ORDER_TYPE)){
            //私教课订单
            ipd.setUserId(privateClassOrderMapper.selectPrivateClassOrderByOrder(orderNo).getUserId());
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(ipd.getUserId());
            ipd.setStoreId(storeId);
        }else if(orderNo.substring(orderNo.length()-1).equals(ConfigConstant.TEAM_CLASS_ORDER_TYPE)){
            //团体订单
            ipd.setUserId(teamClassOrderMapper.selectTeamClassOrderByOrderNo(orderNo).getUserId());
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(ipd.getUserId());
            ipd.setStoreId(storeId);
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.GOODS_ORDER_TYPE)){
            //商品订单
            ipd.setUserId(goodsOrderMapper.queryCouponMoneyByOrderNo(orderNo).getUserId());
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(ipd.getUserId());
            ipd.setStoreId(storeId);
        }
        //订单号
        ipd.setOrderNo(orderNo);
        //流动号
        ipd.setTransactionNumber(transaction_id);
        //金额
        ipd.setMoney(wallet);
        //收支时间
        Date date = new Date();
        ipd.setTradeDate(date);
        //收支方式
        ipd.setTradeType(payType);
        //状态
        ipd.setTradeStatus(3);
        //审核通过时间
        //交易完成时间
        ipd.setTransactionTime(date);
        //创建时间
        ipd.setCreatedDate(date);

        int result = incomePayDetailMapper.insertSelective(ipd);
        return result;
    }
}
