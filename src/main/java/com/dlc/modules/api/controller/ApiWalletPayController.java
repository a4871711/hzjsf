package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.WalletService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.MyConfig;
import com.dlc.modules.qd.utils.WxPayUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 钱包充值和提现
 * @author lnx
 * @Date 2018-09-21
 */
@RestController
@RequestMapping("/api/walletPay")
public class ApiWalletPayController extends BaseController {

    private Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private WalletService walletService;

    @Autowired
    private PayService payService;

    /**
     * 提现（线下提现）
     *
     * @param request
     * @return
     */
    @RequestMapping("/outMoney")
    public R outMoney(@RequestParam Map<String, Object> dateMap, HttpServletRequest request) {
        return walletService.outMoney(dateMap);

    }

    /**
     * 钱包充值
     * @param request
     * @return
     */
    @RequestMapping("/inMoney")
    public R inMoney(Integer payType, String openId, BigDecimal money, HttpServletRequest request) {
        SortedMap<String,String> map = new TreeMap<>();
        UserInfoVo user = getUserVo(request);
        String orderNo = OrderNoGenerator.getOrderIdByTime();
        if (user == null){
            return R.reError("用户未登入！");
        }
        if (StringUtils.isBlank(money+"")){
            return  R.error("金额不能为空！");
        }
        Integer notifyType = 0;
//        //元转换成分
//        Integer wallet = money.multiply(new BigDecimal(100)).intValue();
        if (payType == ConfigConstant.WXPAY){
            try {
                map = payService.wxRechargePay(money,orderNo,notifyType);
                //保存钱包详情表
                walletService.inMoneyBefore(user.getUserId(),money,orderNo);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RRException("微信支付失败",e);
            }
        }else if (payType == ConfigConstant.ZFBPAY){
            String subject = "钱包充值支付";
            map = payService.zfbRechargePay(orderNo,money, subject, notifyType);
            //保存钱包详情表
            walletService.inMoneyBefore(user.getUserId(),money,orderNo);
            return R.reOk(map.get("data"));
        }
        return R.reOk(map);
    }
    /**
     *  充值微信支付回调
     */
    @RequestMapping("/wxNotify")
    public void rechargeCallBack(HttpServletRequest request, HttpServletResponse response){
        //UserInfoVo user = getUserVo(request);
        log.info("回调充值返回结果...");
        InputStream inStream;
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> resultMap = WxPayUtils.doXMLParse(result);
            log.info("进入微信回调" + resultMap);
            if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
                log.info("<<微信回调返回码return_code...");
                //充值金额
                BigDecimal wallet = BigDecimal.valueOf(Integer.parseInt(resultMap.get("total_fee"))).divide(new BigDecimal(100));
                //Double wallet = Double.parseDouble(resultMap.get("total_fee"));
                log.info("-------回调的充值金额------:" + wallet);
                //流水号/订单号？
                String transaction_id = resultMap.get("transaction_id");
                log.info("-------回调的充值单号------:" + transaction_id);
                String orderNo = resultMap.get("out_trade_no");
                log.info("-------回调的商户单号------:"+ orderNo);
                //更新订单
                int payType = ConfigConstant.WXPAY;
                //更新钱包余额 添加收支明细
                //incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,user,wallet,payType);
                Map<String, Object> dataMap = new HashMap<String, Object>();
                //dataMap.put("userId", user.getUserId());
                dataMap.put("money", wallet);
                dataMap.put("orderNo", orderNo);
                dataMap.put("transaction_id", transaction_id);
                dataMap.put("payType", payType);
                walletService.inMoney(dataMap);
                log.info(">>微信回调成功");
                response.getWriter().print("SUCCESS");
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.info(">>回调通知异常");
        }
    }
    /**
     *  支付宝回调
     */
    @RequestMapping(value = "/zfbNotify", method = RequestMethod.POST)
    public void zfbNotify(HttpServletRequest request, HttpServletResponse response){
        //UserInfoVo user = getUserVo(request);
        log.info(">>进入支付宝回调zfbNotify");
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info(">>支付宝回调-->"+params);
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, MyConfig.ZFB_PUBLIC_KEY, MyConfig.ZFB_CHARSET, MyConfig.ZFB_SIGN_TYPE);
            if(flag){
                if(params.get("trade_status").equals("TRADE_SUCCESS")){
                    log.info("<<支付宝回调交易状态TRADE_SUCCESS");
                    //商户订单号
                    String orderNo = params.get("out_trade_no");
                    //支付宝流水号
                    String transaction_id = params.get("trade_no");
                    //金额
                    BigDecimal wallet = new BigDecimal(params.get("total_amount"));
                    //Double wallet = Double.parseDouble(params.get("total_amount"));
                    //支付类型
                    int payType = ConfigConstant.ZFBPAY;
                    //更新钱包余额 添加收支明细
                    //incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,user,wallet,payType);
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    //dataMap.put("userId", user.getUserId());
                    //dataMap.put("openId", user.getOpenId());
                    dataMap.put("money", wallet);
                    dataMap.put("orderNo", orderNo);
                    dataMap.put("transaction_id", transaction_id);
                    dataMap.put("payType", payType);
                    walletService.inMoney(dataMap);
                    log.info("加入返回值-response->>支付宝回调成功");
                }
                response.getWriter().print("success");
                return ;
            }
        }catch (Exception e){
            log.error("支付宝回调报错==",e);
        }
    }
    /**
     * 微信退款
     *
     * @param orderNo
     * @return
     */
    @RequestMapping("/wxOrderRefund")
    public R wxOrderRefund(String orderNo) throws Exception {
        if (StringUtils.isBlank(orderNo)) {
            return R.reError("缺少参数-订单号");
        }
        Map<String,Object> params = payService.queryOrderInfoOrderNo(orderNo);
        String res = payService.wxRefund(params);
       // Map<String, String> map = WxPayUtils.doXMLParse(res);
        if (res == null) {
            return R.reError("退款失败,请联系商家");
        } else {
            return R.reOk();
        }
    }

    /**
     * 微信退款回调（暂时不需要回调）
     *
     * @return
     */
    @RequestMapping(value = "/wxRefoundNotify***", method = RequestMethod.POST)
    public void wxRefoundNotify(HttpServletRequest request, HttpServletResponse response) {
        log.info(">>进入微信退款回调");
        InputStream inStream;
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> map = WxPayUtils.doXMLParse(result);

            log.info(">>进入微信退款回调" + map);
            if (map.get("return_code").equals("SUCCESS")) {
                //更行订单数据
                //回调成功
                log.info(">>退款回调成功执行操作!");
                String req_info = map.get("req_info");  //加密信息
                String reqInfo = WxPayUtils.decryptData(req_info);
                log.info("base64>>" + reqInfo);
                Map<String, String> reInfoMap = WXPayUtil.xmlToMap(reqInfo);
                log.info(">> reInfoMap" + reInfoMap);
                // 转换成map
                log.info("reInfoMap=" + JSON.toJSONString(reInfoMap));
                // OrderEntity orderEntity = orderService.queryOrderByOutTradeNo(reInfoMap.get("out_trade_no"));
                String orderNo = reInfoMap.get("out_trade_no");
                Map<String,Object> params = payService.queryOrderInfoOrderNo(orderNo);
                if (params != null) {
                    int payType = ConfigConstant.WXPAY;
                    //更新订单状态 设为已退款
                    //状态:0 待付款，1 待发货，2 待收货，3已完成，4已取消 5已评价 6已退款
                    int res = payService.updateOrderStatus(orderNo, params, payType);
                    if (res > 0) {
                        log.info("++++++++++++退款更改状态成功++++++++++++");
                    } else {
                        log.error("------------退款更改状态失败-----------");
                    }
                }
                log.info(">>微信退款回调完成");
                response.getWriter().print("SUCCESS");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(">>退款回调通知异常");

        }
    }
    /**
     * 支付宝退款
     *
     * @param orderNo
     * @return
     */
    @RequestMapping("/zfbOrderRefund")
    public R orderRefund(String orderNo) throws Exception {
        if (StringUtils.isBlank(orderNo)) {
            return R.reError("缺少参数-订单号");
        }
        Map<String,Object> params = payService.queryOrderInfoOrderNo(orderNo);
        String res = payService.zfbRefund(params);
        //Map<String, String> map = ZfbPayUtils.doXMLParse(res);
        if (res == null) {
            return R.reError("退款失败");
        } else {
            return R.reOk(res);
        }
    }
    /**
     *  支付宝回调（暂时不需要回调）
     */
    @RequestMapping(value = "/zfbRefoundNotify***", method = RequestMethod.POST)
    public void zfbRefoundNotify(HttpServletRequest request, HttpServletResponse response){
        //UserInfoVo user = getUserVo(request);
        log.info(">>进入支付宝退款回调zfbNotify");
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info(">>支付宝退款回调-->"+params);
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, MyConfig.ZFB_PUBLIC_KEY, MyConfig.ZFB_CHARSET, MyConfig.ZFB_SIGN_TYPE);
            if(flag){
                if(params.get("trade_status").equals("TRADE_SUCCESS")){
                    log.info("<<支付宝退款回调交易状态TRADE_SUCCESS");
                    //商户订单号
                    String orderNo = params.get("out_trade_no");
                    //支付宝流水号
                    String transaction_id = params.get("trade_no");
                    //退款金额
                    BigDecimal wallet = new BigDecimal(params.get("refund_amount"));
                    //Double wallet = Double.parseDouble(params.get("total_amount"));
                    //支付类型
                    int payType = ConfigConstant.ZFBPAY;
                    //更新钱包余额 添加收支明细
                    Map<String,Object> upParams = payService.queryOrderInfoOrderNo(orderNo);
                    if (params != null) {
                        //更新订单状态 设为已退款
                        //状态:0 待付款，1 待发货，2 待收货，3已完成，4已取消 5已评价 6已退款
                        int res = payService.updateOrderStatus(orderNo, upParams, payType);
                        if (res > 0) {
                            log.info("++++++++++++退款更改状态成功++++++++++++");
                        } else {
                            log.error("------------退款更改状态失败-----------");
                        }
                    }
                    log.info("加入返回值-response->>支付宝回调成功");
                }
                response.getWriter().print("success");
                return ;
            }
        }catch (Exception e){
            log.error("支付宝退款回调报错==",e);
        }
    }
}
