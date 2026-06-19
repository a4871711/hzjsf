package com.dlc.modules.api.service;

import com.dlc.common.utils.R;

import java.math.BigDecimal;
import java.util.Map; /**
 * @Auther:YD
 * @Date: Creat in  2018/9/20/020
 */
public interface TeamClassOrderService {
    Map<String,Object> createTeamClassOrder(Map<String, Object> params,Long userId);

    int updateTeamClassOrder(String orderNo, BigDecimal wallet, String transaction_id, int payType);

    int freeTeamClassOrder(Map<String, Object> params,Long userId);
}
