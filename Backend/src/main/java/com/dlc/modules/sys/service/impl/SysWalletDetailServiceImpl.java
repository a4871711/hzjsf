package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.IncomePayDetailMapper;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.sys.dao.SysIncomePayDetailDao;
import com.dlc.modules.sys.dao.SysWalletDetailDao;
import com.dlc.modules.sys.entity.SysIncomePayDetailEntity;
import com.dlc.modules.sys.entity.WalletDetailEntity;
import com.dlc.modules.sys.service.SysWalletDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("walletDetailService")
public class SysWalletDetailServiceImpl implements SysWalletDetailService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysWalletDetailDao sysWalletDetailDao;
    @Autowired
    private SysIncomePayDetailDao sysIncomePayDetailDao;

    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;

    /*@Autowired
    private GlWalletDao glWalletDao;*/
//    @Autowired
//    private UserWalletMapper userWalletMapper;

    /*@Autowired
    private GlWalletDetailDao glWalletDetailDao;*/

    @Override
    public WalletDetailEntity queryObject(Long id) {
        return sysWalletDetailDao.queryObject(id);
    }

    @Override
    public List<WalletDetailEntity> queryList(Map<String, Object> map) {
        return sysWalletDetailDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysWalletDetailDao.queryTotal(map);
    }

    @Override
    public void save(WalletDetailEntity walletDetail) {
        sysWalletDetailDao.save(walletDetail);
    }

    @Override
    public void update(WalletDetailEntity walletDetail) {
        sysWalletDetailDao.update(walletDetail);
    }

    @Override
    public void delete(Long id) {
        sysWalletDetailDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysWalletDetailDao.deleteBatch(ids);
    }

    @Override
    public int updateById(WalletDetailEntity walletDetailEntity) {
        return sysWalletDetailDao.updateById(walletDetailEntity);
    }

