package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.constant.Constants;
import com.dlc.common.utils.*;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.WalletDetailEntity;
import com.dlc.modules.api.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {
    private static Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private WalletDetailDao walletDetailDao;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Map<String, Object> queryMyWallet(Long userId) {
        Map<String, Object> queryMap = userWalletMapper.queryWalletByUserId(userId);
        if(queryMap == null){
            return new HashMap<String, Object>();
        }
        return queryMap;
    }

    @Override
    public PageUtils queryMyTradeDetail(Map<String, Object> params) {
        Query query =new Query(params);
        List<Map<String,Object>> list = incomePayDetailMapper.queryTradeDetailByUserId(query);
        int total = incomePayDetailMapper.queryTradeDetailCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public PageUtils queryMyCoupon(Map<String, Object> params) {
        Query query =new Query(params);
        List<Map<String,Object>> list = couponMapper.queryCouponByUserId(query);
        int total = couponMapper.queryCouponCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public R setAliAcccount(Map<String, Object> updateMap) {
        if(updateMap == null){
            return R.reError("参数未配置");
        }
        if(updateMap.get("userId") == null || updateMap.get("alipay") == null || updateMap.get("realName") == null){
            return R.reError("缺少必要信息");
        }
        int res = userWalletMapper.updateAccountByUserId(updateMap);
        if(res <= 0){
            return R.reError("设置失败");
        }
        return R.reOk();
    }

    @Override
    public Map<String, Object> queryWalletSetting(Long userId) {
        return userWalletMapper.queryWalletAccount(userId);
    }

    @Override
    public R outMoney(Map<String, Object> dateMap) {
        log.info("outMoney start...提现");
        if(dateMap == null){
            return R.reError("参数未配置");
        }
        log.info("dateMap---->"+dateMap.toString());
        if(dateMap.get("userId") == null || dateMap.get("aliAccount") == null){
            return R.reError("提现信息缺失");
        }
        BigDecimal money = BigDecimal.valueOf(Double.parseDouble((String) dateMap.get("money")));  //金额为负数
        //Double Moneys = Double.parseDouble((String) dateMap.get("money"));
        BigDecimal leastMoney = BigDecimal.valueOf(Double.parseDouble((String) dateMap.get("leastMoney")));

        if(money.compareTo(leastMoney) == -1 ){
            return R.reError("提现金额不得少于最低限额");
        }
        //支付接口将提现金额扣除走线下审核
        //aliPay()

        //根据提现状态更新 钱包表 和 收支明细表
        Long userId = Long.parseLong((String) dateMap.get("userId"));
        //Integer money = (Integer) dateMap.get("money");  //金额为负数
        //Integer money = (Integer) dateMap.get("money");  //金额为负数
        String alipay = (String) dateMap.get("aliAccount");
        //查询钱包实时金额（防止金额不够扣除）
        BigDecimal myMoney = userWalletMapper.queryWalletMoney(userId);
        if(money.compareTo(new BigDecimal("0")) == -1 || myMoney.compareTo(money) == -1){
            return R.reError("提现金额超出账户余额");
        }
        Message temp = new Message();
        temp.setUserId(userId);
        temp.setLeastMoney(leastMoney);
        temp.setMoney(money);
        temp.setAliAccount(alipay);
        System.out.println("提现A=" + Constants.CHANNEL);
        System.out.println("提现B=" + JSONObject.toJSONString(temp));
        JedisUtil.publishMessage(Constants.CHANNEL, JSONObject.toJSONString(temp));
       /* try {
            //奖金额变负数做减法
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
            log.info("提现失败outMoney()"+e);
            e.printStackTrace();
        }*/
        return R.reOk();
    }

    @Override
    public int inMoney(Map<String, Object> dataMap) {
        //支付接口回调成功后处理业务表数据
        int res = 0;
        //返回订单号
        //根据提现状态更新 钱包表 和 收支明细表
        String openId = (String) dataMap.get("openId");
        //BigDecimal money = BigDecimal.valueOf(Integer.parseInt((String) dataMap.get("money")));
        BigDecimal money = (BigDecimal) dataMap.get("money");
        String orderNo = (String) dataMap.get("orderNo");
        String transaction_id = (String) dataMap.get("transaction_id");
        int payType = (int) dataMap.get("payType");
        try {
            //根据订单编号查询用户id
            Long userId = walletDetailDao.queryUidByOrderNo(orderNo);
            if(null == userId){
                log.info("订单信息异常！没有找到该订单所属用户");
                return 0;
            }
            res = userWalletMapper.updateMyMoney(userId, money);

            Date date = new Date();
            //钱包明细表
            Map<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put("transactionNumber", transaction_id);
            updateMap.put("orderNo", orderNo);
            updateMap.put("money", money);
            walletDetailDao.updateDetailByOrderNo(updateMap);

            IncomePayDetail inCome = new IncomePayDetail();
            //用户id
            inCome.setUserId(userId);
            //订单号
            inCome.setOrderNo(orderNo);
            //流动号
            inCome.setTransactionNumber(transaction_id);
            //支付用途 (充值)
            inCome.setPayType(6);
            //金额
            inCome.setMoney(money);
            //收支时间
            inCome.setTradeDate(date);
            //收支方式
            inCome.setTradeType(payType);
            //openId
            inCome.setOpenId(openId);
            //状态(已完成)
            inCome.setTradeStatus(3);
            //审核通过时间
            //交易完成时间
            inCome.setTransactionTime(date);
            //创建时间
            inCome.setCreatedDate(date);
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(userId);
            inCome.setStoreId(storeId);
            incomePayDetailMapper.insertSelective(inCome);
        } catch (Exception e) {
            log.info("充值回调异常inMoney{}"+e);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public int inMoneyBefore(Long userId, BigDecimal money, String orderNo) {
        int res = 0;
        try {
            //钱包明细表
            WalletDetailEntity walletDetailEntity = new WalletDetailEntity();
            walletDetailEntity.setUserId(userId);
            walletDetailEntity.setType(3); //充值
            walletDetailEntity.setMoney(money);
            walletDetailEntity.setOrderNo(orderNo);
            walletDetailEntity.setStatus(0); //充值中....
            walletDetailEntity.setCreateTime(new Date());
            res = walletDetailDao.save(walletDetailEntity);
        } catch (Exception e) {
            log.info("异常 inMoneyBefore{}"+e);
            e.printStackTrace();
        }
        return res;
    }
}
