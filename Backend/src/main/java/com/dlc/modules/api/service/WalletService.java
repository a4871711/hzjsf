package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author lnx
 * @Date 2018-09-17
 */
public interface WalletService {
    /**
     * 查询个人钱包
     * @param userId
     * @return
     */
    Map<String, Object> queryMyWallet(Long userId);

    /**
     * 查询我的交易明细
     * @param params
     * @return
     */
    PageUtils queryMyTradeDetail(Map<String, Object> params);

    /**
     * 查询我的优惠券
     * @param params
     * @return
     */
    PageUtils queryMyCoupon(Map<String, Object> params);

    /**
     * 提现设置
     * @param updateMap
     * @return
     */
    R setAliAcccount(Map<String, Object> updateMap);

    /**
     * 查询我的提现设置
     * @param userId
     * @return
     */
    Map<String, Object> queryWalletSetting(Long userId);

    /**
     * 提现
     * @param dateMap
     * @return
     */
    R outMoney(Map<String, Object> dateMap);

    /**
     * 充值
     * @param dataMap
     * @return
     */
    int inMoney(Map<String, Object> dataMap);
    //充值前保存充值交易订单信息
    int inMoneyBefore(Long userId, BigDecimal money, String orderNo);
}