//    @Override
//    public R payToUser(String openId, Integer money, String orderNo, Long id, Long userId) throws Exception {
//        String result;
//        try {
//            MyConfig config = new MyConfig();
//            String payToUserURL = ConfigConstant.PAY_TO_USER;
//            TreeMap<String, String> parms = new TreeMap<>();
//            parms.put("mch_appid", config.getAppID());//企业公众号appid
//            parms.put("mchid", config.getMchID());//微信支付分配的商户号
//            parms.put("nonce_str", WxPayUtils.getRandomStringByLength(19));//随机字符串，不长于32位
//            parms.put("partner_trade_no", orderNo);//商户订单号，需保持唯一性
//            parms.put("amount", money + "");//企业付款金额，单位为分
//            parms.put("desc", "代理商提现");//企业付款描述信息
//            parms.put("spbill_create_ip", WxPayUtils.getLocalhostIp());//调用接口的机器Ip地址
//            parms.put("openid", openId);//用户openid
//            parms.put("check_name", "NO_CHECK");//NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名,OPTION_CHECK：针对已实名认证的用户才校验真实姓名
//            //	parms.put("re_user_name", "mch_appid");//如果check_name设置为FORCE_CHECK或OPTION_CHECK，则必填用户真实姓名
//            String sign = WxPayUtils.createSign("UTF-8", parms);
//            parms.put("sign", sign);//签名//WxPayUtils.getRequestXml(parms)
//            //带证书的双向请求
//            result = CommonUtil.requestWithCert(payToUserURL, parms, 5000, 5000);
//            log.info("++++++++++++++++++" + result + "++++++++++++++++++");
//        }catch (Exception e){
//            return R.error("企业支付配置有误,请联系管理员");
//        }
//
//
//        //交易结果处理
//        Map map = WxPayUtils.doXMLParse(result);
//        String return_code = map.get("return_code").toString();
//        String result_code = map.get("result_code").toString();
//
//        log.info("result_code&return_code=="+ result_code+"++++"+return_code+"+++++++");
//        WalletDetailEntity walletDetailEntity;
//        if (return_code.equalsIgnoreCase("SUCCESS") && result_code.equalsIgnoreCase("SUCCESS")) {
//
//            log.info("+++++++++提现成功+++++++++++++");
//
//            try {
//                //交易成功后更新提现记录信息
//                // 微信订单号（流水号）
//                String payment_no = map.get("payment_no").toString();
//                //商户订单号(订单号)
//                String partner_trade_no = map.get("partner_trade_no").toString();
//                walletDetailEntity = new WalletDetailEntity();
//                walletDetailEntity.setId(id);
//                walletDetailEntity.setTransactionNumber(payment_no);
//                walletDetailEntity.setStatus(Constant.WalletDetailStatus.CHECK_PAY_SUCCESS.getValue());
//                walletDetailEntity.setTransactionTime(new Date());
//                int res = walletDetailDao.update(walletDetailEntity);
//                if (res > 0) {
//                    log.info("+++++++++++++++更新提现记录成功++++++++++++++");
//                } else {
//                    log.error("--------------更新提现记录失败---------------");
//                }
//            } catch (Exception e) {
//                log.error("--------------更新提现记录失败---------------");
//                e.printStackTrace();
//            }
//            try {
//                //新增个联平台钱包记录
//                GlWalletDetailEntity glWallet = new GlWalletDetailEntity();
//                glWallet.setCreateTime(new Date());
//                glWallet.setMoney(money);
//                //4 :提现
//                glWallet.setType(4);
//                int res = glWalletDetailDao.save(glWallet);
//                if (res > 0) {
//                    log.info("+++++++++++++++新增个联平台钱包记录成功++++++++++++++");
//                } else {
//                    log.error("------------新增个联平台钱包记录失败---------");
//                }
//            } catch (Exception e) {
//                log.error("------------新增个联平台钱包记录失败---------");
//                e.printStackTrace();
//            }
//            return R.reOk();
//        } else {
//            //转账失败
//            String err_code_des = map.get("err_code_des").toString();
//            walletDetailEntity = new WalletDetailEntity();
//            walletDetailEntity.setStatus(Constant.WalletDetailStatus.CHECK_PAY_FAILED.getValue());
//            walletDetailEntity.setId(id);
//            walletDetailEntity.setReason(err_code_des);
//            int update = walletDetailDao.update(walletDetailEntity);
//            if (update > 0) {
//                log.info("++++++++转账失败更新状态成功+++++++++");
//            }
//
//            //转账失败,回复代理商钱包余额
//
//            return R.error(err_code_des);
//        }
//    }

    @Override
    public R updateWalletDetailStatus(WalletDetailEntity walletDetail, int flag) {
        //flag 3:提现成功; 2:提现审核失败
        //Integer status, Long id, String orderNo, Long userId, Integer type
        try {
            if(null == walletDetail){return R.error("操作失败");}
            WalletDetailEntity walletDetailEntity = new WalletDetailEntity();
            walletDetailEntity.setId(walletDetail.getId());
            walletDetailEntity.setStatus(flag);
            Date date = new Date();
            walletDetailEntity.setCheckedTime(date);
            //add by lnx 20180921 根据订单号同时更新你收支明细表的状态
            Integer type = walletDetail.getType();    //type 1:普通用户  2：教练
            if(type == 1){
                IncomePayDetail income = new IncomePayDetail();
                income.setTradeStatus(flag);
                income.setCheckedTime(date);
                income.setOrderNo(walletDetail.getOrderNo());
                incomePayDetailMapper.updateTradeStatus(income);
            }
            //flag =2 审核失败 金额原路退还
            if(flag == 2){
                Map<String, Object> updateMap = new HashMap<String, Object>();
                int res = 0;
                if(type == 1){
                    //退还用户钱包
                    updateMap.put("userId", walletDetail.getUserId());
                    updateMap.put("money",walletDetail.getMoney());
                    res = sysWalletDetailDao.updateUserMoney(updateMap);
                    if(res <= 0){R.error("金额原路返回失败");}
                    //提现审核失败，插入收支明细表
                    SysIncomePayDetailEntity sysIncomePay = new SysIncomePayDetailEntity();
                    sysIncomePay.setUserId(walletDetail.getUserId());
                    sysIncomePay.setOrderNo(walletDetail.getOrderNo());
                    sysIncomePay.setPayType(11);
                    sysIncomePay.setMoney(new BigDecimal(walletDetail.getMoney()));
                    sysIncomePay.setTradeDate(date);
                    sysIncomePay.setTradeType(1);  //钱包
                    sysIncomePay.setTradeStatus(3);  //已完成
                    sysIncomePay.setCheckedTime(date);
                    sysIncomePay.setTransactionTime(date);
                    sysIncomePay.setCreatedDate(date);
                    sysIncomePayDetailDao.insertBack(sysIncomePay);
                }else if(type == 2){
                    updateMap.put("userId", walletDetail.getUserId());
                    updateMap.put("money",walletDetail.getMoney());
                    res = sysWalletDetailDao.updateCoachMoney(updateMap);
                    if(res <= 0){R.error("金额原路返回失败");}
                }
                //add end
            }
            sysWalletDetailDao.update(walletDetailEntity);
        } catch (Exception e) {
            return R.error("操作失败");
        }
        return R.ok();
    }

    @Override
    public int saveWalletDetail(Map<String, Object> params) {
        WalletDetailEntity walletDetailEntity = new WalletDetailEntity();
        walletDetailEntity.setUserId(Long.parseLong(params.get("userId").toString()));
        //明细类型（1.(用户) 提现 2.（教练）提现 3.(用户）充值）4.（用户）门店商品消费',
        walletDetailEntity.setType(4);
        walletDetailEntity.setMoney(Double.valueOf(params.get("totalMoney").toString()));
        walletDetailEntity.setTransactionTime(new Date());
        walletDetailEntity.setCreateTime(new Date());
        walletDetailEntity.setStatus(3);
        walletDetailEntity.setOrderNo(OrderNoGenerator.getOrderIdByTime());
        int res = sysWalletDetailDao.save(walletDetailEntity);
        return res;
    }

}
