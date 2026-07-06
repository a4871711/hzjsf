package com.dlc.modules.api.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CommonUtil;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.*;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.MyConfig;
import com.dlc.modules.qd.utils.WxPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static com.dlc.modules.qd.utils.WxPayUtils.createSign;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/17/017
 */
@Service
@Transactional
public class PayServiceImpl implements PayService {
    private Logger log =  LoggerFactory.getLogger(getClass());
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    @Autowired
    private GoodsOrderMapper goodsOrderMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private PrivateClassService privateClassService;
    @Autowired
    private TeamClassOrderService teamClassOrderService;
    @Autowired
    private GoodsOrderService goodsOrderService;
    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;
    @Autowired
    private TeamClassOrderMapper teamClassOrderMapper;

    /**
     *  @Auther:YD
     *  @parameters:
     *  余额支付
     */
    @Override
    public R blRechargePay(UserInfoVo user, BigDecimal userMoney, String orderNo, Integer payType) {
        //查询用户钱包余额
        UserWallet userWallet = userWalletMapper.selectWalletByUserId(user.getUserId());
        //用户余额
        BigDecimal wallet = userWallet.getMoney();
        //消费的金额
//        BigDecimal userMoney = BigDecimal.valueOf(money).divide(new BigDecimal(100));

        int a = wallet.compareTo(userMoney);
        //判断余额和支付金额大小
        if (userWallet.getMoney() != null && a == -1){
            return R.reError("很抱歉，您的余额不足！");
        }else {
            String transaction_id = null;
            //添加收支明细
            incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,userMoney,payType);
            if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_ORDER_TYPE)){
                //更新健身卡订单
                try {
                    CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
                    if(cardOrder.getRealPayment().compareTo(userMoney) == -1){
                        return R.reError("支付金额异常");
                    }
                    cardOrderService.updateCardOrder(orderNo,userMoney,transaction_id,payType, 0);
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new RRException("请求失败",e);
                }
            }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PRIVATE_CLASS_ORDER_TYPE)){
                //更新私教课订单
                try {
                    PrivateClassOrder pco = privateClassOrderMapper.selectPrivateClassOrderByOrder(orderNo);
                    if(pco.getRealPayment().compareTo(userMoney) == -1){
                        return R.reError("支付金额异常");
                    }
                    privateClassService.updatePrivateClassOrder(orderNo,userMoney,transaction_id,payType);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if(orderNo.substring(orderNo.length()-1).equals(ConfigConstant.TEAM_CLASS_ORDER_TYPE)){
                //更新团体订单
                TeamClassOrder tco = teamClassOrderMapper.selectTeamClassOrderByOrderNo(orderNo);
                if(tco.getRealPayment().compareTo(userMoney) == -1){
                    return R.reError("支付金额异常");
                }
                teamClassOrderService.updateTeamClassOrder(orderNo,userMoney,transaction_id,payType);
            }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.GOODS_ORDER_TYPE)){
                //更新商品订单
                GoodsOrder goodsOrder = goodsOrderMapper.selectGoodOrderByOrder(orderNo);
                if(goodsOrder.getRealPayment().compareTo(userMoney) == -1){
                    return R.reError("支付金额异常");
                }
                goodsOrderService.updateByOrderNo(orderNo, userMoney,transaction_id,payType);
            }
            //更新钱包
            userWallet.setMoney(userWallet.getMoney().subtract(userMoney));
            userWalletMapper.updateByPrimaryKey(userWallet);
            return R.reOk();
        }
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  微信购卡支付
     */
    @Override
    public SortedMap<String, String> wxRechargePay(BigDecimal money, String orderNo, Integer notifyType) throws Exception {
        log.info("统一下单获取预支付id:==========");
        log.info("统一下单money:=========="+money);

        //元转换成分
        Integer wallet = money.multiply(new BigDecimal(100)).intValue();

        try{
            //统一下单获取预支付id
            String prepayId = getPrepayId(wallet,orderNo,notifyType);

            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", config.getAppID());
            packageParams.put("partnerid", config.getMchID());
            packageParams.put("prepayid", prepayId);
            packageParams.put("package", "Sign=WXPay");
            packageParams.put("noncestr", WxPayUtils.getRandomStringByLength(19));
            packageParams.put("timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
            packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
            log.info("<<packageParams>>"+packageParams);
            return packageParams;
        }catch (Exception e){
            log.error("微信支付报错==",e);
        }
        return null;


    }

    public String getPrepayId(Integer wallet, String outTradeNo, Integer notifyType) throws Exception {
//        wallet = 1;
        log.info("微信实际付款金额：========="+wallet+"===========" );
        MyConfig config = new MyConfig();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getAppID());
        packageParams.put("mch_id", config.getMchID());
        packageParams.put("device_info", "WEB");
        packageParams.put("body", "东莞市小盒子数据服务有限公司");
        packageParams.put("out_trade_no", outTradeNo);
        packageParams.put("total_fee", wallet + "");
        packageParams.put("spbill_create_ip", WxPayUtils.getLocalhostIp());
        if(notifyType != null){//充值回调路径
            packageParams.put("notify_url", ConfigConstant.NOTIFY_WX_INMONEY_URL);
            log.info("《《回调地址时》》"+ConfigConstant.NOTIFY_WX_INMONEY_URL+"<<1>>>");
        }else{
            //
            packageParams.put("notify_url", ConfigConstant.NOTIFY_WX_URL);
            log.info("《《回调地址时》》"+ConfigConstant.NOTIFY_WX_URL+"<<2>>>");
        }
        packageParams.put("trade_type", "APP");
        packageParams.put("nonce_str", WxPayUtils.getRandomStringByLength(19));
        String sign = WxPayUtils.createSign("UTF-8", packageParams);
        packageParams.put("sign", sign);
        String result = CommonUtil.httpsRequest(MyConfig.UNIFIED_ORDER_URL, "POST", WxPayUtils.getRequestXml(packageParams));
        return getXMLPayKey(result, "prepay_id");
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  支付宝购卡
     */
    @Override
    public SortedMap<String, String> zfbRechargePay(String orderNo, BigDecimal money, String subject, Integer notifyType) {
        //money=1;
        log.info("支付宝实际付款金额：========="+money+"===========" );
        SortedMap<String, String> map = new TreeMap<>();
        //String outTradeNo = DateUtils.format(new Date(), "yyyyMMddHHmmss");
        AlipayClient alipayClient = new DefaultAlipayClient(MyConfig.ZFB_GATEWAY, MyConfig.ZFB_APPID, MyConfig.ZFB_PRIVATE_KEY, MyConfig.ZFB_OBJECT, MyConfig.ZFB_CHARSET, MyConfig.ZFB_PUBLIC_KEY, MyConfig.ZFB_SIGN_TYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("东莞市小盒子数据服务有限公司");
        model.setSubject(subject);
        model.setOutTradeNo(orderNo);
        model.setTimeoutExpress("30m");
        model.setTotalAmount(String.valueOf(money));
        request.setBizModel(model);
        if(notifyType != null){
            request.setNotifyUrl(MyConfig.ZFB_INMONEY_NOTIFYURL);
        }else{
            request.setNotifyUrl(MyConfig.ZFB_NOTIFYURL);
        }

        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            map.put("data", response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            log.error("支付宝支付报错==",e);
        }
        return map;
    }
    //微信退款
    @Override
    public String wxRefund(Map<String, Object> params) throws Exception {
        log.debug("+++++++++进入微信退款方法+++++++++");
        String res = null;
        try {
            BigDecimal reMoney = (BigDecimal) params.get("realPayment");
            Integer money = reMoney.multiply(new BigDecimal(100)).intValue();
            // 第15步私教退款扩展(可选参数,不传保持原行为):
            // totalFee=原单实收(部分退款时 total_fee 须为原单金额而非退款额);
            // refundNo=退款单号(支持一单多次部分退款,微信同 out_refund_no 只退一笔);
            // refundDesc=退款原因(透传后台备注)
            BigDecimal totalMoney = params.get("totalFee") != null ? (BigDecimal) params.get("totalFee") : reMoney;
            Integer total = totalMoney.multiply(new BigDecimal(100)).intValue();
            String outRefundNo = params.get("refundNo") != null
                    ? (String) params.get("refundNo") : (String) params.get("orderNo");
            String refundDesc = params.get("refundDesc") != null
                    ? (String) params.get("refundDesc") : "订单退款";
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<String, String>();
            packageParams.put("appid",config.getAppID());
            packageParams.put("mch_id",config.getMchID());
            packageParams.put("nonce_str", WxPayUtils.getRandomStringByLength(19));
            packageParams.put("out_trade_no", (String) params.get("orderNo"));
            packageParams.put("out_refund_no", outRefundNo);
            packageParams.put("total_fee",  total +"");
            packageParams.put("refund_fee", money +"");
            packageParams.put("refund_desc", refundDesc);
            //packageParams.put("notify_url", ConfigConstant.REFOUND_WXNOTIFY_URL);
            String sign = createSign("UTF-8", packageParams);
            packageParams.put("sign", sign);
            String result = CommonUtil.requestWithCert(MyConfig.REFUND_URL,packageParams ,5000,5000);
            log.info(result+"+++++++++++++++");
            Map<String, String> map = WxPayUtils.doXMLParse(result);
            if(map.get("return_code").equals("SUCCESS") && map.get("result_code").equals("SUCCESS")){
                res = "退款成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    //根据订单编号查询订单信息
    @Override
    public Map<String, Object> queryOrderInfoOrderNo(String orderNo) {
        return goodsOrderMapper.queryGoodsOrderByOrder(orderNo);
    }

    /**
     * 微信退款回调处理方法
     * @param orderNo
     * @param params
     * @param payType
     * @return
     */
    @Override
    public int updateOrderStatus(String orderNo, Map<String, Object> params, int payType) {
        int res = 0;
        //根据订单编号设置已退款
        Map<String, Object> updateMap = new HashMap<String, Object>();
        Long userId = (Long) params.get("userId");
        BigDecimal money = (BigDecimal) params.get("realPayment");
        updateMap.put("userId", userId);
        updateMap.put("orderNo", orderNo);
        updateMap.put("status", (byte)4);  //已取消
        res = goodsOrderMapper.updateOrderStatus(updateMap);
        if(res > 0){
            Date date = new Date();
            //收支明细表
            IncomePayDetail inCome = new IncomePayDetail();
            //用户id
            inCome.setUserId(userId);
            //订单号
            inCome.setOrderNo(orderNo);
            //流动号
            //inCome.setTransactionNumber(transaction_id);
            //支付用途 (商城退款)
            inCome.setPayType(9);
            //金额
            inCome.setMoney(money);
            //收支时间
            inCome.setTradeDate(date);
            //收支方式
            inCome.setTradeType(payType);
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
        }
        return res;
    }

    @Override
    public String zfbRefund(Map<String, Object> params) {
        log.info("zfbRefund支付宝退款start...");
        String str = null;
        try {
            if(params == null){
                return str;
            }
            AlipayClient alipayClient = new DefaultAlipayClient(MyConfig.ZFB_GATEWAY, MyConfig.ZFB_APPID, MyConfig.ZFB_PRIVATE_KEY, MyConfig.ZFB_OBJECT, MyConfig.ZFB_CHARSET, MyConfig.ZFB_PUBLIC_KEY, MyConfig.ZFB_SIGN_TYPE);
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo((String) params.get("orderNo"));
            model.setTradeNo((String) params.get("transactionNo"));
            model.setRefundAmount(params.get("realPayment")+"");
            model.setRefundReason("商城取消订单退款");
            request.setBizModel(model);
            //request.setNotifyUrl(ConfigConstant.REFOUND_ZFBNOTIFY_URL);
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                str = "支付宝退款成功";
            }else{
                //str = response.getSubMsg();
                log.info("退款异常"+response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.info("支付宝退款异常{}"+e);
            e.printStackTrace();
        }
        return str;
    }
    /**
     * 微信支付
     * */
    public SortedMap<String, String> wxPay(String outOrderNo,BigDecimal money,String openId,String notify_url) throws Exception{
        //元转换成分
        Integer wallet = money.multiply(new BigDecimal(100)).intValue();
        //log.info("微信支付回调地址设置："+notify_url+" ");
        //统一下单,获得预支付Id
        String prepayId = unifiledOrder(outOrderNo, wallet,openId,notify_url);
        MyConfig config = new MyConfig();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appId", config.getGzhAppID());
        packageParams.put("timeStamp",String.valueOf(Calendar.getInstance().getTimeInMillis()/1000));
        packageParams.put("nonceStr", WxPayUtils.getRandomStringByLength(19));
        packageParams.put("package", "prepay_id="+prepayId);
        packageParams.put("signType", "MD5");
        packageParams.put("paySign", WxPayUtils.createSign("UTF-8", packageParams));
        log.info("packageParams:"+packageParams);
        return packageParams;
    }
    // 统一下单
    public String unifiledOrder(String outTradeNo, Integer wallet,String openid,String notify_url) throws Exception {
        MyConfig config = new MyConfig();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getGzhAppID());
        packageParams.put("mch_id", config.getMchID());
        packageParams.put("device_info", "WEB");
        packageParams.put("body", "矢历运动");
        packageParams.put("out_trade_no", outTradeNo);
        packageParams.put("total_fee", wallet + "");
        packageParams.put("fee_type", "CNY");
        packageParams.put("spbill_create_ip", WxPayUtils.getLocalhostIp());
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", "JSAPI");
        packageParams.put("nonce_str", WxPayUtils.getRandomStringByLength(19));
        packageParams.put("sign_type","MD5");
        packageParams.put("openid", openid);
        String sign = WxPayUtils.createSign("UTF-8", packageParams);
        packageParams.put("sign", sign);
        String result = CommonUtil.httpsRequest(MyConfig.UNIFIED_ORDER_URL, "POST", WxPayUtils.getRequestXml(packageParams));
        return getXMLPayKey(result, "prepay_id");
    }
    //解析
    public static String getXMLPayKey(String result, String key) throws Exception {
        Map<String, String> map = WxPayUtils.doXMLParse(result);
        String returnCode = map.get("return_code");
        String resultCode = map.get("result_code");
        if (returnCode.equalsIgnoreCase("SUCCESS") && resultCode.equalsIgnoreCase("SUCCESS")) {
            String prepayId = map.get(key);
            if (map.containsKey("code_url")) {
                prepayId = map.get("code_url");
            }
            return prepayId;
        } else {
            return null;
        }
    }
}
