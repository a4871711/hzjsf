package com.dlc.modules.qd.utils;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.RedisUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Package com.dlc.modules.qd.utils
 * @Description: GetWeiXinCode
 * @Copyright: Copyright (c) 2017
 * Author tangxs
 * @date 2017/12/13 16:14
 * version V1.0.0
 */
@Component
public class GetWeiXinCode {

    private static RedisUtils redisUtils;

    private static Logger logger = LoggerFactory.getLogger(GetWeiXinCode.class);

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    public static String getAccessTokenUrl(String appid, String secret) {

        String result = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
        return result;

    }

    public static String getTicketUrl(String accessToken) {

        String ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";

        return ticket;

    }

    public static String getInfoUrl(String accessToken, String openid) {

        String result = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
        return result;

    }

    public static String getCodeRequestByBase(String appid, String url) {

        String result = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        return result;

    }

    public static String getCurrentOpenId(String code, String appid, String secret) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        return url;

    }

    /**
     * @Package com.dlc.modules.qd.utils
     * @Description: GetWeiXinCode
     * @Copyright: Copyright (c) 2017
     * Author tangxs
     * @date 2017/12/14 16:13
     * version V1.0.0
     */
    public static JSONObject getInfoUrlByAccessToken(String accessToken, String openid) {


        logger.info("getInfoUrlByAccessToken = [" + accessToken + "], openid = [" + openid + "]");
        String getInfoUrl = GetWeiXinCode.getInfoUrl(accessToken, openid);
        String result = SendPushPost.sendGet(getInfoUrl);
        JSONObject httpRequest = JSONObject.parseObject(result);
        logger.info(result);
        redisUtils.set("wxUser" + openid, httpRequest);
        return httpRequest;
    }

    public static String getAccessToken(String appid, String secret) {
        String accessToken = redisUtils.get(appid + "gxzjj_accessToken");
        if ((StringUtils.isEmpty(accessToken)) || ("NULL".equals(accessToken.toUpperCase()))) {
            String accessTokenUrl = GetWeiXinCode.getAccessTokenUrl(appid, secret);
            String result = SendPushPost.sendGet(accessTokenUrl);
            JSONObject httpRequest = JSONObject.parseObject(result);
            accessToken = (String) httpRequest.get("access_token");
            Integer expiresIn = (Integer) httpRequest.get("expires_in");
            logger.info("wocaonima++++++++++++++++" + result);
            redisUtils.set(appid + "gxzjj_accessToken", accessToken, expiresIn);
            return accessToken;
        }
        return accessToken;
    }

    public static String getAccessToken(String appid, String secret, String key) {
        String accessToken = redisUtils.get(key);
        if ((StringUtils.isEmpty(accessToken)) || ("NULL".equals(accessToken.toUpperCase()))) {
            String accessTokenUrl = GetWeiXinCode.getAccessTokenUrl(appid, secret);
            String result = SendPushPost.sendGet(accessTokenUrl);
            JSONObject httpRequest = JSONObject.parseObject(result);
            accessToken = (String) httpRequest.get("access_token");
            Integer expiresIn = (Integer) httpRequest.get("expires_in");
            logger.info("getAccessToken=!!!!" + result);
            redisUtils.set(key, accessToken, expiresIn);
            return accessToken;
        }
        return accessToken;
    }

    public static JSONObject getOpenId(HttpServletRequest request, String code) {


        String currentOpenIdurl = GetWeiXinCode.getCurrentOpenId
                (code, ConfigConstant.GZH_WECHAT_APPID, ConfigConstant.GZH_WECHAT_APPSECRET);
        String result = SendPushPost.sendGet(currentOpenIdurl);
        logger.info("getOpenId--->result=" + result.toString());

        JSONObject obj = JSONObject.parseObject(result);
        String accessToken = (String) obj.get("access_token");
        Integer expiresIn = (Integer) obj.get("expires_in");
        redisUtils.set("hzjsf_access_token", accessToken, expiresIn);
        //String openId = (String) obj.get("openid");
        //logger.info("getOpenId=" + result.toString());
        return obj;

    }

    public static String getAccToken(String code) {
        String token = redisUtils.get("accetoken_gxzjj");
        if ((StringUtils.isEmpty(token)) || ("NULL".equals(token.toUpperCase()))) {
            String accetonkeUrl = GetWeiXinCode.getCurrentOpenId
                    (code, ConfigConstant.GZH_WECHAT_APPID, ConfigConstant.GZH_WECHAT_APPSECRET);

            String result = SendPushPost.sendGet(accetonkeUrl);
            logger.info("getAccToken=" + result);
            JSONObject resultMap = JSONObject.parseObject(result);
            Integer expiresIn = (Integer) resultMap.get("expires_in");
            String tonken = resultMap.get("access_token").toString();
            redisUtils.set("accetoken_gxzjj", tonken, expiresIn);
            return token;
        }
        return token;
    }

    /**
     * @Package com.dlc.modules.qd.utils
     * @Description: GetWeiXinCode
     * @Copyright: Copyright (c) 2017
     * Author tangxs
     * @date 2017/12/15 14:03
     * version V1.0.0
     */
    public static JSONObject getTicket(String accessToken) {

        JSONObject httpRequest;
        String ticket = redisUtils.get("hzjsf_ticket");
        if ((StringUtils.isEmpty(ticket)) || ("NULL".equals(ticket.toUpperCase()))) {
            String ticketUrl = GetWeiXinCode.getTicketUrl(accessToken);
            String result = SendPushPost.sendGet(ticketUrl);
            httpRequest = JSONObject.parseObject(result);
            Integer expiresIn = (Integer) httpRequest.get("expires_in");
            redisUtils.set("hzjsf_ticket", httpRequest, expiresIn);
            logger.info(result);
            return httpRequest;
        }
        return JSONObject.parseObject(ticket);
    }
    
    /*
     * 获取手机号码url
     */
    public static String getUserPhoneNumberUrl(String component_access_token) {
    	String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token="+component_access_token;
        return url;
    }

    /**获取小程序accessToken 及 openId URL*/
    public static String getOpenIdSmallUrl(String wxCode, String appid, String secret){
        String smallOpenIdUrl = "https://api.weixin.qq.com/sns/jscode2session?appid="+appid
                + "&secret=" + secret +"&js_code="+wxCode+"&grant_type=authorization_code" ;
        return smallOpenIdUrl;
    }

    /**
     * 小程序
     * @param code
     * @return
     */
    public static JSONObject getUserPhoneNumber(String code) {
    	String access_token = getAccessToken(ConfigConstant.PRO_WECHAT_APPID, ConfigConstant.PRO_WECHAT_APPSECRET);
    	String url = getUserPhoneNumberUrl(access_token);
    	JSONObject param = new JSONObject();
        param.put("code", code);
        String result = SendPushPost.sendPost(url, param.toJSONString(), true);
        JSONObject jsonObject = JSONObject.parseObject(result);
        logger.info("微信授权获取  用户手机:"+result);
    	return jsonObject;
    }

    /**
     * 获取小程序 openId
     * @param wxCode 微信code
     * @return
     */
    public static JSONObject getOpenIdSmall(String wxCode){
        String smallOpenIdUrl = GetWeiXinCode.getOpenIdSmallUrl(wxCode, ConfigConstant.PRO_WECHAT_APPID, ConfigConstant.PRO_WECHAT_APPSECRET);
        String result = SendPushPost.sendGet(smallOpenIdUrl);
        JSONObject wxObj = JSONObject.parseObject(result);
        //String openId = (String) wxObj.get("openid");
        logger.info("获取小程序-----20210525---------- wxObj " + wxObj.toJSONString());
        return wxObj;
    }

} 