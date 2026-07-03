package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitTransfer;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private VipBenefitMapper vipBenefitMapper;
    @Autowired
    private VipBenefitTransferMapper vipBenefitTransferMapper;
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
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_CARD_BUY_TYPE)){
            //VIP权益卡购买订单:按订单号反查 vip_benefit 取 userId,再查所属门店
            VipBenefit vipBenefit = vipBenefitMapper.selectByOrderNo(orderNo);
            if (vipBenefit != null) {
                ipd.setUserId(vipBenefit.getUserId());
                Long storeId = userInfoMapper.queryStoreIdByUserId(vipBenefit.getUserId());
                ipd.setStoreId(storeId);
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_TRANSFER_FEE_TYPE)){
            //VIP转让服务费订单:按服务费订单号反查 vip_benefit_transfer,取转让人 userId/storeId(付费人=转让人)
            VipBenefitTransfer transfer = vipBenefitTransferMapper.selectByFeeOrderNo(orderNo);
            if (transfer != null) {
                ipd.setUserId(transfer.getFromUserId());
                Long storeId = transfer.getFromStoreId() != null
                        ? transfer.getFromStoreId()
                        : userInfoMapper.queryStoreIdByUserId(transfer.getFromUserId());
                ipd.setStoreId(storeId);
            }
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

    @Override
    public int updateAnotherId(String orderNo, Long anotherId) {
        return incomePayDetailMapper.updateAnotherId(orderNo, anotherId);
    }

    @Override
    public int saveTransferRefund(String orderNo, BigDecimal money, Long userId, Long anotherId) {
        IncomePayDetail ipd = new IncomePayDetail();
        ipd.setOrderNo(orderNo);
        // 用途=9退款(与商城退款一致;前台 moneyType CASE 把 9 记'+',退款回款计收入)
        ipd.setPayType(9);
        // 退款金额存正数,正负号由前台展示层决定
        ipd.setMoney(money);
        // 本功能只接微信,收支方式=微信
        ipd.setTradeType(ConfigConstant.WXPAY);
        ipd.setUserId(userId);
        ipd.setStoreId(userInfoMapper.queryStoreIdByUserId(userId));
        // 对方=受让人(留痕)
        ipd.setAnotherId(anotherId);
        ipd.setTradeStatus(3);
        Date now = new Date();
        ipd.setTradeDate(now);
        ipd.setTransactionTime(now);
        ipd.setCreatedDate(now);
        return incomePayDetailMapper.insertSelective(ipd);
    }
}
