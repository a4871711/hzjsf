package com.dlc.modules.api.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.*;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.MyConfig;
import com.dlc.modules.qd.utils.WxPayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/17/017
 * 微信支付
 */
@RestController
@RequestMapping("/api/pay")
public class PayController extends BaseController{

    private Logger log =  LoggerFactory.getLogger(getClass());
    @Autowired
    private PayService payService;
    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private PrivateClassService privateClassService;
    @Autowired
    private TeamClassOrderService teamClassOrderService;
    @Autowired
    private GoodsOrderService goodsOrderService;
    @Autowired
    private VipBenefitService vipBenefitService;
    @Autowired
    private com.dlc.modules.api.service.VipTransferService vipTransferService;
    @Autowired
    private PrivateOrderService privateOrderService;
    @Autowired
    private PtMemberWalletService ptMemberWalletService;
    @Autowired
    private PtInstallmentService ptInstallmentService;
    @Autowired
    private com.dlc.modules.api.service.CardPauseService cardPauseService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  购卡支付
     */
    @RequestMapping("/rechargePay")
    public R rechargePay(Integer payType, BigDecimal money, String orderNo, HttpServletRequest request) throws ParseException {
        log.info("============================购卡支付时前端传的money值："+ money +"========================");
        log.info("============================购卡支付时前端传的orderNo值："+ orderNo +"========================");
        SortedMap<String,String> map = new TreeMap<>();
        //元转换成分
//        int wallet = money.multiply(new BigDecimal(100)).intValue();
        UserInfoVo user = getUserVo(request);
        if (StringUtils.isBlank(orderNo)){
            return R.reError("订单号不能为空！");
        }
        if (StringUtils.isBlank(money+"")){
            return  R.reError("金额不能为空！");
        }
        if(money.compareTo(BigDecimal.ZERO) == -1){
            return  R.reError("金额不能为负数！该交易存在恶意攻击风险, 已被标记！");
        }
        if (user == null){
            return R.reError("用户未登入！");
        }
        if (payType == ConfigConstant.BLPAY){
                payType = ConfigConstant.BLPAY;
            try {
                return payService.blRechargePay(user,money,orderNo,payType);
            } catch (ParseException e) {
                log.info("rechargePay 转换异常"+e);
                e.printStackTrace();
            }
        }else if (payType == ConfigConstant.WXPAY){
            try {
                //为了方便处理回调，新加参数notifyType区分 0：标识充值回调   这里给null不影响后续流程
                map = payService.wxRechargePay(money,orderNo, null);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RRException("微信支付失败",e);
            }
        }else if (payType == ConfigConstant.ZFBPAY){

            String subject = "";
            if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_ORDER_TYPE)){
                subject = "购买健身卡支付";
            }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PRIVATE_CLASS_ORDER_TYPE)){
                subject = "购买私教课支付";
            }else if(orderNo.substring(orderNo.length()-1).equals(ConfigConstant.TEAM_CLASS_ORDER_TYPE)){
                subject = "购买团体课支付";
            }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.GOODS_ORDER_TYPE)){
                subject = "购买商品支付";
            }
            //为了方便处理回调，新加参数notifyType区分 0：标识充值回调   这里给null不影响后续流程
            map = payService.zfbRechargePay(orderNo,money,subject, null);
            return R.reOk(map.get("data"));
        }
        return R.reOk(map);
    }
    /**
     * 微信公众号支付
     */
    @RequestMapping("/orderPayH5")
    public R orderPay(String openId, BigDecimal money, String orderNo, HttpServletResponse response) {
        if (StringUtils.isBlank(orderNo) || StringUtils.isBlank(openId)) {
            return R.reError("参数不能为空");
        }
        try {
            SortedMap<String, String> map = payService.wxPay(orderNo, money, openId, ConfigConstant.NOTIFY_URL);
            return R.reOk(map);
        } catch (Exception e) {
            throw new RRException("微信支付失败", e);
        }

    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  购卡微信支付回调
     */
    @RequestMapping(value = "/wxNotify",method = RequestMethod.POST)
    public void rechargeCallBack(HttpServletRequest request, HttpServletResponse response){
        log.info("开始进入微信回调=================================");
        UserInfoVo user = getUserVo(request);
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
                //充值金额
                BigDecimal wallet = new BigDecimal(resultMap.get("total_fee")).divide(new BigDecimal(100));
                log.info("-------回调的充值金额------:" + wallet);
                //流水号/订单号？
                String transaction_id = resultMap.get("transaction_id");
                log.info("-------回调的充值单号------:" + transaction_id);
                String orderNo = resultMap.get("out_trade_no");
                log.info("-------回调的商户单号------:"+ orderNo);
                //添加收支明细
                int payType = ConfigConstant.WXPAY;
                log.info("=========添加收支明细==========");
                //VIP权益卡(后缀6)/转让服务费(后缀7)/私教商品(后缀b)/储值充值(后缀8)/分期首付(后缀9)/分期后续(后缀a)/停卡费(后缀c)
                //的记账在各自 service 内部单事务完成,此处跳过避免重复记账(后缀a/c非数字,若不跳过 Integer.valueOf 会抛异常)
                String vipSuffix = orderNo.substring(orderNo.length()-1);
                if (!vipSuffix.equals(ConfigConstant.VIP_CARD_BUY_TYPE)
                        && !vipSuffix.equals(ConfigConstant.VIP_TRANSFER_FEE_TYPE)
                        && !vipSuffix.equals(ConfigConstant.PT_PRIVATE_ORDER_TYPE)
                        && !vipSuffix.equals(ConfigConstant.WALLET_RECHARGE_TYPE)
                        && !vipSuffix.equals(ConfigConstant.INSTALLMENT_DOWN_TYPE)
                        && !vipSuffix.equals(ConfigConstant.INSTALLMENT_BILL_TYPE)
                        && !vipSuffix.equals(ConfigConstant.CARD_PAUSE_FEE_TYPE)) {
                    incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,wallet,payType);
                }
                if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_ORDER_TYPE)){
                    //更新健身卡订单
                    log.info("-------更新健身卡订单=========================================" );
                    cardOrderService.updateCardOrder(orderNo,wallet,transaction_id,payType, 0);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PRIVATE_CLASS_ORDER_TYPE)){
                    //更新私教课订单
                    log.info("-------更新私教课订单=========================================" );
                    privateClassService.updatePrivateClassOrder(orderNo,wallet,transaction_id,payType);
                }else if(orderNo.substring(orderNo.length()-1).equals(ConfigConstant.TEAM_CLASS_ORDER_TYPE)){
                    //更新团体订单
                    log.info("-------更新团体订单=========================================" );
                    teamClassOrderService.updateTeamClassOrder(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.GOODS_ORDER_TYPE)){
                    //更新商品订单
                    log.info("-------更新商品订单=========================================" );
                    goodsOrderService.updateByOrderNo(orderNo, wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_CARD_BUY_TYPE)){
                    //激活VIP权益卡(单事务:记账+激活+sold_count+1,幂等)
                    log.info("-------激活VIP权益卡=========================================" );
                    vipBenefitService.activateByOrderNo(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_TRANSFER_FEE_TYPE)){
                    //VIP转让服务费(单事务:记账+转让单10→20待审核,幂等)
                    log.info("-------VIP转让服务费支付成功=========================================" );
                    vipTransferService.payFeeCallback(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PT_PRIVATE_ORDER_TYPE)){
                    //私教商品购买(单事务:记账+扣库存+券核销+结清+建权益,幂等三道闸)
                    log.info("-------更新私教商品购买订单=========================================" );
                    privateOrderService.updatePrivateOrder(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.WALLET_RECHARGE_TYPE)){
                    //储值充值(后缀8):单事务记账+行锁加余额+写充值流水,两道幂等闸(先查流水+out_order_no唯一键)
                    log.info("-------储值充值到账=========================================" );
                    ptMemberWalletService.walletRechargeCallback(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_DOWN_TYPE)){
                    //分期首付独立单(后缀9,防御性):单事务记账+首付账单入账+计划推进+订单转部分支付+激活权益,幂等
                    log.info("-------分期首付到账=========================================" );
                    ptInstallmentService.installmentDownCallback(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_BILL_TYPE)){
                    //分期后续期(后缀a):单事务记账+账单入账(pay_order_no行锁+status条件)+计划推进/结清,幂等
                    log.info("-------分期后续期到账=========================================" );
                    ptInstallmentService.installmentBillCallback(orderNo,wallet,transaction_id,payType);
                }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_PAUSE_FEE_TYPE)){
                    //付费停卡费(后缀c):单事务记账+停卡生效+预顺延有效期,幂等
                    log.info("-------付费停卡费支付成功=========================================" );
                    cardPauseService.payCallback(orderNo,wallet,transaction_id,payType);
                }
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
     *  @Auther:YD
     *  @parameters:
     *  支付宝回调
     */
    @RequestMapping(value = "/zfbNotify", method = RequestMethod.POST)
    public void zfbNotify(HttpServletRequest request, HttpServletResponse response, UserInfoVo user){
        log.info(">>进入支付宝回调zfbNotify");
        log.info("打印User+++++++++++++++++++++++++++++"+user);
        //UserInfoVo user2 = getUserVo(request);
        //log.info("打印user信息");
        //log.info("+====================================="+user2.toString()+"+++++++++++++++++++++++++++++++++++");
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
        log.info(">>开始支付宝回调============================================="+params);
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, MyConfig.ZFB_PUBLIC_KEY, MyConfig.ZFB_CHARSET, MyConfig.ZFB_SIGN_TYPE);
            log.info(">>开始支付宝回调=============================================输出flag的值："+flag);
            if(flag){
                if(params.get("trade_status").equals("TRADE_SUCCESS")){
                    log.info(">>正式进入支付宝回调=============================================");
                    //商户订单号
                    String orderNo = params.get("out_trade_no");
                    //支付宝流水号
                    String transaction_id = params.get("trade_no");
                    //金额
                    BigDecimal wallet = new BigDecimal(params.get("total_amount"));
                    //支付类型
                    int payType = ConfigConstant.ZFBPAY;
                    //添加收支明细(私教商品后缀b/储值充值后缀8/分期首付后缀9/分期后续后缀a的记账在各自 service 单事务内完成,
                    //此处跳过避免重复记账;后缀a非数字,若不跳过 Integer.valueOf 会抛异常)
                    if (!orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PT_PRIVATE_ORDER_TYPE)
                            && !orderNo.substring(orderNo.length()-1).equals(ConfigConstant.WALLET_RECHARGE_TYPE)
                            && !orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_DOWN_TYPE)
                            && !orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_BILL_TYPE)) {
                        incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,wallet,payType);
                    }
                    if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_ORDER_TYPE)){
                        //更新健身卡订单
                        log.info(">>开始支付回调更新健身卡订单=============================================");
                        cardOrderService.updateCardOrder(orderNo,wallet,transaction_id,payType, 0);
                    }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PRIVATE_CLASS_ORDER_TYPE)){
                        //更新私教课订单
                        log.info(">>开始支付宝回调更新私教课订单============================================");
                        privateClassService.updatePrivateClassOrder(orderNo,wallet,transaction_id,payType);
                    }else if(orderNo.substring(orderNo.length()-1).equals(ConfigConstant.TEAM_CLASS_ORDER_TYPE)){
                        //更新团体订单
                        log.info(">>开始支付宝回调更新团体订单=============================================");
                        teamClassOrderService.updateTeamClassOrder(orderNo,wallet,transaction_id,payType);
                    }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.GOODS_ORDER_TYPE)){
                        //更新商品订单
                        log.info(">>开始支付宝回调更新商品订单=============================================");
                        goodsOrderService.updateByOrderNo(orderNo, wallet,transaction_id,payType);
                    }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PT_PRIVATE_ORDER_TYPE)){
                        //私教商品购买(单事务:记账+扣库存+券核销+结清+建权益,幂等三道闸)
                        log.info(">>开始支付宝回调更新私教商品购买订单=============================================");
                        privateOrderService.updatePrivateOrder(orderNo,wallet,transaction_id,payType);
                    }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.WALLET_RECHARGE_TYPE)){
                        //储值充值(后缀8):单事务记账+行锁加余额+写充值流水,两道幂等闸(充值当前走微信,支付宝分支为对齐三链留置)
                        log.info(">>开始支付宝回调储值充值到账=============================================");
                        ptMemberWalletService.walletRechargeCallback(orderNo,wallet,transaction_id,payType);
                    }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_DOWN_TYPE)){
                        //分期首付独立单(后缀9,防御性):单事务记账+首付账单入账+计划推进+订单转部分支付+激活权益,幂等
                        log.info(">>开始支付宝回调分期首付到账=============================================");
                        ptInstallmentService.installmentDownCallback(orderNo,wallet,transaction_id,payType);
                    }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_BILL_TYPE)){
                        //分期后续期(后缀a):单事务记账+账单入账(pay_order_no行锁+status条件)+计划推进/结清,幂等
                        log.info(">>开始支付宝回调分期后续期到账=============================================");
                        ptInstallmentService.installmentBillCallback(orderNo,wallet,transaction_id,payType);
                    }
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
     * 公众号支付微信回调
     */
    @RequestMapping(value = "/wxH5Notify", method = RequestMethod.POST)
    public void wcPayNotify(HttpServletRequest request, HttpServletResponse response) {
        log.info(">>进入公众号支付微信回调");
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
            log.info(">>进入微信回调" + map);
            if (map.get("return_code").equals("SUCCESS")) {
                //充值金额
                BigDecimal wallet = new BigDecimal(map.get("total_fee")).divide(new BigDecimal(100));
                log.info("-------回调的充值金额------:" + wallet);
                //流水号/订单号？
                String transaction_id = map.get("transaction_id");
                log.info("-------回调的充值单号------:" + transaction_id);
                String orderNo = map.get("out_trade_no");
                log.info("-------回调的商户单号------:"+ orderNo);
                //添加收支明细
                int payType = ConfigConstant.WXPAY;
                //更新健身卡订单
                incomePayDetailService.saveIncomePayDetail(orderNo,transaction_id,wallet,payType);
                log.info(">>开始支付回调更新健身卡订单=============================================");
                cardOrderService.updateCardOrder(orderNo,wallet,transaction_id,payType, 0);
                log.info(">>公众号支付微信回调成功");
                response.getWriter().print("SUCCESS");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(">>公众号支付回调通知异常");
        }
    }

    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public void test(String orderNo, BigDecimal wallet, String transaction_id, Integer payType) throws ParseException{
        System.out.println("测试" + orderNo);
        cardOrderService.updateCardOrder(orderNo,wallet,transaction_id,payType, 0);
    }


}
