/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ConsumerRedis
 * Author:   Administrator
 * Date:     2019/4/27 11:01
 * Description: redis队列消费者
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.IncomePayDetailMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.UserWalletMapper;
import com.dlc.modules.api.dao.WalletDetailDao;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.UserWallet;
import com.dlc.modules.api.entity.WalletDetailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPubSub;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chenyuexin
 * @date 2019-04-27 11:01
 * @version 1.0
 */
@Service
public class ConsumerRedis extends JedisPubSub {
    final Logger logger = LoggerFactory.getLogger(ConsumerRedis.class);
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private WalletDetailDao walletDetailDao;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    public ConsumerRedis(UserWalletMapper userWalletMapper, WalletDetailDao walletDetailDao, UserInfoMapper userInfoMapper,
                         IncomePayDetailMapper incomePayDetailMapper){
        this.userWalletMapper = userWalletMapper;
        this.walletDetailDao = walletDetailDao;
        this.userInfoMapper = userInfoMapper;
        this.incomePayDetailMapper = incomePayDetailMapper;
    }
    /*
     * channel频道接收到新消息后，执行的逻辑
     */
    @Override
    @Transactional
    public void onMessage(String channel, String message) {
        // 执行逻辑
        logger.info(channel + "频道发来消息：" + message);
        logger.info("userWalletMapper-----"+userWalletMapper);
        logger.info("walletDetailDao-----"+walletDetailDao);
        logger.info("userInfoMapper-----"+userInfoMapper);
        logger.info("incomePayDetailMapper-----"+incomePayDetailMapper);
        JSONObject json = JSONObject.parseObject(message);
        try {

            Message temp = JSONObject.toJavaObject(json, Message.class);
            if(null != temp){
                Long userId = temp.getUserId();
                BigDecimal money = temp.getMoney();
                String alipay = temp.getAliAccount();
                UserWallet uw = userWalletMapper.selectWalletByUserId(userId);
                //BigDecimal myMoney = userWalletMapper.queryWalletMoney(userId);
                BigDecimal myMoney = uw.getMoney();
                if(money.compareTo(new BigDecimal("0")) ==1 && myMoney.compareTo(money) != -1){
                    try {
                        logger.info("用户金额大于或者等于提现金额------");
                        //将金额变负数做减法
                        BigDecimal upMoney = money.multiply(new BigDecimal(-1));
                        userWalletMapper.updateMyMoney(userId, upMoney);
                        //钱包明细表
                        WalletDetailEntity walletDetailEntity = new WalletDetailEntity();
                        String orderNo = OrderNoGenerator.getOrderIdByTime();
                        walletDetailEntity.setUserId(userId);
                        walletDetailEntity.setType(1); //提现
                        walletDetailEntity.setMoney(money);
                        walletDetailEntity.setOrderNo(orderNo);
                        walletDetailEntity.setStatus(1); //审核中....
                        walletDetailEntity.setAliAccount(alipay);
                        walletDetailEntity.setRealName(uw.getRealName());
                        walletDetailEntity.setCreateTime(new Date());
                        walletDetailDao.save(walletDetailEntity);

                        IncomePayDetail inCome = new IncomePayDetail();
                        inCome.setUserId(userId);
                        inCome.setOrderNo(orderNo);
                        inCome.setPayType(1);
                        inCome.setMoney(money);
                        inCome.setTradeStatus(0);
                        inCome.setTradeDate(new Date());//交易时间
                        inCome.setPayType(1);    //支付用途 1提现
                        inCome.setTradeStatus(1); //审核中
                        //查询当前用户所属门店id
                        Long storeId = userInfoMapper.queryStoreIdByUserId(userId);
                        inCome.setStoreId(storeId);
                        incomePayDetailMapper.insertSelective(inCome);
                    } catch (Exception e) {
                        logger.info("提现失败outMoney()"+e);
                        e.printStackTrace();
                        throw new RRException("提现异常");
                    }
                }
            }
        }catch (Exception e){
            logger.info("onMessage-------->",e);
        }
        // 如果消息为 close channel， 则取消此频道的订阅
        if("close channel".equals(message)){
            this.unsubscribe(channel);
        }
    }

    /*
     * channel频道有新的订阅者时执行的逻辑
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info(channel + "频道新增了"+ subscribedChannels +"个订阅者");
    }

    /*
     * channel频道有订阅者退订时执行的逻辑
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.info(channel + "频道退订成功");
    }
}
