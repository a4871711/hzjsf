package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.UserInfo;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-19 21:07
 */
public interface ZfbService {
    /**
     * 根据支付宝authCode  获取accessToken
     * @param authCode
     * @return
     */
    String getAccessToken(String authCode);

    /**
     * 根据accessToken获取支付宝用户信息
     * @param authCode
     */
    UserInfo getAlipayUser(String authCode);
}
