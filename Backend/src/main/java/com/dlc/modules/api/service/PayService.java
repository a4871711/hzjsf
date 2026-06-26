package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;
import java.util.SortedMap;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/17/017
 */
public interface PayService {

    SortedMap<String, String> wxRechargePay(BigDecimal money, String orderNo, Integer notifyType) throws Exception;

//    SortedMap<Object,Object> zfbRechargePay(String orderNo, BigDecimal money);

    R blRechargePay(UserInfoVo user, BigDecimal money, String orderNo, Integer payType) throws ParseException;
    SortedMap<String, String> zfbRechargePay(String orderNo, BigDecimal money, String subject, Integer notifyType);

    String wxRefund(Map<String, Object> params) throws Exception;

    Map<String, Object> queryOrderInfoOrderNo(String orderNo);

    int updateOrderStatus(String orderNo, Map<String, Object> params, int payType);

    String zfbRefund(Map<String, Object> params);

    SortedMap<String,String> wxPay(String orderNo, BigDecimal mealSum, String openId, String notifyUrl) throws Exception;
}
