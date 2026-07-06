/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ZfbServiceImpl
 * Author:   Administrator
 * Date:     2018/9/19 21:08
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.HttpUtil;
import com.dlc.common.utils.CommonUtil;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.HttpRequest;
import com.dlc.common.utils.IPUtils;
import com.dlc.common.utils.PayCommonUtil;
import com.dlc.common.utils.R;
import com.dlc.common.utils.RedisUtils;
import com.dlc.common.utils.UUIDUtil;
import com.dlc.modules.api.dao.CardOrderMapper;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.StoreAddressMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.StoreAddress;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.WxPayService;
import com.dlc.modules.api.vo.PapPayApplyVo;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.MyConfig;
import com.dlc.modules.qd.utils.WxPayUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wtdk
 * @date 2018-09-19 21:08
 * @version 1.0
 */
@Service
public class WxPayServiceImpl implements WxPayService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    //签约回调通知url
    private static final String NOTIFY_PREENTRUST_URL = "http://shilijsf.shilisports.com/api/wx/preenTrustNotify";
    //扣款结果回调通知
    private static final String NOTIFY_PAYMENT_URL = "http://shilijsf.shilisports.com/api/wx/papPaymentNotify";
    //微信预签约接口
    private static final String PREENTRUST_URL = "https://api.mch.weixin.qq.com/papay/preentrustweb";
    //申请扣款
    private static final String PAPPAYAPPLY_URL = "https://api.mch.weixin.qq.com/pay/pappayapply";
    //申请解约
    private static final String DELETECONTRACT_URL = "https://api.mch.weixin.qq.com/papay/deletecontract";
    //查询签约
    private static final String QUERY_PREENTRUST_URL = "https://api.mch.weixin.qq.com/papay/querycontract";
    //订单查询
    private static final String PAPORDERQUERY_URL = "https://api.mch.weixin.qq.com/pay/paporderquery";
    //订单退款
    private static final String PAPORDERFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    //小程序支付回调
    private static final String NOTIFY_PROPAY_URL = "http://shilijsf.shilisports.com/api/wx/proPayNotify";
    //协议模板id
    private static final String PLAN_ID_209 = "190490";
    //协议模板id
    private static final String PLAN_ID_459 = "190491";
    private static final String PLAN_ID_239 = "199950";
    private static final String PLAN_ID_499 = "199951";
    /*//签约协议号
    private static final String CONTRACT_CODE = "1179165297853071360";
    //签约协议号
    private static final String REQUEST_SERIAL = "1179165297853071361";*/

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    public SortedMap<String, String> appPreenTrustWebParams(UserInfoVo userVo, String orderNo, int type) {
    	SortedMap<String, String> packageParams = new TreeMap<>();
    	try{
    		MyConfig config = new MyConfig();    	
	        packageParams.put("appid", config.getProAppID());
	        packageParams.put("mch_id", config.getMchID());
	        if("17688089557".equals(userVo.getPhone())) {
	        	packageParams.put("plan_id", type == 0 ? PLAN_ID_239 : PLAN_ID_499);  //协议模板id，后面有变更需要修正这个值
	        }else {
	        	packageParams.put("plan_id", type == 0 ? PLAN_ID_209 : PLAN_ID_459);  //协议模板id，后面有变更需要修正这个值
	        }
	        packageParams.put("contract_code", orderNo); //商户侧的签约协议号，由商户生成，只能是数字、大小写字母的描述。
	        packageParams.put("request_serial", String.valueOf(Calendar.getInstance().getTimeInMillis())); //商户请求签约时的序列号，要求唯一性。禁止使用0开头，序列号主要用于排序，不作为查询条件，纯数字,范围不能超过Int64的范围（9223372036854775807）。
	        packageParams.put("contract_display_account", StringUtils.isNotBlank(userVo.getPhone())?userVo.getPhone():"矢历运动"); //签约用户的名称，用于页面展示，参数值不支持UTF8非3字节编码的字符，例如表情符号，所以请勿传微信昵称到该字段
	        packageParams.put("notify_url", NOTIFY_PREENTRUST_URL); //用于接收签约成功消息的回调通知地址，以http或https开头，通知url必须为外网可访问的url，不能携带参数。
	        //packageParams.put("version", "1.0"); //版本号 固定值1.0
	        //packageParams.put("return_app", "Y");  //用来控制签约页面结束后的返回路径（不传此参数，则签约完成后停留在微信内）。Y表示返回app, 不填则不返回 注：签约参数appid必须为发起签约的app所有，且在微信开放平台注册过。ios用户点完成按钮才返回app，直接右划关闭页面或左上角不支持返回APP 安卓暂不支持返回APP
	        packageParams.put("timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
	        packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
	        log.info("APP发起签约<<packageParams>>{}", packageParams);
    	}catch(Exception e) {
    		return null;
    	}
    	return packageParams;
    }

    @Override
    public Map<String, String> appPreenTrustWeb(UserInfoVo userVo, String orderNo) {
        try {
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = appPreenTrustWebParams(userVo, orderNo, 0);
            String result = CommonUtil.requestWithCert(PREENTRUST_URL,packageParams ,5000,5000);
            log.info("+++++++预签约结果返回++++++++{}", result);
            //Map<String, String> map = WxPayUtils.doXMLParse(result);
            return WxPayUtils.doXMLParse(result);
        } catch (Exception e) {
            log.error("APP发起签约报错=={}",e);
        }
        return null;
    }

    @Transactional
    @Override
    public void updateTrustNotify(Map<String, String> resultMap) {
        String change_type = resultMap.get("change_type");
        if ("ADD".equals(change_type)) { //签约回调
            //String userId = resultMap.get("userId");
            CardOrder cardOrder = cardOrderMapper.selectByOrderNo(resultMap.get("orderNo"));
            UserInfo update = new UserInfo();
            update.setUserId(cardOrder.getUserId());
            update.setWtOpenId(resultMap.get("openid"));
            update.setWtState(1); //签约成功
            update.setContractId(resultMap.get("contract_id")); //委托代扣协议id
            userInfoMapper.updateContract(update);
        }else if ("DELETE".equals(change_type)){ //解约回调
            UserInfo update = new UserInfo();
            update.setWtOpenId(resultMap.get("openid"));
            update.setWtState(2); //解除签约
            update.setContractId(""); //委托代扣协议id resultMap.get("contract_id")
            userInfoMapper.updateCancelContract(update);
            
            Long userId = userInfoMapper.getUserIdByContractId(resultMap.get("contract_id"));
            Device myDevice = deviceMapper.selectUserValidity(userId);
            if(myDevice != null) {
            	Device up = new Device();
            	up.setDeviceId(myDevice.getDeviceId());
            	up.setAutoPay(0);
            	deviceMapper.updateByPrimaryKeySelective(up);
            }
        }
    }

    @Override
    public void firstPapPayment(Map<String, String> resultMap) {
        String orderNo = resultMap.get("orderNo");
        //查询卡订单有没有待支付的微信代扣订单
        //CardOrder cardOrder = cardOrderMapper.selectPapCardOrder(userId);
        CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
        if (cardOrder != null) {
        	if(cardOrder.getRealPayment().compareTo(BigDecimal.ZERO) > 0) {
	            String contractId = resultMap.get("contract_id");
	            //发起代扣
	            PapPayApplyVo papPayApply = new PapPayApplyVo();
	            papPayApply.setContractId(contractId);
	            papPayApply.setOutTradeNo(cardOrder.getOrderNo());
	            papPayApply.setTotalFee(cardOrder.getRealPayment().multiply(new BigDecimal("100")).intValue());
	            /*StoreAddress storeAddress = storeAddressMapper.selectByPrimaryKey(cardOrder.getStoreAddressId());
	            if (storeAddress != null) {//地址信息
	                papPayApply.setStoreId(storeAddress.getStoreAddrId());
	                papPayApply.setStoreName(storeAddress.getStoreName());
	                papPayApply.setStoreAddress(storeAddress.getStoreAddrDetail());
	            }*/
	            //发起扣款申请
	            Map<String, String> result = papPayApply(papPayApply);
	            if(result != null && result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
	                //申请扣款成功==等待回调处理卡订单...
	                log.info("第一次发起代扣申请成功");
	            }else {
	                //申请扣款失败--取消订单
	                cardOrder.setStatus(5); //已取消
	                cardOrderMapper.updateByPrimaryKeySelective(cardOrder);
	                log.info("第一次发起代扣申请失败");
	                //解约--让用户重新发起
	                deleteContract(contractId);
	            }
        	}else {
        		try {
	        		log.info(">>微信代扣价格为零");
	                //更新健身卡订单
	                log.info("-------微信代扣更新健身卡订单=========" );
	                BigDecimal wallet = cardOrder.getRealPayment();
	                String transaction_id = "";
	                int res = cardOrderService.updateCardOrder(orderNo, wallet, transaction_id, ConfigConstant.WXAUTOPAY, null);
	                if (res > 0) {
	                    log.info("=========添加自动续费收支明细==========");
	                    incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,wallet,ConfigConstant.WXAUTOPAY);
	                }
        		}catch(Exception e) {
        			log.info(">>微信代扣价格为零异常{}", e);
        		}
        	}
        }
    }


    @Override
    public void papAutoPay() {
        //自动扣费轮询-提前24小时触发扣费申请
        List<Device> list = deviceMapper.selectAutoPayUser();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Device device : list) {//续费
            	try {
            		log.info("定时器任务自动代扣申请-设备信息{}", device);
            		String isPay = redisUtils.get("papAutoPay_" + device.getDeviceId());
            		if(StringUtils.isNotBlank(isPay))continue;
            		createAutoPayCardOrder(device);
            	}catch(Exception e) {
            		log.error("定时器任务自动代扣申请报错=={}",e);
            	}
            }
        }
    }

    @Async
    @Transactional
    @Override
    public void createAutoPayCardOrder(Device device) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(device.getProxyId());
        log.info("定时器任务自动代扣申请-用户信息{}", userInfo);
        //查询是否已签约
        if (StringUtils.isBlank(userInfo.getContractId()) || userInfo.getWtState() != 1) { //签约状态： 0：无签约  1：已签约 2：已解约
            log.info("定时器自动代扣申请-该用户尚未签约成功代扣协议，无法正常发起代扣{}", userInfo);
            return;
        }
        UserInfoVo user = new UserInfoVo();
        user.setUserId(device.getProxyId());
        Map<String,Object> params = new HashMap<>();
        params.put("fitCardId", String.valueOf(device.getFitCardId()));
        BigDecimal price = device.getDevicePrice();

        params.put("nextPrice", device.getNextPrice());
        params.put("nextPriceTitle", device.getNextPriceTitle());
        params.put("nextPrice2", device.getNextPrice2());
        params.put("nextPriceTitle2", device.getNextPriceTitle2());
        params.put("nextPrice3", device.getNextPrice3());
        params.put("nextPriceTitle3", device.getNextPriceTitle3());
        //续费
    	String[] s1 = CommonUtil.tryStrings(device.getNextPriceTitle());
    	if (s1.length > 0 && device.getBuyCount() >= 1 && device.getNextPrice() != null && device.getNextPrice().compareTo(BigDecimal.ZERO) > 0) {//次月扣款金额
    		price = device.getNextPrice();
        }
    	String[] s2 = CommonUtil.tryStrings(device.getNextPriceTitle2());
        if (device.getBuyCount() > s1.length && device.getNextPrice2() != null && device.getNextPrice2().compareTo(BigDecimal.ZERO) > 0) {//次月扣款金额
        	price = device.getNextPrice2();
        }
        String[] s3 = CommonUtil.tryStrings(device.getNextPriceTitle3());
        if (device.getBuyCount() > s1.length + s2.length && device.getNextPrice3() != null && device.getNextPrice3().compareTo(BigDecimal.ZERO) > 0) {//次月扣款金额
        	price = device.getNextPrice3();
        }
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    	String validityDate = df.format(device.getValidityDate().getTime() + device.getValidity() * 24l * 60l * 60l * 1000l);
    	params.put("validityDate", validityDate);
    	
        params.put("type", device.getType());
        params.put("buyCount", device.getBuyCount() + 1);
        params.put("oldValidityDate", device.getValidityDate());
        params.put("paySum", String.valueOf(price));
        params.put("autoPay", device.getAutoPay() == null ? "0" : device.getAutoPay().toString()); //自动续费
        params.put("useCount", device.getUseCount()); //可使用次数
        params.put("selecteFree", device.getSelecteFree()); //0: 未勾选免费服务  1：勾选了免费服务
        params.put("deviceId", device.getDeviceId());
        Map<String,Object> map = cardOrderService.createFitCardOrder(user, params,1);
        System.out.println("创建续费订单后返回参数：" + map);
        if (map != null) { //创建自动续费订单成功
            //发起扣款申请
            String orderNo = String.valueOf(map.get("orderNo"));
            //BigDecimal money = new BigDecimal(String.valueOf(map.get("paySum")));
            //发起代扣
            PapPayApplyVo papPayApply = new PapPayApplyVo();
            papPayApply.setContractId(userInfo.getContractId());
            papPayApply.setOutTradeNo(orderNo);
            papPayApply.setTotalFee(price.multiply(new BigDecimal("100")).intValue());
            /*StoreAddress storeAddress = storeAddressMapper.selectByPrimaryKey(device.getStoreAddressId());
            if (storeAddress != null) {//地址信息
                papPayApply.setStoreId(storeAddress.getStoreAddrId());
                papPayApply.setStoreName(storeAddress.getStoreName());
                papPayApply.setStoreAddress(storeAddress.getStoreAddrDetail());
            }*/
            //发起扣款申请
            Map<String, String> result = papPayApply(papPayApply);
            if(result != null && result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
                log.info("定时器自动代扣申请扣款成功{}", result);
                redisUtils.set("papAutoPay_" + device.getDeviceId(), validityDate, 86400 * 3);
            }else {
                log.info("定时器自动代扣申请扣款失败{}", result);
            }
        }

    }

    //https://pay.weixin.qq.com/wiki/doc/api/wxpay_v2/papay/chapter3_3.shtml
    @Override
    public Map<String, String> papPayApply(PapPayApplyVo papPayApply) {
        try {
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", config.getProAppID());
            packageParams.put("mch_id", config.getMchID());
            packageParams.put("nonce_str", WxPayUtils.getRandomStringByLength(19));
            packageParams.put("body", "矢历运动"); //商品或支付单简要描述
            packageParams.put("detail", "自动续费套餐代扣"); //商品或支付单简要描述
            packageParams.put("out_trade_no", papPayApply.getOutTradeNo()); //商户系统内部的订单号,32个字符内、可包含字母
            packageParams.put("total_fee", papPayApply.getTotalFee()+""); //订单总金额，单位为分，只能为整数
            //packageParams.put("fee_type", "CNY"); //默认人民币：CNY
            packageParams.put("notify_url", NOTIFY_PAYMENT_URL); //接受扣款结果异步回调通知的url, 以http或https开头，通知url必须为外网可访问的url，不能携带参数。
            packageParams.put("trade_type", "PAP"); //交易类型 PAP：微信委托代扣支付
            packageParams.put("contract_id", papPayApply.getContractId());  //签约成功后，微信返回的委托代扣协议id
            /*if (papPayApply.getStoreId() != null) {
                JSONObject scene = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("id", papPayApply.getStoreId());//门店唯一标识
                info.put("name", papPayApply.getStoreName());//门店名称
                info.put("address", papPayApply.getStoreAddress());//门店详细地址
                //info.put("area_code", "441900");//441900
                scene.put("store_info", info);
                packageParams.put("scene_info", scene.toJSONString());  //该字段用于上报场景信息,目前支持上报实际门店信息,该字段为JSON对象数据
            }*/
            packageParams.put("timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
            packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
            log.info("代扣申请扣款<<packageParams>>{}", packageParams);
            String result = CommonUtil.requestWithCert(PAPPAYAPPLY_URL,packageParams ,5000,5000);
            log.info("+++++++代扣申请扣款结果返回++++++++{}", result);
            return WxPayUtils.doXMLParse(result);
        } catch (Exception e) {
            log.error("代扣申请扣款报错=={}",e);
        }
        return null;
    }

    /**
     * contract_state：协议状态，枚举值： 0：已签约 1：未签约 9：签约进行中
     * @param contractId
     * @return
     */
    @Override
    public Map<String, String> queryContract(String contractId) {
        try {
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", config.getProAppID());
            packageParams.put("mch_id", config.getMchID());
            packageParams.put("contract_id", contractId); //委托代扣签约成功后由微信返回的委托代扣协议id，选择contract_id查询
            packageParams.put("version", "1.0"); //版本号 固定值1.0
            packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
            log.info("签约查询<<packageParams>>{}", packageParams);
            String result = CommonUtil.requestWithCert(QUERY_PREENTRUST_URL,packageParams ,5000,5000);
            log.info("+++++++签约查询结果返回++++++++{}", result);
            return WxPayUtils.doXMLParse(result);
        } catch (Exception e) {
            log.error("签约查询结果报错=={}",e);
        }
        return null;
    }

    @Override
    public Map<String, String> deleteContract(String contractId) {
        try {
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", config.getProAppID());
            packageParams.put("mch_id", config.getMchID());
            packageParams.put("contract_id", contractId); //委托代扣签约成功后由微信返回的委托代扣协议id，选择contract_id查询
            packageParams.put("contract_termination_remark", "签约信息有误，需重新签约"); //解约原因的备注说明，如：签约信息有误，须重新签约
            packageParams.put("version", "1.0"); //版本号 固定值1.0
            packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
            log.info("申请解约<<packageParams>>{}", packageParams);
            String result = CommonUtil.requestWithCert(DELETECONTRACT_URL,packageParams ,5000,5000);
            log.info("+++++++申请解约结果返回++++++++{}", result);
            return WxPayUtils.doXMLParse(result);
        } catch (Exception e) {
            log.error("申请解约结果报错=={}",e);
        }
        return null;
    }

    /**
     * 订单号查询
     * @param outTradeNo
     * @return
     */
    @Override
    public Map<String, String> papOrderQuery(String outTradeNo) {
        try {
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", config.getProAppID());
            packageParams.put("mch_id", config.getMchID());
            packageParams.put("out_trade_no", outTradeNo); //单号
            packageParams.put("nonce_str", WxPayUtils.getRandomStringByLength(19));
            packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
            log.info("签约查询<<packageParams>>{}", packageParams);
            String result = CommonUtil.requestWithCert(PAPORDERQUERY_URL,packageParams ,5000,5000);
            log.info("+++++++订单查询结果返回++++++++{}", result);
            return WxPayUtils.doXMLParse(result);
        } catch (Exception e) {
            log.error("订单查询结果报错=={}",e);
        }
        return null;
    }

    @Override
    public Map<String, String> papRefundApply(String out_trade_no, String transaction_id, String out_refund_no, Integer total_fee, Integer refund_fee, String refund_desc) {
        try {
            MyConfig config = new MyConfig();
            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", config.getProAppID());
            packageParams.put("mch_id", config.getMchID());
            packageParams.put("nonce_str", WxPayUtils.getRandomStringByLength(19));
            if(StringUtils.isNotBlank(transaction_id))packageParams.put("transaction_id", transaction_id); //单号
            if(StringUtils.isNotBlank(out_trade_no))packageParams.put("out_trade_no", out_trade_no); //单号
            packageParams.put("out_refund_no", out_refund_no); //商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
            packageParams.put("total_fee", new BigDecimal(total_fee).multiply(new BigDecimal(100)).setScale(0).toPlainString()); //订单总金额，单位为分，只能为整数，
            packageParams.put("refund_fee", new BigDecimal(refund_fee).multiply(new BigDecimal(100)).setScale(0).toPlainString()); //退款总金额，订单总金额，单位为分，只能为整数
            packageParams.put("refund_desc", refund_desc); //若商户传入，会在下发给用户的退款消息中体现退款原因
            packageParams.put("sign", WxPayUtils.createSign("UTF-8", packageParams));
            log.info("自动续费代扣--退款<<packageParams>>{}", packageParams);
            String result = CommonUtil.requestWithCert(PAPORDERFUND_URL,packageParams ,5000,5000);
            log.info("+++++++自动续费退款结果返回++++++++{}", result);
            return WxPayUtils.doXMLParse(result);
        } catch (Exception e) {
            log.error("自动续费退款结果报错=={}",e);
        }
        return null;
    }

    /**
     * 扣款申请-装配
     * @param money
     * @param orderNo
     * @return
     */
    @Override
    public R papPayApplyStart(BigDecimal money, String orderNo) {
        CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(cardOrder.getUserId());
        //查询是否已签约
        if (userInfo.getWtState() != 1) { //签约状态： 0：无签约  1：已签约 2：已解约
            return R.reError("该用户尚未签约成功代扣协议，无法正常发起代扣");
        }
        //发起代扣
        PapPayApplyVo papPayApply = new PapPayApplyVo();
        papPayApply.setContractId(userInfo.getContractId());
        papPayApply.setOutTradeNo(orderNo);
        papPayApply.setTotalFee(money.multiply(new BigDecimal("100")).intValue());
        /*StoreAddress storeAddress = storeAddressMapper.selectByPrimaryKey(cardOrder.getStoreAddressId());
        if (storeAddress != null) {//地址信息
            papPayApply.setStoreId(storeAddress.getStoreAddrId());
            papPayApply.setStoreName(storeAddress.getStoreName());
            papPayApply.setStoreAddress(storeAddress.getStoreAddrDetail());
        }*/
        //发起扣款申请
        Map<String, String> result = papPayApply(papPayApply);
        if(result != null && result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
            return R.reOk("申请扣款成功");
        }else {
            return R.reError("申请扣款失败");
        }
    }

	/**
	 * 基础参数
	 * @param packageParams
	 */
	private void commonParams(SortedMap<Object, Object> packageParams) {
		try {
			MyConfig config = new MyConfig();
			String currTime = PayCommonUtil.getCurrTime();
			String strTime = currTime.substring(8);
			String strRandom = PayCommonUtil.buildRandom(4) + "";
			String nonce_str = strTime + strRandom;
			packageParams.put("appid", config.getProAppID());
			packageParams.put("mch_id", config.getMchID());
			packageParams.put("nonce_str", nonce_str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @Override
	public Map<String,Object> doPay(Map<String, Object> product, HttpServletRequest request) throws Exception{
    	Map map = null;
		String amount = CommonUtil.tryToBigDecimal(String.valueOf(product.get("totalFee"))).multiply(new BigDecimal("100")).setScale(0).toPlainString();
		//String amount = "1"; //测试金额
		String notify_url =  NOTIFY_PROPAY_URL;//回调接口
		String trade_type = "JSAPI";// 交易类型H5支付 也可以是小程序支付参数
		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		commonParams(packageParams);
		packageParams.put("body",String.valueOf(product.get("body")));// 商品描述
		packageParams.put("out_trade_no", String.valueOf(product.get("orderNo")));// 商户订单号
		packageParams.put("total_fee", amount);// 总金额
		packageParams.put("spbill_create_ip", IPUtils.getIpAddr(request));// 发起人IP地址
		packageParams.put("notify_url", notify_url);// 回调地址
		packageParams.put("trade_type", trade_type);// 交易类型
		packageParams.put("openid", String.valueOf(product.get("openId")));//用户openID			

		String key = "";
		try {
	    	MyConfig config = new MyConfig();
	    	key = config.getKey();
			String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
			packageParams.put("sign", sign);// 签名
			log.info("支付参数===="+packageParams);
			String requestXML = PayCommonUtil.getRequestXml(packageParams);
			String resXml = HttpRequest.postDataSSL("https://api.mch.weixin.qq.com/pay/unifiedorder", requestXML);
			log.info("支付XML====="+resXml);
		
			map = WxPayUtils.doXMLParse(resXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("支付返回参数===="+map);
		String returnCode = map.get("return_code").toString();
		String returnMsg =  map.get("return_msg").toString();
		Map<String,Object> res = new HashMap<>();
		if("SUCCESS".equals(returnCode)){
			String resultCode = (String) map.get("result_code");
			String errCodeDes = (String) map.get("err_code_des");
			if("SUCCESS".equals(resultCode)){
				//获取预支付交易会话标识
				String prepay_id = (String) map.get("prepay_id");
				String prepay_id2 = "prepay_id=" + prepay_id;
				String packages = prepay_id2;
				SortedMap<Object, Object> finalpackage = new TreeMap<>();
				String timestamp = PayCommonUtil.getTimestamp();
				String nonceStr = packageParams.get("nonce_str").toString();
				finalpackage.put("appId",  String.valueOf(packageParams.get("appid")));
				finalpackage.put("timeStamp", timestamp);
				finalpackage.put("nonceStr", nonceStr);
				finalpackage.put("package", packages);
				finalpackage.put("signType", "MD5");
				//这里很重要  参数一定要正确 狗日的腾讯 参数到这里就成大写了
				//可能报错信息(支付验证签名失败 get_brand_wcpay_request:fail)
				String sign = PayCommonUtil.createSign("UTF-8", finalpackage,key);
				log.info("finalpackage====="+finalpackage);

				res.put("appId",String.valueOf(packageParams.get("appid")));
				res.put("nonceStr",nonceStr);
				res.put("signType","MD5");
				res.put("package",packages);
				res.put("paySign",sign);
				res.put("timeStamp",timestamp);
				log.info("支付结果参数====="+res);


				return res;
			}else{
				log.info("订单号:{}错误信息:{}",String.valueOf(product.get("orderNo")),errCodeDes);
				throw new Exception("该订单已支付");
			}
		}else{
			log.info("订单号:{}错误信息:{}",String.valueOf(product.get("orderNo")),returnMsg);
			throw new Exception("支付发生错误");
		}
	}

    @Override
    public Map<String, String> parseResult(HttpServletRequest request) throws IOException, JDOMException {

        InputStream inStream;
        inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        String result = new String(outSteam.toByteArray(), "utf-8");
        Map<String, String> map = WxPayUtils.doXMLParse(result);
        return map;

    }

}
