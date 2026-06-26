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
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.ZfbService;
import com.dlc.modules.qd.utils.MyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chenyuexin
 * @date 2018-09-19 21:08
 * @version 1.0
 */
@Service
@Transactional
public class ZfbServiceImpl implements ZfbService {
    private final Logger logger = LoggerFactory.getLogger(ZfbServiceImpl.class);

    private final static AlipayClient alipayClient = new DefaultAlipayClient(MyConfig.ZFB_GATEWAY,MyConfig.ZFB_APPID,
            MyConfig.ZFB_PRIVATE_KEY, MyConfig.ZFB_OBJECT, MyConfig.ZFB_CHARSET,
            MyConfig.ZFB_PUBLIC_KEY, MyConfig.ZFB_SIGN_TYPE);  //获得初始化的AlipayClient

    @Override
    public String getAccessToken(String authCode) {
        String accessToken = null;
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();//创建API对应的request类
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        try{
            //通过alipayClient调用API，获得对应的response类
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            accessToken = response.getAccessToken();
        }catch (Exception e){
            logger.info("获取支付宝accessToken失败：",e);
        }
        return accessToken;
    }

    @Override
    public UserInfo getAlipayUser(String authCode) {
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        UserInfo userInfo = new UserInfo();
        try{
            String accessToken = this.getAccessToken(authCode);
            logger.info("getAlipayUser---accessToken-->"+accessToken);
            AlipayUserInfoShareResponse response = alipayClient.execute(request,accessToken);
            logger.info("getAlipayUser---response-->"+ JSONObject.toJSONString(response));
            if(response.isSuccess()){
                logger.info("AlipayUserInfoShareResponse---->调用成功");
                userInfo.setNickname(response.getNickName());
                userInfo.setHeadImgUrl(response.getAvatar());
                /*userInfo.setSex(response.getGender().equals("m")?1:2);*/
            }
        }catch (Exception e){
            logger.info("获取支付宝userInfo失败：",e);
        }
        return userInfo;
    }
}
