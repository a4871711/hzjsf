package com.dlc.modules.api.service;

import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/18/018
 */
public interface IncomePayDetailService {
    int saveIncomePayDetail(String orderNo, String transaction_id, BigDecimal wallet, Integer payType);
}
