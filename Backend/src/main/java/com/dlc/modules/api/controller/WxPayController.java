package com.dlc.modules.api.controller;

import com.alibaba.druid.sql.visitor.functions.Locate;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.R;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.dao.CardOrderMapper;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.*;
import com.dlc.modules.api.vo.PapPayApplyVo;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.MyConfig;
import com.dlc.modules.qd.utils.WxPayUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author 0316
 * @version 1.0
 * @date 2018-09-15 11:06
 */

@RestController
@RequestMapping("/api/wx")
public class WxPayController extends BaseController {
    private Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private VipBenefitService vipBenefitService;
    @Autowired
    private com.dlc.modules.api.service.VipTransferService vipTransferService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CardOrderMapper cardOrderMapper;

    /**
     *  @Auther:
     *  @parameters:
     *  委托代扣解约回调
     */
    @RequestMapping(value = "/cancelContract")
    public void rechargeCallBack(HttpServletRequest request, HttpServletResponse response){
        log.info("进入委托代扣解约回调=================================");
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
            response.setCharacterEncoding("UTF-8");
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> resultMap = WxPayUtils.doXMLParse(result);
            log.info("进入委托代扣解约回调{}", resultMap);
            if (resultMap == null) {
                response.getWriter().print(setWechatXml("FAIL", "参数格式校验错误"));
                return;
            }
            MyConfig config = new MyConfig();
            if (!WXPayUtil.isSignatureValid(resultMap, config.getKey())) { //API密钥
                //签名验证失败
                response.getWriter().print(setWechatXml("FAIL", "签名失败"));
            }
            if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
                log.info(">>解约回调成功");
                wxPayService.updateTrustNotify(resultMap);
            }//解约失败不做处理
            response.getWriter().print(setWechatXml("SUCCESS", "OK"));
            return;
        }catch (Exception e) {
            log.info(">>回调通知异常{}",e);
            try {
                response.getWriter().print(setWechatXml("FAIL", "参数格式校验错误"));
                return;
            } catch (IOException e1) {

            }
        }
    }
    /**
     *  用于接收签约成功消息的回调通知地址
     */
    @RequestMapping(value = "/preenTrustNotify")
    public void preenTrustNotify(HttpServletRequest request, HttpServletResponse response){
        //log.info("接收APP用户签约成功消息的回调通知{}",id);
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
            response.setCharacterEncoding("UTF-8");
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> resultMap = WxPayUtils.doXMLParse(result);
            log.info("签约成功消息回调{}", resultMap);
            if (resultMap == null) {
                response.getWriter().print(setWechatXml("FAIL", "参数格式校验错误"));
                return;
            }
            MyConfig config = new MyConfig();
            if (!WXPayUtil.isSignatureValid(resultMap, config.getKey())) { //API密钥
                //签名验证失败
                response.getWriter().print(setWechatXml("FAIL", "签名失败"));
            }
            if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
                log.info(">>微信签约回调成功");
                resultMap.put("orderNo", resultMap.get("contract_code"));
                wxPayService.updateTrustNotify(resultMap);
                //签约成功-查询有没有代扣款（未支付）的卡订单--并发起支付
                wxPayService.firstPapPayment(resultMap);
            }//签约失败不做处理
            response.getWriter().print(setWechatXml("SUCCESS", "OK"));
            return;
        }catch (Exception e) {
            log.info(">>签约回调通知异常{}",e);
            try {
                response.getWriter().print(setWechatXml("FAIL", "参数格式校验错误"));
                return;
            } catch (IOException e1) {

            }
        }
    }

    /**
     *  @Auther:
     *  @parameters:
     *  用于接收扣款结果消息的回调通知地址
     */
    @RequestMapping(value = "/papPaymentNotify")
    public void papPaymentNotify(HttpServletRequest request, HttpServletResponse response){
        log.info("接收到代扣扣款结果消息的回调通知");
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
            response.setCharacterEncoding("UTF-8");
            String result = new String(outSteam.toByteArray(), "utf-8");
            log.info("代扣扣款消息result回调{}", result);
            Map<String, String> resultMap = WxPayUtils.doXMLParse(result);
            log.info("代扣扣款消息回调{}", resultMap);
            if (resultMap == null) {
                response.getWriter().print(setWechatXml("FAIL", "参数格式校验错误"));
                return;
            }
            MyConfig config = new MyConfig();
            if (!WXPayUtil.isSignatureValid(resultMap, config.getKey())) { //API密钥
                //签名验证失败
                response.getWriter().print(setWechatXml("FAIL", "签名失败"));
            }
            if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
                log.info(">>微信代扣扣款回调成功");
                //更新健身卡订单
                log.info("-------微信代扣更新健身卡订单=========" );
                String orderNo = resultMap.get("out_trade_no");
                BigDecimal wallet = new BigDecimal(resultMap.get("total_fee")).divide(new BigDecimal(100));
                String transaction_id = resultMap.get("transaction_id");
                int res = cardOrderService.updateCardOrder(orderNo, wallet, transaction_id, ConfigConstant.WXAUTOPAY, null);
                if (res > 0) {
                    log.info("=========添加自动续费收支明细==========");
                    incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,wallet,ConfigConstant.WXAUTOPAY);
                }
            }else{
                //这一块好像不对，能走到这里的好像都是一些成功签约，但是卡余额不足之类的原因扣费失败。待核查后删除
//                String contractId = resultMap.get("contract_id");
//                Long userId = userInfoService.getUserIdByContractId(contractId);
//                log.info("-------微信代扣失败，更新用户签约状态，contract_id=" + contractId + "&userId=" + userId );
//                userInfoService.doCancelContract(userId);
            }
            response.getWriter().print(setWechatXml("SUCCESS", "OK"));
            return;
        }catch (Exception e) {
            log.info(">>代扣扣款回调通知异常{}",e);
            try {
                response.getWriter().print(setWechatXml("FAIL", "参数格式校验错误"));
                return;
            } catch (IOException e1) {

            }
        }
    }

    /**
     * App-委托代扣纯签约：https://pay.weixin.qq.com/wiki/doc/api/wxpay_v2/papay/chapter3_2.shtml
     * 外部App拉起微信客户端发起签约前，需先后台调用预签约接口完成预签约，获取pre_entrustweb_id，再拉起微信客户端，完成签约，返回App。
     */
    @RequestMapping("/appPreenTrustWeb")
    public R appPreenTrustWeb(String orderNo, HttpServletRequest request){
        UserInfoVo userVo = getUserVo(request);
        log.info("app用户发起委托代扣纯签约{}", userVo);
        log.info("app用户发起委托代扣纯签约扣款单号{}", orderNo);
        if (StringUtils.isBlank(orderNo)) {
            return R.reError("单号缺失，请先创建订单");
        }
        String pkey = "pre_entrustweb_id_" + userVo.getUserId();
        String preEntrustwebId = redisUtils.get(pkey);
        if (StringUtils.isBlank(preEntrustwebId)) {
            Map<String, String> result = wxPayService.appPreenTrustWeb(userVo, orderNo);
            if(result != null && result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
                preEntrustwebId = result.get("pre_entrustweb_id");
                //预签约id，两个小时内有效 (这里只缓存1.5小时)
                redisUtils.set(pkey, preEntrustwebId, 5400);
            }else {
                return R.reError("发起预签约失败");
            }

        }
        return R.reOk().put("pre_entrustweb_id", preEntrustwebId);
    }
    
    /**
     * App-委托代扣纯签约：https://pay.weixin.qq.com/wiki/doc/api/wxpay_v2/papay/chapter3_2.shtml
     * 外部App拉起微信客户端发起签约前，需先后台调用预签约接口完成预签约，获取pre_entrustweb_id，再拉起微信客户端，完成签约，返回App。
     */
    @RequestMapping("/proPreenTrustWeb")
    public R proPreenTrustWeb(String orderNo, HttpServletRequest request){
        UserInfoVo userVo = getUserVo(request);
        log.info("app用户发起委托代扣纯签约{}", userVo);
        log.info("app用户发起委托代扣纯签约扣款单号{}", orderNo);
        if (StringUtils.isBlank(orderNo)) {
            return R.reError("单号缺失，请先创建订单");
        }
        String pkey = "pre_entrustweb_id_" + userVo.getUserId();
        String preEntrustwebId = redisUtils.get(pkey);
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isBlank(preEntrustwebId)) {
        	CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
            result = wxPayService.appPreenTrustWebParams(userVo, orderNo, cardOrder.getType());
            if(result != null){
                
            }else {
                return R.reError("获取预签约参数失败");
            }
        }
        return R.reOk().put("params", result);
    }

    /**
     * 签约关系查询
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/queryContract")
    public R queryContract(HttpServletRequest request){
        UserInfo userInfo = userInfoService.selectByPrimaryKey(getUserId(request));
        //查询是否已签约
        if (StringUtils.isBlank(userInfo.getContractId())) { //签约状态： 0：已签约 1：未签约 9：签约进行中
            return R.reOk().put("contract_state", "1");
        }
        Map<String, String> result = wxPayService.queryContract(userInfo.getContractId());
        if(result != null && result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
            return R.reOk().put("contract_state", result.get("contract_state"));
        }else {
            return R.reError("查询签约失败");
        }
    }

    /**
     * 测试委托代扣
     * @param papPayApply
     * @param request
     * @return
     */
    @RequestMapping("/papPaymentTest")
    public R appPreenTrustWeb(PapPayApplyVo papPayApply, HttpServletRequest request){
        log.info("测试委托代扣扣款{}", papPayApply);
        wxPayService.papPayApply(papPayApply);
        return R.reOk();
    }

    @RequestMapping("/autoPapPaymentTest")
    public R autoPapPaymentTest(){
        log.info("测试委托代扣扣款papAutoPay");
        wxPayService.papAutoPay();
        return R.reOk();
    }

    /**
     * 测试委托代扣-退款
     * @param out_trade_no
     * @param request
     * @return
     */
    @RequestMapping("/papRefundTest")
    public R papRefundTest(String out_trade_no, String transaction_id, String out_refund_no, Integer total_fee, Integer refund_fee,
                           String refund_desc, HttpServletRequest request){
        log.info("测试委托代扣退款{}", out_trade_no);
        wxPayService.papRefundApply(out_trade_no, transaction_id, out_refund_no, total_fee, refund_fee, refund_desc);
        return R.reOk();
    }

    /**
     * 测试代扣-解约
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/deleteContractTest")
    public R deleteContractTest(String contractId, HttpServletRequest request){
        log.info("解约测试控制器{}", contractId);
        UserInfoVo userVo = getUserVo(request);
        if(StringUtils.isBlank(contractId))contractId = userVo.getContractId();
        Device myDevice = deviceMapper.selectUserValidity(userVo.getUserId());
        if(myDevice != null) {
        	Device up = new Device();
        	up.setDeviceId(myDevice.getDeviceId());
        	up.setAutoPay(0);
        	deviceMapper.updateByPrimaryKeySelective(up);
        }
        UserInfo userUp = new UserInfo();
        userUp.setUserId(userVo.getUserId());
        userUp.setWtState(2);
        userUp.setContractId("");
        userUp.setContractTime(new Date());
        userInfoService.updateUserAccount(userUp);
        wxPayService.deleteContract(contractId);
        return R.reOk();
    }

    /**
     * 测试代扣-签约关系查询
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/queryContractTest")
    public R queryContractTest(String contractId, HttpServletRequest request){
        log.info("签约关系查询{}", contractId);
        return R.reOk(wxPayService.queryContract(contractId));
    }

    /**
     * 测试代扣-查询订单
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/papOrderQueryTest")
    public R papOrderQueryTest(String outTradeNo, HttpServletRequest request){
        log.info("查询订单查询{}", outTradeNo);
        return R.reOk(wxPayService.papOrderQuery(outTradeNo));
    }

    private String setWechatXml(String return_code, String return_msg) {
        SortedMap<String, String> parameters = new TreeMap<String, String>();
        parameters.put("return_code", return_code);
        parameters.put("return_msg", return_msg);
        return "<xml><return_code><![CDATA[" + return_code + "]]>" +
                "</return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }


    /**
     * 小程序支付
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/proPay")
    public R proPay(String orderNo, String paySum, HttpServletRequest request){
        log.info("小程序支付{}", orderNo);
        UserInfoVo userVo = getUserVo(request);
        Map<String, Object> sortMap = null;
        
        try {
        	Map<String, Object> product = new HashMap<>();
	        product.put("orderNo", orderNo);
	        product.put("body", "矢历运动");
	        product.put("totalFee", paySum);
	        product.put("openId", userVo.getOpenId());
	        sortMap = wxPayService.doPay(product,request);
        }catch(Exception e) {
        	return R.reError(e.getMessage());
        }
        
        return R.reOk().put("params", sortMap);
    }
    
    /**
     * 使用订单支付回调
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/proPayNotify")
    @Transactional
    public void proPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("进入微信回调-----文章支付----------");
        try {
            Map<String, String> map = wxPayService.parseResult(request);
            //Map<String, String> map = null;
            log.info("-----------微信回调数据: " + map);
            if (map.get("return_code").equals("SUCCESS")) {
                //获取订单
                String orderNo = map.get("out_trade_no");
                //获取微信订单号
                String transaction_id = map.get("transaction_id");
                
                BigDecimal wallet = new BigDecimal(map.get("total_fee")).divide(new BigDecimal(100));
                if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_CARD_BUY_TYPE)) {
                    //VIP权益卡(后缀6):单事务记账+激活+sold_count自增,幂等(小程序支付走本回调)
                    log.info("-------激活VIP权益卡(小程序回调)=========" );
                    vipBenefitService.activateByOrderNo(orderNo, wallet, transaction_id, ConfigConstant.WXPAY);
                } else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_TRANSFER_FEE_TYPE)) {
                    //VIP转让服务费(后缀7):单事务记账+转让单10→20待审核,幂等(小程序支付走本回调)
                    log.info("-------VIP转让服务费支付成功(小程序回调)=========" );
                    vipTransferService.payFeeCallback(orderNo, wallet, transaction_id, ConfigConstant.WXPAY);
                } else {
                    //更新健身卡订单
                    log.info("-------微信代扣更新健身卡订单=========" );
                    int res = cardOrderService.updateCardOrder(orderNo, wallet, transaction_id, ConfigConstant.WXPAY, 0);
                    if (res > 0) {
                        log.info("=========添加收支明细==========");
                        incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,wallet,ConfigConstant.WXPAY);
                        //随单加购权益会员:有占位则幂等激活(未加购命中0行跳过,不重复记账)
                        vipBenefitService.activateAttached(orderNo);
                    }
                }

                response.getWriter().print("SUCCESS");
                return;
            }
        } catch (Exception e) {
            log.info("回调通知异常-----------------", e);
            response.getWriter().print("FAIL");
            return;
        }
    }


}
