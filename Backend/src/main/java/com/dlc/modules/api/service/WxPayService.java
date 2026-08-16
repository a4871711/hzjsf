package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.vo.PapPayApplyVo;
import com.dlc.modules.api.vo.UserInfoVo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import org.jdom.JDOMException;

/**
 * @author wtdk
 * @version 1.0
 * @date 2018-09-19 21:07
 */
public interface WxPayService {
    /**
     * APP发起签约-获取预签约id
     * @param userVo
     * @param orderNo
     * @return
     */
    Map<String, String> appPreenTrustWeb(UserInfoVo userVo, String orderNo);

    /**
     * 解约、签约回调处理
     * @param resultMap
     */
    void updateTrustNotify(Map<String, String> resultMap);

    /**
     * 发起-申请扣款
     * @param
     */
    Map<String, String> papPayApply(PapPayApplyVo papPayApplyVo);

    /**
     * 查询签约
     * @param contractId
     * @return
     */
    Map<String, String> queryContract(String contractId);

    /**
     * 申请解约
     * @param contractId
     * @return
     */
    Map<String, String> deleteContract(String contractId);

    /**
     * 查询订单
     * @param outTradeNo
     * @return
     */
    Map<String, String> papOrderQuery(String outTradeNo);

    /**
     * 自动续费代扣--退款
     * @param out_trade_no
     * @param out_refund_no
     * @param total_fee
     * @param refund_fee
     * @param refund_desc
     * @return
     */
    Map<String, String> papRefundApply(String out_trade_no, String transaction_id, String out_refund_no,
                                       Integer total_fee, Integer refund_fee, String refund_desc);

    R papPayApplyStart(BigDecimal money, String orderNo);

    /**
     * 发起-第一次代扣
     * @param resultMap
     */
    void firstPapPayment(Map<String, String> resultMap);

    /**
     * 自动扣费-轮询
     */
    void papAutoPay();

    /**
     * 创建自动扣费订单
     * @param device
     */
    void createAutoPayCardOrder(Device device);

	SortedMap<String, String> appPreenTrustWebParams(UserInfoVo userVo, String orderNo, int type);

	Map<String, Object> doPay(Map<String, Object> product, HttpServletRequest request) throws Exception;

    /** 查询普通微信支付订单（JSAPI），返回值已完成微信响应验签和商户身份校验 */
    Map<String, String> queryPayOrder(String outTradeNo);

    /** 关闭普通微信支付订单（JSAPI），返回值已完成微信响应验签和商户身份校验 */
    Map<String, String> closePayOrder(String outTradeNo);

    /** 旧健身卡订单支付回调：订单推进与收支流水保持在同一事务 */
    int updateCardOrderPay(String orderNo, BigDecimal amount, String transactionId);

	Map<String, String> parseResult(HttpServletRequest request) throws IOException, JDOMException;

}
