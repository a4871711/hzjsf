package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/14/014
 */
public interface CardOrderService {

    Map<String,Object> createFitCardOrder(UserInfoVo user, Map<String, Object> params, int flag);


    int updateCardOrder(String orderNo, BigDecimal wallet, String transaction_id, Integer payType, Integer autoPay) throws ParseException;

    Map<String,Object> selectCardOrderByUSerId(Long userId);

    Map<String,Object> queryCardInfoByUserId(Long userId);

    int updateOrderStatus(String orderNo, int status);


	List<Map<String, Object>> selectCardOrderList(Query query);


	int queryCardOrderCount(Query query);
}
