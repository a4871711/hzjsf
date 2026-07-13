/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: UserInfoController
 * Author:   Administrator
 * Date:     2018/5/18 18:49
 * Description: 用户基本信息
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.dlc.common.utils.*;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.validator.group.AddGroup;
import com.dlc.common.validator.group.LoginGroup;
import com.dlc.modules.api.dao.CardPauseRecordMapper;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.OpenDoorRecordMapper;
import com.dlc.modules.api.entity.Advertising;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.MsgLog;
import com.dlc.modules.api.entity.StoreDeviceV2;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.entity.UserSportDeviceRecord;
import com.dlc.modules.api.service.BodyInfoService;
import com.dlc.modules.api.service.StoreAddressService;
import com.dlc.modules.api.service.StoreDeviceV2Service;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.*;
import com.dlc.modules.sys.shiro.ShiroUtils;

import io.rong.util.GsonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/userInfo")
public class UserInfoController extends BaseController {
    final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AliyunSmsUtil aliyunSmsUtil;
    @Autowired
    private BodyInfoService bodyInfoService;

    @Autowired
    private StoreDeviceV2Service storeDeviceV2Service;
    @Autowired
    private StoreAddressService storeAddressService;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private OpenDoorRecordMapper openDoorRecordMapper;
    @Autowired
    private CardPauseRecordMapper cardPauseRecordMapper;

    @Value("${rpro3_qrcode_path}")
    private String rpro3QrcodePath;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public R register(UserInfo userInfo, String code, String rePassword) throws Exception {
        ValidatorUtils.validateEntity(userInfo, AddGroup.class);
        if (StringUtils.isBlank(userInfo.getPhone())) {
            return R.reError("手机号不能为空");
        }
        if (!PhoneCodeVer.isPhoneNum(userInfo.getPhone())) {
            return R.reError("手机号码错误");
        }
        Map<String, Object> map = userInfoService.getUserInfoByPhone(userInfo.getPhone());
        if (map != null) {
            return R.reError("该手机号码已经注册");
        }
        if (StringUtils.isEmpty(userInfo.getPassword())) {
            return R.reError("密码为空");
        }
        String codeStr = redisUtils.get(ConfigConstant.PHONE + userInfo.getPhone());
        if (StringUtils.isEmpty(codeStr)) {
            return R.reError("验证码失效");
        }
        if (!code.equalsIgnoreCase(codeStr)) {
            return R.reError("验证码错误");
        }
        this.userInfoService.saveUserInfo(userInfo);
        return R.reOk();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public R login(UserInfo userInfo, HttpServletRequest req) {
        logger.info("用户进入登录状态..................");
        ValidatorUtils.validateEntity(userInfo, LoginGroup.class);
        JSONObject obj = this.userInfoService.login(userInfo, req);
        if (obj.containsKey("userInfo")) {
            req.getSession().setAttribute(ConfigConstant.ACCOUNT, obj.get("userInfo"));
            return R.reOk(obj);
        }
        return R.reError(CodeAndMsg.ERROR_USER_LOGIN);
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    public R sendCode(String phone) {
        if (StringUtils.isEmpty(phone) || !PhoneCodeVer.isPhoneNum(phone)) {
            return R.reError("手机号码有误");
        }
        PhoneCodeVer.sendCode(phone);
        return R.reOk();
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/sendCodeV2", method = RequestMethod.POST)
    public R sendCodeV2(String phone) throws Exception {
        if (StringUtils.isEmpty(phone) || !PhoneCodeVer.isPhoneNum(phone)) {
            return R.reError("手机号码有误");
        }
        MsgLog log = aliyunSmsUtil.sendSms(phone);
        if(log != null && log.getStatus() == 1){
            return R.reOk();
        }else{
            return R.error(log.getMsg());
        }
    }

    /**
     * 获取短信验证码
     * @param phone 手机号码
     * @return
     */
    @RequestMapping(value = "/getCode")
    public R getSmsCode(String phone) {
        String code = redisUtils.get(ConfigConstant.PHONE + phone);
        return R.reOk(code);
    }

    /**
     * 忘记密码
     *
     * @param phone      手机号
     * @param code       验证码
     * @param password   密码
     * @param rePassword 二次密码
     * @return
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public R forgetPassword(String phone, String code, String password, String rePassword) {

        if (StringUtils.isEmpty(phone) || !PhoneCodeVer.isPhoneNum(phone)) {
            return R.reError("手机号码有误");
        }
        if (StringUtils.isEmpty(password)) {
            return R.reError("密码不能为空!");
        }
        Map<String, Object> map = userInfoService.getUserInfoByPhone(phone);
        if (map == null) {
            return R.reError("手机号码未注册用户");
        }
        if (StringUtils.isEmpty(code) || !code.equals(redisUtils.get(ConfigConstant.PHONE + phone))) {
            return R.reError("验证码有误");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId((Long) map.get("userId"));
        userInfo.setPassword(MD5Util.MD5Encode(password, "utf-8"));
        userInfoService.updateUserInfo(userInfo, "");
        return R.reOk();
    }

    /**
     * @api {POST} /account/qd/updatePassword  修改密码
     * @apiGroup login
     * @apiParam {String} password 旧密码
     * @apiParam {String} newPassword 新密码
     * @apiParam {String} rePassword 确认新密码
     * @apiSuccessExample {json} 成功的响应
     */
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    public R updatePwd(String password, String newPassword, String rePassword, HttpServletRequest req) {
        UserInfoVo userInfo = getUserVo(req);
        if (userInfo == null) {
            return R.error(CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode(), "用户未登录");
        }
        if (!(userInfo.getPassword().equalsIgnoreCase(MD5Util.MD5Encode(password, "utf-8")))) {
            return R.error("旧密码错误");
        }
        if (StringUtils.isBlank(newPassword)) {
            return R.error("新密码不能为空");
        }
        if (!(newPassword.equalsIgnoreCase(rePassword))) {
            return R.error("两次密码不一致");
        }
        UserInfo users = new UserInfo();
        users.setUserId(userInfo.getUserId());
        users.setPassword(MD5Util.MD5Encode(newPassword, "utf-8"));
        userInfoService.updateUserInfo(users,"");
        return R.reOk();
    }

    /**
     * 修改个人信息（头像，昵称，姓名, 出生日期，身高）
     *
     * @param userInfo
     * @param req
     * @return
     */
    @RequestMapping("/updateUserInfo")
    public R updateUserInfo(UserInfo userInfo, String code, HttpServletRequest req) {
        UserInfoVo userVo = getUserVo(req);
        if (userVo == null) {
            return R.error(CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode(), "用户未登录");
        }
        userInfo.setUserId(userVo.getUserId());

        R  r = userInfoService.updateUserInfo(userInfo, code);
        return r;
    }

    /**
     * 查找用户信息
     *
     * @return
     */
    @RequestMapping(value = "/findUserInfo", method = RequestMethod.POST)
    public R findUserInfo(HttpServletRequest req) {
        UserInfoVo userVo = getUserVo(req);
        if (userVo == null) {
            return R.error(CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode(), "用户未登录");
        }
        Map<String, Object> map = this.userInfoService.findUserInfoById(getUserId(req));
        if (null == map) {
            return R.reError("用户不存在");
        }
        return R.reOk(map);
    }

    /**
     * 微信登录
     *
     * @return
     */
    @RequestMapping(value = "/wxLogin", method = RequestMethod.POST)
    public R wxLogin(UserInfo userInfo, HttpServletRequest req) {
        logger.info("用户进入微信登录..................");
        //ValidatorUtils.validateEntity(userInfo, LoginGroup.class);
        JSONObject obj = this.userInfoService.wxLogin(userInfo, req);
        if (obj.containsKey("userInfo")) {
            req.getSession().setAttribute(ConfigConstant.ACCOUNT, obj.get("userInfo"));
            return R.reOk(obj);
        }
        return R.reError(CodeAndMsg.ERROR_USER_LOGIN);
    }

    /**
     * 小程序登录
     *
     * @return
     */
    @RequestMapping(value = "/proLogin", method = RequestMethod.POST)
    public R proLogin(String wxCode, HttpServletRequest req) {
        logger.info("用户进入微信小程序登录..................");
        //ValidatorUtils.validateEntity(userInfo, LoginGroup.class);
        JSONObject obj = this.userInfoService.proLogin(wxCode, req);
        if (obj.containsKey("userInfo")) {
            //req.getSession().setAttribute(ConfigConstant.ACCOUNT, obj.get("userInfo"));
            return R.reOk(obj);
        }
        return R.reError(CodeAndMsg.ERROR_USER_LOGIN);
    }

    /**
     * 微信小程序绑定手机号
     * @param wxCode
     * @param request
     * @return
     * @throws Exception
     */
    //@Login
    @RequestMapping(value = "proPhone")
    public R proPhone(String wxCode, HttpServletRequest req) throws Exception {        
        if (StringUtils.isBlank(wxCode)) {
            return R.reError("code不能为空");
        }
        UserInfoVo userVo = getUserVo(req);
        JSONObject obj = GetWeiXinCode.getUserPhoneNumber(wxCode);
        String phone = null;
        if(obj != null && obj.containsKey("phone_info")) {
        	phone = obj.getJSONObject("phone_info").getString("purePhoneNumber");
        }
        if(StringUtils.isBlank(phone)) {
        	return R.reError("授权手机号失败");
        }
        if(StringUtils.isNotBlank(userVo.getPhone()) && !phone.equals(userVo.getPhone())) {
        	return R.reError("登录异常，请联系客服处理");
        }
        UserInfo sameUser = this.userInfoService.querySamePhoneUser(phone);
        logger.info(phone + " => " + GsonUtil.toJson(userVo) + " => " + GsonUtil.toJson(sameUser));
        if(sameUser != null && !sameUser.getUserId().equals(userVo.getUserId())) {
        	return R.reError("该手机号已绑定其它微信，请联系客服处理");
        }
                
        UserInfo userInfo = new UserInfo();
        if(StringUtils.isBlank(userVo.getNickname())) {
        	userInfo.setNickname("SL" + phone.substring(phone.length() - 4));
        }
        userInfo.setPhone(phone);
        userInfo.setUserId(userVo.getUserId());
        this.userInfoService.updateUserAccount(userInfo);
        return R.reOk(obj);
    }

    /**
     * 支付宝登录
     *
     * @return
     */
    @RequestMapping(value = "/zfbLogin", method = RequestMethod.POST)
    public R zfbLogin(String authCode, String zfbUserId, String deviceToken, HttpServletRequest req) {
        logger.info("用户进入支付宝登录----authCode--->" + authCode + "---zfbUserId--->" + zfbUserId);
        if (StringUtils.isBlank(authCode) || StringUtils.isBlank(zfbUserId)) {
            return R.reError("authCode为空或者zfbUserId为空");
        }
        JSONObject obj = this.userInfoService.zfbLogin(authCode, zfbUserId, deviceToken, req);
        if (obj.containsKey("userInfo")) {
            req.getSession().setAttribute(ConfigConstant.ACCOUNT, obj.get("userInfo"));
            return R.reOk(obj);
        }
        return R.reError(CodeAndMsg.ERROR_USER_LOGIN);
    }

    /**
     * 苹果登录
     * @param identityToken
     * @param userID
     * @param request
     * @return
     */
    @RequestMapping(value = "/appleLogin")
    public R appleLogin(String identityToken, String userID, String email,
                        String fullName, String deviceToken, HttpServletRequest request) {
        logger.info("苹果授权登录..................{}",userID);
        try {
            //验证identityToken
            if(!AppleUtil.verify(identityToken)){
                return R.reError(CodeAndMsg.VALIDATION_ERROR);
            }
            //对identityToken解码
            JSONObject json = AppleUtil.parserIdentityToken(identityToken);
            if(json == null){
                return R.reError(CodeAndMsg.VALIDATION_CODE_ERROR);
            }
            logger.info("identityToken验证成功后：{}", json);
            if (StringUtils.isBlank(email)) {
                email = json.getString("email");
            }
            JSONObject obj = this.userInfoService.appleLogin(userID, email, fullName, deviceToken, request);
            if (obj.containsKey("userInfo")) {
                request.getSession().setAttribute(ConfigConstant.ACCOUNT, obj.get("userInfo"));
                return R.reOk(obj);
            }
            return R.reError(CodeAndMsg.ERROR_USER_LOGIN);
        }catch (Exception e){
            logger.error("app wxLogin error:" + e.getMessage(),e);
            return R.reError("苹果授权系统错误");
        }

    }

    /**
     * 返回支付宝私钥跟pid
     *
     * @return
     */
    @RequestMapping(value = "/getPrivateKeyAndPid", method = RequestMethod.POST)
    public R getPrivateKeyAndPid(HttpServletRequest req) {
        Map<String, Object> map = new HashMap<>();
        map.put("privateKey", MyConfig.ZFB_PRIVATE_KEY);
        map.put("pid", MyConfig.ZFB_PID);

        //下面是获取到autoInfo
        String enCodesign = null;
        String authInfo = null;
        try {
            String content = "apiname=com.alipay.account.auth&app_id="
                    + MyConfig.ZFB_APPID + "&app_name=mc&auth_type=AUTHACCOUNT&biz_type=openservice" +
                    "&method=alipay.open.auth.sdk.code.get&pid=" + MyConfig.ZFB_PID + "&product_id=APP_FAST_LOGIN&scope=kuaijie&target_id="
                    + System.currentTimeMillis() + "&sign_type=RSA2";
            String sign = AlipaySignature.rsaSign(content, MyConfig.ZFB_PRIVATE_KEY, MyConfig.ZFB_CHARSET, MyConfig.ZFB_SIGN_TYPE);
            enCodesign = URLEncoder.encode(sign, "UTF-8");
            authInfo = content + "&sign=" + enCodesign;
        } catch (Exception e) {
            logger.error("支付宝返回autoInfo 报错：", e);
        }

        return R.reOk(authInfo);
    }

    /**
     * 微信登录
     *
     * @return
     */
    @RequestMapping(value = "/h5Login", method = RequestMethod.POST)
    public R h5Login(String wxCode, HttpServletRequest req) {
        logger.info("用户进入H5微信登录.............");
        if (StringUtils.isEmpty(wxCode)) {
            return R.reError("wxCode is wrong,请与管理员联系");
        }
        JSONObject obj = this.userInfoService.h5Login(wxCode, req);
        if (obj.containsKey("userInfo")) {
            req.getSession().setAttribute(ConfigConstant.ACCOUNT, obj.get("userInfo"));
            return R.reOk(obj);
        }
        return R.reError(CodeAndMsg.ERROR_USER_LOGIN);
    }


    /**
     * 维塑获取我们的接口凭证
     *
     * @param visid
     * @param secret
     * @return
     */
    @RequestMapping("v1/token")
    public R getVisToken(String visid, String secret) {
        logger.info("++++++++++维塑请求我们的接口凭证+++++++++");
        //此处需要维塑提供
        if (!visid.equals("") && secret.equals("")) {
            R r = new R();
            r.put("code", 40001);
            return r;
        }
        if (redisUtils.get("toVisToken") == null) {
            String token = generateToken();
            redisUtils.set("toVisToken", token, 60 * 60 * 2);
        }

        R r = new R();
        r.put("expires_in", 60 * 60 * 2);
        r.put("token", redisUtils.get("toVisToken"));
        r.put("code", 0);
        logger.info("++++++++++成功获取接口凭证+++++++++");
        return r;
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 维塑手环登录
     *
     * @return
     */
    @RequestMapping("/v1/thirdLogin")
    public Map thirdLogin(String scanid, String deviceid, String cardid, String token) {

        logger.info("+++scanid:" + scanid + "deviceid:" + deviceid + "cardid:" + cardid + "token:" + token);

        R r = new R();
       /* if (!token.equalsIgnoreCase(redisUtils.get("toVisToken"))) {
            logger.info("weisuo token验证失败+++++++++++++++++");
            r.put("code", 40002);
            return r;
        }*/

        //token验证通过，通过cardid查询到我们系统用户id
        UserInfo userInfo = userInfoService.queryUserIdByCardid(cardid);

        if (userInfo == null) {
            //该手环未绑定用户
            r.put("code", 40004);
            logger.info("-----------该手环未绑定用户-------");
            return r;
        }
        //add by 20190118 to:判断该手环是否禁用如果已禁用返回error
        try {
            int forbidden = userInfoService.queryIsForbiddenStatus(userInfo.getUserId(), cardid);
            if(forbidden > 0){
                logger.info("++++++++++++该用户被禁用了++++++++++++++++");
                r.put("code", 40020);      //权限不足
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //add end
        if (redisUtils.get("visToken") == null) {
            getvisToken(ConfigConstant.VISID, ConfigConstant.SECRET);
        }
        //调用维塑用户信息绑定（维塑提供）
        Map<String, Object> res = userInfoService.userDataBind(userInfo, scanid, deviceid, cardid, token);
        logger.info("++++++++++++++res++++++++++"+res);
        if (res.get("code").toString().equals("40002")||res.get("code").toString().equals("40020")) {
            getvisToken(ConfigConstant.VISID, ConfigConstant.SECRET);
            res = userInfoService.userDataBind(userInfo, scanid, deviceid, cardid, token);
            if (res.get("code").toString().equalsIgnoreCase("0")) {
                logger.info("+++++++++绑定成功+++++++++++++++");
            }

        }
        if (res.get("code").toString().equals("0")){
            r.put("code", 0);
            return r;
        }
        res.put("code",40005);
        return res;
    }


    /**
     * 维塑回调通知
     *
     * @param // String deviceId,String userId,String scanId,String time,String token,Integer status,Integer type,String msg,Object data
     * @return ?token= 【VISTOKEN】&id=【ID】
     */
//    @PostMapping("v1/notifyResult")
//    public R visNotifyResult(@RequestBody JSONObject paramsObj) {
//        logger.info("+++++++++++++++++++++++++param==" + paramsObj);
//        if (paramsObj == null) {
//            logger.info("-------------回调通知参数为空----------");
//        }
//        logger.info("+++++++++++status:" + paramsObj.get("status"));
//        if (paramsObj.get("status").toString().equalsIgnoreCase("1")) {
//            if (redisUtils.get("visToken") == null) {
//                getvisToken(ConfigConstant.VISID, ConfigConstant.SECRET);
//            }
//            //获取维塑用户标识id（维塑提供）
//            String result = SendPushPost.sendGet(ConfigConstant.GET_VISUSERID_URL + "?token=" + redisUtils.get("visToken") + "&id=" + paramsObj.get("userId").toString());
//            JSONObject jsonObject = JSONObject.parseObject(result);
//            if (!jsonObject.get("code").toString().equalsIgnoreCase("0")) {
//                logger.info("------------code:" + jsonObject.get("code"));
//                return R.reError("用户不存在");
//            }
//            //维塑内部用户id
//            Long vid = Long.parseLong(jsonObject.get("vid").toString());
//            logger.info("+++++++++维塑内部用户id+++" + vid);
//            //获取体成分数据（维塑提供）URL:https://api.visbodyfit.com:30000/v1/bodys/info?token=【VISTOKEN】&scanid= 【xxx】&vid=【VID】&date=【20170608】
//
//            String date = DateUtils.formatYYYMMDD(new Date());
//            logger.info("date=====" + date);
//
//            //体测结果
//            String bodyInfo = SendPushPost.sendGet(ConfigConstant.GETVIS_BODYSINFO_URL + "?token=" + redisUtils.get("visToken") + "&scanid=" + paramsObj.get("scanId").toString() + "&vid=" + vid + "&date=" + date);
//            JSONObject bodyInfoMap = JSONObject.parseObject(bodyInfo);
//            logger.info("体侧信息++++++++++++++++++++++" + bodyInfo);
//
//            //围度信息
//            String bodyDimension = SendPushPost.sendGet(ConfigConstant.BODY_DIMENSION_URL + "?token=" + redisUtils.get("visToken") + "&scanid=" + paramsObj.get("scanId").toString() + "&vid=" + vid + "&date=" + date);
//            JSONObject bodyDimensionMap = JSONObject.parseObject(bodyDimension);
//            logger.info("围度信息++++++++++++++++++++++" + bodyDimension);
//            logger.info("bodyDimensiontoken=" + redisUtils.get("visToken"));
//
//
//            bodyInfoMap.put("userId", Long.parseLong(paramsObj.get("userId").toString()));
//            bodyInfoMap.put("vid", vid);
//
//            bodyDimensionMap.put("userId", Long.parseLong(paramsObj.get("userId").toString()));
//            bodyDimensionMap.put("vid", vid);
//
//
//            //保存或更新围度信息
//            int saveDimesion = bodyInfoService.saveOrUpdateBodyDimension(bodyDimensionMap);
//            if (saveDimesion > 0) {
//                logger.info("++++++++++保存拉取的围度信息成功+++++++++");
//            }
//
//            //保存拉取的体侧数据
//            int svaeResult = bodyInfoService.saveOrUpdateBodyInfo(bodyInfoMap);
//            if (svaeResult > 0) {
//                logger.info("++++++++++保存拉取的体侧数据成功+++++++++");
//            }
//
//            //体态评估信息
//            String getvisToken = getStatusToken(ConfigConstant.VISID, ConfigConstant.SECRET);
//            String bodyStatus = SendPushPost.sendGet(ConfigConstant.BODY_STATUS_URL + "?token=" + getvisToken + "&scanid=" + paramsObj.get("scanId").toString() + "&vid=" + vid + "&date=" + date);
//
//            JSONObject bodyStatusMap = JSONObject.parseObject(bodyStatus);
//            logger.info("bodyStatustoken=" + redisUtils.get("visToken"));
//            logger.info("体态评估信息++++++++++++++++++++++" + bodyStatus);
//
//            if ("0".equals(bodyStatusMap.get("code").toString())){
//                bodyStatusMap.put("userId", Long.parseLong(paramsObj.get("userId").toString()));
//                bodyStatusMap.put("vid", vid);
//
//                //保存拉取的体态信息
//                int saveResultBodyStatus = bodyInfoService.saveOrUpdateBodyStatus(bodyStatusMap);
//                if (saveResultBodyStatus > 0) {
//                    logger.info("++++++++++保存拉取的体态信息成功+++++++++");
//                }
//                logger.info("获取获取维塑用户标识id+++++++++" + jsonObject.get("code").toString());
//            }
//
//
//
//        }
//        R r = new R();
//        r.put("code", 0);
//        return r;
//    }

//    /**
//     * 维塑回调通知
//     *
//     * @param // String deviceId,String userId,String scanId,String time,String token,Integer status,Integer type,String msg,Object data
//     * @return ?token= 【VISTOKEN】&id=【ID】
//     */
//    @PostMapping("v1/rproNotifyResult")
//    public R rproNotifyResult(@RequestBody JSONObject paramsObj) {
//        R r = new R();
//        r.put("code", 0);
//        logger.info("3D智能提测精灵-推送合成通知消息param=" + paramsObj);
//        if (paramsObj == null) {
//            logger.info("-------------回调通知参数为空----------");
//        }
//        JSONObject actionStatus = paramsObj.getJSONObject("action_status");
//        logger.info("合成状态信息:" + actionStatus);
//        if(actionStatus == null){
//            return r;
//        }
//
//        if (redisUtils.get(ConfigConstant.REDIS_KEY_RPRO) == null) {
//            getRpro3Token(ConfigConstant.RPRO_KEY, ConfigConstant.RPRO_SECRET);
//        }
//        String token = redisUtils.get(ConfigConstant.REDIS_KEY_RPRO);
//        String scanId = paramsObj.get("scan_id").toString();
//        String phone = paramsObj.getJSONObject("data").getString("phone");
//        Map<String, Object> userMap = userInfoService.getUserInfoByPhone(phone);
//        Long userId = Long.parseLong(userMap.getOrDefault("userId", "0").toString());
//        Long vid =  Long.parseLong("0");
//        Long gymEngineId = Long.parseLong("47");
//
//        //获取维塑用户标识id（维塑提供）
////            String result = SendPushPost.sendGet(ConfigConstant.GET_VISUSERID_URL + "?token=" + redisUtils.get("visToken") + "&id=" + paramsObj.get("userId").toString());
////            JSONObject jsonObject = JSONObject.parseObject(result);
////            if (!jsonObject.get("code").toString().equalsIgnoreCase("0")) {
////                logger.info("------------code:" + jsonObject.get("code"));
////                return R.reError("用户不存在");
////            }
////            //维塑内部用户id
////            Long vid = Long.parseLong(jsonObject.get("vid").toString());
////            logger.info("+++++++++维塑内部用户id+++" + vid);
//
//        //获取提测数据（维塑提供）
//        //体测成分信息 URL:http://api.rpro3.visbody.com/v1/measure/mass?token=【VISTOKEN】&scanid= 【xxx】
//        int saveResult = 0;
//        if("1".equalsIgnoreCase(actionStatus.getString("bia_status")) ){
//            String bodyInfo = SendPushPost.sendGet(ConfigConstant.RPRO_MEASURE_MASS + "?token=" + token + "&scan_id=" + scanId);
//            logger.info("体测信息：" + bodyInfo);
//            JSONObject bodyInfoResult = JSONObject.parseObject(bodyInfo);
//            if(bodyInfoResult.getString("code").equalsIgnoreCase("0")){
//                JSONObject bodyinfoData = bodyInfoService.bodyinfoFormatData(bodyInfoResult.getJSONObject("data"));
//                bodyinfoData.put("userId", userId);
//                bodyinfoData.put("vid", vid);
//                bodyinfoData.put("ScanId", scanId);
//                bodyinfoData.put("gymEngineId", gymEngineId);    //3D智能提测精灵设备ID
//
//                saveResult = bodyInfoService.saveOrUpdateBodyInfo(bodyinfoData);
//                if (saveResult > 0) {
//                    logger.info("++++++++++保存拉取的体测成分数据成功+++++++++");
//                }
//            }
//        }
//
//        //围度信息 URL:http://api.rpro3.visbody.com/v1/measure/girth
//        int saveDimesion = 0;
//        if("1".equalsIgnoreCase(actionStatus.getString("girth_status")) ){
//            String bodyDimension = SendPushPost.sendGet(ConfigConstant.RPRO_MEASURE_GIRTH + "?token=" + token + "&scan_id=" + scanId);
//            logger.info("围度信息: " + bodyDimension);
//            JSONObject bodyDimensionResult = JSONObject.parseObject(bodyDimension);
//            if(bodyDimensionResult.getString("code").equalsIgnoreCase("0")){
//                JSONObject bodyDimensData = bodyInfoService.bodyDimensFormatData(bodyDimensionResult.getJSONObject("data"));
//                bodyDimensData.put("userId", userId);
//                bodyDimensData.put("vid", vid);
//                bodyDimensData.put("ScanId", scanId);
//                bodyDimensData.put("gymEngineId", gymEngineId);  //3D智能提测精灵设备ID
//
//                saveDimesion = bodyInfoService.saveOrUpdateBodyDimension(bodyDimensData);
//                if (saveDimesion > 0) {
//                    logger.info("++++++++++保存拉取的围度信息成功+++++++++");
//                }
//            }
//        }
//
//
//        //体态评估信息 URL:http://api.rpro3.visbody.com/v1/shape/points
//        int saveResultBodyShape = 0;
//        if("1".equalsIgnoreCase(actionStatus.getString("eval_status"))){
//            String bodyShape = SendPushPost.sendGet(ConfigConstant.RPRO_SHAPE_POINTS + "?token=" + token + "&scan_id=" + scanId );
//            logger.info("体态评估信息++++++++++++++++++++++" + bodyShape);
//            JSONObject bodyShapeResult = JSONObject.parseObject(bodyShape);
//            JSONObject bodyShapeData = bodyInfoService.bodyShapeFormatData(bodyShapeResult.getJSONObject("data"));
//            bodyShapeData.put("userId", userId);
//            bodyShapeData.put("ScanId", scanId);
//            bodyShapeData.put("gymEngineId", gymEngineId);  //3D智能提测精灵设备ID
//
//            saveResultBodyShape = bodyInfoService.saveOrUpdateBodyShape(bodyShapeData);
//            if (saveResultBodyShape > 0) {
//                logger.info("++++++++++保存拉取的体态评估成功+++++++++");
//            }
//        }
//
//        return r;
//    }


    public String getvisToken(String visid, String secret) {
        Map<String, Object> param = new HashMap<>();
        param.put("visid", visid);
        param.put("secret", secret);
        String result = SendPushPost.sendGet(ConfigConstant.GET_VISTOKEN + "?visid=" + visid + "&secret=" + secret);
        JSONObject resMap = JSONObject.parseObject(result);
        if (resMap.get("code").toString().equalsIgnoreCase("0")) {
            redisUtils.set("visToken", resMap.get("token").toString(), Long.parseLong(resMap.get("expires_in").toString()));
            logger.info("getVisToken:==" + resMap.get("token").toString());
            return resMap.get("token").toString();
        }
        return resMap.get("token").toString();
    }

    //获取体态接口token
    public String getStatusToken(String visid, String secret) {
        Map<String, Object> param = new HashMap<>();
        param.put("visid", visid);
        param.put("secret", secret);
        String result = SendPushPost.sendGet(ConfigConstant.GET_StATUS_VISTOKEN + "?visid=" + visid + "&secret=" + secret);
        JSONObject resMap = JSONObject.parseObject(result);
        if (resMap.get("code").toString().equalsIgnoreCase("0")) {
            redisUtils.set("visBodyStatusToken", resMap.get("token").toString(), Long.parseLong(resMap.get("expires_in").toString()));
            logger.info("getVisBodyStatusToken:==" + resMap.get("token").toString());
            return resMap.get("token").toString();
        }
        return resMap.get("token").toString();
    }


    /**
     * 前端调用 体成分数据
     *
     * @param request
     * @return
     */
    @RequestMapping("/bodyInfo")
    public R getBodyInfo(HttpServletRequest request) {
        Long userId = getUserId(request);
        /*Map<String,Object> param = new HashMap<>();*/

//        String visToken = getvisToken(ConfigConstant.VISID, ConfigConstant.SECRET);
        Map<String, Object> bodyInfoMap = bodyInfoService.queryBodyInfoByUserId(userId);
        //从scanid中提取出deviceId，再根据deviceId生产token
        String[] scanids = bodyInfoMap.get("scanid").toString().split("-");
        String deviceId = scanids[0];
        String token = getVisbodyTokenByCache(deviceId);

        if(bodyInfoMap != null){
            if(bodyInfoMap.get("gymEngineId").toString().equals("46") || bodyInfoMap.get("gymEngineId").toString().equals("101")){
//                Date date = (Date) bodyInfoMap.get("createTime");
//                String result = SendPushPost.sendGet(ConfigConstant.MODELS_INFO_URL + "?token=" + visToken.toString() + "&scanid=" + bodyInfoMap.get("scanid").toString() + "&vid=" + bodyInfoMap.get("vid").toString() + "&date=" + DateUtils.formatYYYMMDD(date));
//                JSONObject resultMap = JSONObject.parseObject(result);
//                if (resultMap.get("code").toString().equalsIgnoreCase("0")) {
//                    bodyInfoMap.put("model_url", resultMap.get("model_url"));
//                    bodyInfoMap.put("expires_in", resultMap.get("expires_in"));
//                    saveUrlAs(resultMap.get("model_url").toString(), request.getServletContext().getRealPath("/statics/model/FitNormalModel"), bodyInfoMap.get("scanid") + ".obj", "GET");
//                }

                String rangeResult = SendPushPost.sendGet(ConfigConstant.RANGE_INFO_URL + "?token=" + token + "&scanid=" + bodyInfoMap.get("scanid").toString() + "&vid=" + bodyInfoMap.get("vid").toString());
                JSONObject rangeResultMap = JSONObject.parseObject(rangeResult);
                for (String item:rangeResultMap.keySet()){
                    if (item.equals("code") || item.equals("CreateTime") || item.equals("ScanId")){
                        continue;
                    }
                    Map<String,Object> map = (Map) rangeResultMap.get(item);
                    for (String name:map.keySet()){
                        String s = MathUtil.fromStringToOnePoint(map.get(name).toString());
                        map.put(name,s);
                    }
                }
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("dataInfo", bodyInfoMap);
                resultData.put("rangeInfo", rangeResultMap);
                return R.reOk(resultData);
            }else if(bodyInfoMap.get("gymEngineId").toString().equals("47") || bodyInfoMap.get("gymEngineId").toString().equals("102")){
                Map<String, Object> resultData = new HashMap<>();
                JSONObject rangeResultMap = JSON.parseObject(bodyInfoMap.get("result").toString());
                resultData.put("dataInfo", bodyInfoMap);
                resultData.put("rangeInfo", rangeResultMap);
                return R.reOk(resultData);
            }
        }

        return R.reOk(new JSONObject());
    }

    /**
     * 前端接口-体态信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/bodyStatus")
    public R getBodyStatus(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Map<String, Object>> list = bodyInfoService.queryBodyBodyStatusByUserId(userId);
        List<Map<String, Object>> list2 = bodyInfoService.queryBodyShapeLastRecord(userId);
        System.out.println("新体态数据" + list2 );

        Map<String, Object> bodyStatusMap = new HashMap<>();

        // 3D智能提测精灵
        if(!list2.isEmpty()){
            Map<String, Object> bodyStatusV2Map = list2.get(0);
            bodyStatusMap.put("v2", JSON.parseObject(bodyStatusV2Map.get("result").toString()) );
//            bodyStatusMap.put("v2", bodyStatusV2Map);
        }else if (!list.isEmpty()) {
//            Map<String, Object> bodyStatusMap = list.get(0);
            bodyStatusMap = list.get(0);
            String statusToken = getStatusToken(ConfigConstant.VISID, ConfigConstant.SECRET);

            String resultStr = bodyStatusMap.get("results") + "";
            String resultStrNew = resultStr.substring(1, resultStr.length() - 1);
            String s = resultStrNew.replaceAll("\"", "");
            String demoArray[] = s.split(",");
            List<String> resultList = Arrays.asList(demoArray);

            bodyStatusMap.put("results", resultList);
            Date date = (Date) bodyStatusMap.get("createTime");
            String result = SendPushPost.sendGet(ConfigConstant.MODELS_BODYSTATUS_URL + "?token=" + statusToken.toString() + "&scanid=" + bodyStatusMap.get("scanId").toString() + "&vid=" + bodyStatusMap.get("vid").toString() + "&date=" + DateUtils.formatYYYMMDD(date));
            JSONObject resultMap = JSONObject.parseObject(result);
            if (resultMap.get("code").toString().equalsIgnoreCase("0")) {
                saveUrlAs(resultMap.get("model_url").toString(), request.getServletContext().getRealPath("/statics/model/BodyStatusModel"), bodyStatusMap.get("scanId") + ".obj", "GET");
            }
//            return R.reOk(bodyStatusMap);
        }


        return R.reOk(bodyStatusMap);
    }


    /**
     * 前端接口-围度信息
     *
     * @return
     */
    @RequestMapping("/bodyDimension")
    public R getBodyDimension(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Map<String, Object>> bodyDimensionList = bodyInfoService.queryBodyDimensionInfo(userId);
        Map<String, Object> scanIdMap = bodyInfoService.queryBodyDimensionScanId(userId);
        if (bodyDimensionList == null) {
            return R.reOk(new HashMap<>());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bodyDimensionInfo", bodyDimensionList);
        jsonObject.putAll(scanIdMap);
        return R.reOk(jsonObject);
    }


    /**
     * 趋势对比
     *
     * @return
     */
    @RequestMapping("trendComparison")
    public R trendComparison(HttpServletRequest request, String startTime, String endTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId(request));
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        List<Map<String, Object>> bodyInfoTrend = bodyInfoService.queryBodyInfoBetweenTime(paramMap);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bodyInfoTrend", bodyInfoTrend);
        return R.reOk(jsonObject);

    }

    /**
     * @param filePath 文件将要保存的目录
     * @param method   请求方法，包括POST和GET
     * @param url      请求的路径
     * @return
     * @从制定URL下载文件并保存到指定目录
     */

    public static File saveUrlAs(String url, String filePath, String fileName, String method) {
        //System.out.println("fileName---->"+filePath);
        //创建不同的文件夹目录
        File file = new File(filePath);
        //判断文件夹是否存在
        if (!file.exists()) {
            //如果文件夹不存在，则创建新的的文件夹
            file.mkdirs();
        }
        FileOutputStream fileOut = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            // 建立链接
            URL httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            //以Post方式提交表单，默认get方式
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            //获取网络输入流
            inputStream = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            //判断文件的保存路径后面是否以/结尾
            if (!filePath.endsWith("/")) {

                filePath += "/";

            }
            //写入到文件（注意文件保存路径的后面一定要加上文件的名称）
            fileOut = new FileOutputStream(filePath + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fileOut);

            byte[] buf = new byte[4096];
            int length = bis.read(buf);
            //保存文件
            while (length != -1) {
                bos.write(buf, 0, length);
                length = bis.read(buf);
            }
            bos.close();
            bis.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("抛出异常！！");
        }

        return file;
    }


    @RequestMapping("/rfid")
    public R weFitLogin(String rfid_number, String id, String sign, Integer timestamp, String appkey) {
        logger.info("rfid_number==" + rfid_number + "--" + "id==" + id + "--appkey==" + appkey);
        R r = new R();
        //查询rfid是否绑定会员
        if (StringUtils.isBlank(rfid_number)) {
            r.put("status", 40);
            r.put("message", "RFID号码为空");
            return r;
        }
        UserInfo userInfo = userInfoService.queryUserIdByCardid(rfid_number);
        if (userInfo == null) {
            r.put("status", 41);
            r.put("message", "RFID号码未绑定会员");
            return r;
        }
        //add by 20190118 to:判断该手环是否禁用如果已禁用返回error
        try {
            int forbidden = userInfoService.queryIsForbiddenStatus(userInfo.getUserId(), rfid_number);
            if(forbidden > 0){
                r.put("status", 45);
                r.put("message", "没有权限使用该设备");
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //add end
        //调用第三方开启设备接口

        Date now = new Date();
        String strTime = String.valueOf(now.getTime());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appkey", ConfigConstant.WEFIT_APPKEY);
        paramMap.put("timestamp", now.getTime() + "");
        paramMap.put("id", id);
        paramMap.put("user_id", userInfo.getUserId() + "");
        paramMap.put("order_number", OrderNoGenerator.getOrderIdByTime());
        StringBuilder sb = new StringBuilder();
        String append = sb.append(ConfigConstant.WEFIT_APPKEY).append(ConfigConstant.WEFIT_PRIVATE_APPKEY).append(strTime).toString();
        // String sign1 = new StringBuilder(ConfigConstant.WEFIT_APPKEY + ConfigConstant.WEFIT_PRIVATE_APPKEY + new Date()).toString();
        paramMap.put("sign", MD5Util.MD5Encode(append, "utf-8"));
        String result = SendPushPost.sendPost(ConfigConstant.WEFIT_OPEN_DEVICE_URL, paramMap);
        JSONObject jsonObject = JSONObject.parseObject(result);
        logger.info("开启设备信息+++" + jsonObject);
        r.put("status", "0");
        r.put("message", "成功");
        return r;
    }


    /**
     * 运动设备数据上传接口
     *
     * @param timestamp
     * @param device_id
     * @param user_id
     * @param order_number
     * @param time
     * @param calorie
     * @param distance
     * @param times
     * @return
     */
    @RequestMapping("/uploadSportData")
    public R uploadSportData(String timestamp, String device_id, Long user_id, String order_number, Integer time, Integer calorie, Integer distance, Integer times, String name, String start_time) {

        logger.info("上传过来的运动数据++" + timestamp + "++" + user_id + "++" + time + "++" + calorie + "++" + distance + "++" + times + "++" + name + "++" + start_time);
        BigDecimal dbDistance = MathUtil.fromIntToBigDecimal(distance);
        BigDecimal dbCalorie = MathUtil.fromIntToBigDecimal(calorie);
        UserSportDeviceRecord userSportDeviceRecord = new UserSportDeviceRecord();
        userSportDeviceRecord.setSportTime(time);
        userSportDeviceRecord.setCalorie(dbCalorie);
        userSportDeviceRecord.setDeviceId(device_id);
        userSportDeviceRecord.setDeviceName(name);
        userSportDeviceRecord.setStartTime(new Date());
        userSportDeviceRecord.setDistance(dbDistance);
        userSportDeviceRecord.setUserId(user_id);
        userSportDeviceRecord.setCreateTime(new Date());
        int record = userInfoService.saveUserSportRecord(userSportDeviceRecord);
        if (record > 0) {
            logger.info(user_id + "运动信息保存成功");
            storeDeviceV2Service.setRidoDeviceState(device_id, 0, 0L);
            logger.info("更新设备运行状态、使用用户" + device_id);
        }
        R r = new R();
        r.put("status", 0);

        return R.reOk();
    }




    @RequestMapping("/userSportRecord")
    public R userSportRecord(@RequestParam Map<String,Object> map,HttpServletRequest request) {
        Long userId = getUserId(request);
        map.put("userId", userId);
        Query query = new Query(map);
        List<Map<String, Object>> list = userInfoService.queryUserRecordByUserId(query);
        int total = userInfoService.queryTotalSportRecord(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        Map<String, Object> sportTotal = userInfoService.queryUserSportTotal(query);
        if (sportTotal==null){
            sportTotal = new JSONObject();
            return R.reOk().put("page",page).put("sportTotal",sportTotal);
        }
        int cTotal = userInfoService.queryClassTotal(query);
        sportTotal.put("sportCount",total + cTotal);
        return R.reOk().put("page",page).put("sportTotal",sportTotal);
    }


    /**
     * 提供给第三方售货柜厂商
     * @param wristId
     * @param totalMoney
     * @param token
     * @return
     */
    @RequestMapping("/salesCounterLogin")
    public R salesCounterLogin(String wristId,BigDecimal totalMoney,String token){
        logger.info("售货柜登录++++++++++++++++++");
        R r = new R();
        if (!token.equals(MD5Util.MD5Encode("dlc_xiaohezi","utf-8"))){
            r.put("code",1001);
            r.put("msg","Token验证失败");
            return r;
        }
        UserInfo userInfo = userInfoService.queryUserIdByCardid(wristId);
        if (userInfo==null){
            r.put("code",1002);
            r.put("msg","手环(卡)未绑定会员");
            return r;
        }
        //查询用户是否余额不足
        BigDecimal userMoney = userInfoService.queryUserWalletByUserId(userInfo.getUserId());
        int i = totalMoney.compareTo(userMoney);
        if (i<0){
            r.put("code",1003);
            r.put("msg","余额不足");
            return r;
        }
        return R.reOk().put("tradeNo",OrderNoGenerator.getOrderIdByTime());
    }

    /**
     * 售货柜回调通知
     * @param tradeNo
     * @param status 0:出货成功  1：出货失败
     * @return
     */
    @RequestMapping("/salesCounterNotify")
    public R salesCounterNotify(String tradeNo,Integer status,String time,BigDecimal amount){
        logger.info("售货柜回调++++++++++++++++++");
        //根据是否出货成功生成订单 并扣款
        if (status==0){
            //出货成功，生成订单扣款

        }
        return R.reOk();
    }

    @RequestMapping("/getTicket")
    public JSONObject getTicket(HttpServletRequest request) throws Exception {
        String ticket = redisUtils.get("hzjsf_ticket");
        logger.info("ticket--->" + ticket);
        if (ticket == null || ticket.equals("")) {
            MyConfig config = new MyConfig();
            String accessToken = GetWeiXinCode.getAccessToken(config.getGzhAppID(), config.getGzhAppSecret());

            logger.info("accessToken--" + accessToken);
            JSONObject newTicket = GetWeiXinCode.getTicket(accessToken);
            return newTicket;
        } else {
            return JSONObject.parseObject(ticket);
        }
    }

    /**
     * 维塑绑定解绑设备
     * @param type
     * @return
     */
    @RequestMapping(value = "/unBind", method = RequestMethod.POST)
    public R unBind(String type){
        logger.info("维塑绑定解绑设备------"+type);   //2 纯手环对接
        if(StringUtils.isBlank(type)){
            return R.reError("类型为空了！！");
        }
        int tempType = Integer.valueOf(type);
        String url = "https://api.visbodyfit.com:30000/v1/thirdsync/setinfo";
        String token = getvisToken(ConfigConstant.VISID, ConfigConstant.SECRET);
        String getInfoUrl = ConfigConstant.GET_INFO_URL + "?token="+token + "&deviceid=01021805085027";
        String getInfo = SendPushPost.sendGet(getInfoUrl);
        logger.info("getInfo-----?"+getInfo);
        JSONObject obj = JSONObject.parseObject(getInfo);
        //Map<String, Object> map  = new HashMap<>();
        JSONObject param = new JSONObject();
        /*param.put("AccessKey", obj.get("AccessKey"));
        param.put("AccessSecret", obj.get("AccessSecret"));*/
        param.put("AccessKey", "vf5bd4086794f7d");
        param.put("AccessSecret", "54cbe0a17d501dd7bfcc0f4e112c5d96");
        param.put("CompanyName", "S-live运动");
        param.put("DeviceID", obj.get("DeviceID"));
        param.put("MethodType", tempType);
        param.put("URLLogin", "http://shilijsf.shilisports.com" + "/api/userInfo/v1/thirdLogin");
        //param.put("URLQrcode", fileServicePath+"/api/userInfo/v1/getQrCode");
        param.put("URLSync", "http://shilijsf.shilisports.com" +"/api/userInfo/v1/notifyResult");
        param.put("URLToken", "http://shilijsf.shilisports.com" +"/api/userInfo/v1/token");
        param.put("token", token);
        String result = SendPushPost.sendJsonPost(url, param.toString());
        return R.reOk(result);
    }


    /*** 3D智能提测精灵 - 相关接口 *****************************************************************/

    /**
     * 获取维塑API接口凭证
     * @param key
     * @param secret
     * @return
     */
//    public String getRpro3Token(String key, String secret) {
//        String result = SendPushPost.sendGet(ConfigConstant.GET_RPRO_TOKEN + "?key=" + key + "&secret=" + secret);
//        JSONObject resMap = JSONObject.parseObject(result);
//        if (resMap.get("code").toString().equalsIgnoreCase("0")) {
//            String token = resMap.getJSONObject("data").getString("token");
//            redisUtils.set(ConfigConstant.REDIS_KEY_RPRO, token, Long.parseLong(resMap.getJSONObject("data").get("expires_in").toString()));
//            logger.info("getRpro3Token:" + token);
//            return token;
//        }
//        return toString();
//    }

    /**
     * API对接设置-获取接口凭证(3D智能提测精灵)
     * @param key
     * @param secret
     * @return
     * tips：维塑获取我们的接口凭证
     */
//    @RequestMapping("v1/rproToken")
//    public R getRproToken(String key, String secret) {
//        String redisKey = ConfigConstant.REDIS_TOKEN_CHECK_KEY;
//        R r = new R();
//        logger.info("+++++++++维塑请求我们的接口凭证，参数：" + key +"/"+ secret);
//        if (!key.equals("") && secret.equals("")) {
//            r.put("code", 30001);
//            return r;
//        }else if(!key.equals(ConfigConstant.RPRO_KEY)){
//            r.put("code", 30002);
//            return r;
//        }else if(!secret.equals(ConfigConstant.RPRO_SECRET)){
//            r.put("code", 30003);
//            return r;
//        }
//        if (redisUtils.get(redisKey) == null) {
//            String token = generateToken();
//            redisUtils.set(redisKey, token, 60 * 60 * 2);
//        }
//
//        HashMap data = new HashMap();
//        data.put("token", redisUtils.get(redisKey));
//        data.put("expires_in", 60 * 60 * 2);
//        r.put("code", 0);
//        r.put("data", data);
//        logger.info("来自维塑的key/secret效验通过，返回token给维塑：" + redisUtils.get(redisKey) );
//        return r;
//    }

    /**
     * API对接设置-二维码请求地址
     * @param request
     * @return
     * tips：维塑获取我们的二维码地址，并将附带参数/scan_id、device_id
     */
//    @RequestMapping("v1/getQrCode")
//    public R getQrCode(HttpServletRequest request){
//        String scanId = request.getParameter("scan_id");
//        String deviceId = request.getParameter("device_id");
//        String token = request.getParameter("token");
//        String redisKey = ConfigConstant.REDIS_TOKEN_CHECK_KEY;
//        R r = new R();
//        logger.info("++++++++++获取体测结果绑定二维码，token=" + token +"scan_id="+ scanId +"device_id="+ deviceId);
//        logger.info("++++++++++正确token=" + redisUtils.get(redisKey) );
//
//        //将参数scan_id、device_id写入二维码中,等待APP扫码后将user_id与scan_id、device_id一起提交给维塑提供的用户绑定接口
//        String filename = "";
//        try {
//            String qrContent = "scan_id=" + scanId + "&device_id=" + deviceId;
//            filename = QrcodeUtil.generateQRCodeImage(qrContent, 350, 350, rpro3QrcodePath);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        String qrcodePath = "http://" + request.getServerName() + request.getContextPath() + "/statics/images/qrcode/" + filename;
//        System.out.println("二维码路径" + qrcodePath);
//
//        //此处需要维塑提供
//        if (token == null || redisUtils.get(redisKey) == null || !redisUtils.get(redisKey).equals(token) ) {
//            r.put("code", 30001);
//            r.put("error_msg", "ERROR_MSG");
//        }else{
//            HashMap data = new HashMap();
//            data.put("url", qrcodePath);
//            data.put("scan_id", scanId);
//            data.put("device_id", deviceId);
//            r.put("code", 0);
//            r.put("data", data);
//        }
//        return r;
//    }

    /**
     * 用户信息绑定
     * @param scan_id
     * @param device_id
     * @param user_id
     * @return
     */
//    @RequestMapping(value = "v1/userDataBind", method = RequestMethod.POST)
//    public R userDataBind(String scan_id, String device_id, String user_id){
//        R r = new R();
//        logger.info("++++++++++调用维塑用户信息绑定接口，scan_id=" + scan_id +"/device_id=" + device_id +"/user_id=" + user_id);
//        Map userinfo = userInfoService.findUserInfoById(Long.parseLong(user_id));
//        System.out.println("用户信息=" + userinfo);
//        Long proxyId = userInfoService.getDeviceProxyId(Long.parseLong(user_id));
//        System.out.println("用户设备proxyId=" + proxyId);
//        if (userinfo == null) {
//            r.put("code", 40001);
//            r.put("msg", "ERROR_MSG");
//        }else if(proxyId == null){
//            r.put("code", 40001);
//            r.put("msg", "非会员卡用户，请购卡后使用");
//        }else{
//            if(redisUtils.get(ConfigConstant.REDIS_KEY_RPRO) == null){
//                getRpro3Token(ConfigConstant.RPRO_KEY, ConfigConstant.RPRO_SECRET);
//            }
//            String third_uid = String.format("%08d", Integer.parseInt(user_id));
//            Map paramMap = new HashMap<>();
//            paramMap.put("scan_id", scan_id);
//            paramMap.put("device_id", device_id);
//            paramMap.put("third_uid", third_uid);
//            paramMap.put("mobile", userinfo.getOrDefault("phone", "").toString() );
//            paramMap.put("sex", Integer.parseInt(userinfo.getOrDefault("sex", "1").toString()) );
//            paramMap.put("height", Integer.parseInt(userinfo.getOrDefault("height", "0").toString()) );
//            String birthday = userinfo.getOrDefault("birthday", "0").toString().substring(0, 10);
//            paramMap.put("birthday", birthday);
//            paramMap.put("token", redisUtils.get(ConfigConstant.REDIS_KEY_RPRO) );
//            System.out.println("提交的用户信息转换" + JSONObject.toJSONString(paramMap) );
//
//            String result = HttpClientUtils.postJson(ConfigConstant.RPRO_DATABIND_URL, JSONObject.toJSONString(paramMap) );
//            logger.info("用户信息绑定返回结果：" + result);
//
//            JSONObject resultObject = JSONObject.parseObject(result);
//            if(resultObject.getString("code").equalsIgnoreCase("0")){
//                r.put("code", 0);
//                r.put("data", paramMap);
//                r.put("msg", "成功");
//            }else{
//                r.put("code", resultObject.getString("code"));
//                r.put("msg", resultObject.getString("error_msg"));
//            }
//        }
//        return r;
//    }


    /*** 第二版/多门店通用版 *************************************/

    /**
     * API对接设置-获取接口凭证(3D体型追踪仪)
     * @param params
     * @return
     * tips：
     * 1、维塑获取我们的接口凭证
     * 2、注意，两种不同设备传入的参数名不同，并且不同来源需要返回的数据格式不同（3D体型追踪仪/D智能体测精灵）
     * 例：
     * ?visid=vf5bd4086794f7d&secret=54cbe0a17d501dd7bfcc0f4e112c5d96
     * ?key=vfy1EkEPsup05eLB&secret=APcPyFdTRmY9hLuT7JfGZXROhEntFQPL
     */
    @RequestMapping("v1/visbody/token")
    public R putVisToken(@RequestParam Map params) {
        logger.info("+++++++++维塑请求我们的接口凭证，参数：" + params);
        String key = params.getOrDefault("key", "").toString();
        String secret = params.getOrDefault("secret", "").toString();
        if(params.containsKey("visid")){
            key = params.get("visid").toString();
            logger.info("+++++++++维塑请求我们的接口凭证，来自：cmspro，参数：" + key +"/"+ secret);
        }else if(params.containsKey("key")){
            logger.info("+++++++++维塑请求我们的接口凭证，来自：rpro3，参数：" + key +"/"+ secret);
        }
        String redisKey = ConfigConstant.REDIS_VISTOKEN_KEY;
        R r = new R();

        if (key.equals("") || secret.equals("")) {
            r.put("code", 30001);
            return r;
        }
        StoreDeviceV2 deviceV2 = storeDeviceV2Service.queryDeviceKey(key);
        if(deviceV2 == null || !secret.equalsIgnoreCase(deviceV2.getVisSecret())){
            r.put("code", 30003);
            return r;
        }
        if (redisUtils.get(redisKey) == null) {
            String token = generateToken();
            redisUtils.set(redisKey, token, 60 * 60 * 2);
        }

        if(deviceV2.getSource() == 101){
            r.put("code", 0);
            r.put("token", redisUtils.get(redisKey));
            r.put("expires_in", 60 * 60 * 2);
        }else if(deviceV2.getSource() == 102){
            HashMap data = new HashMap();
            data.put("token", redisUtils.get(redisKey));
            data.put("expires_in", 60 * 60 * 2);
            r.put("code", 0);
            r.put("data", data);
        }else{
            r.put("code", 30005);
            return r;
        }
        logger.info("来自维塑的key/secret效验通过，返回token给维塑：" + redisUtils.get(redisKey) );
        return r;
    }

    /**
     * API对接设置-二维码请求地址(跟设备关系不大，可通用)
     * @param request
     * @return
     * tips：维塑获取我们的二维码地址，并将附带参数/scan_id、device_id
     */
    @RequestMapping("v1/visbody/getQrCode")
    public R putVisQrCode(HttpServletRequest request){
        /*
         * 杀千刀的，3D体型追踪仪传入的参数是scanId、deviceId
         * 3D智能体测仪传入的参数为：scan_id、device_id
         * 注意区分下，做兼容处理，同时，返回的数据格式也不同，尤其要注意，否则设备端会提示网络异常....
         */
        String scanId = request.getParameter("scan_id");
        String deviceId = request.getParameter("device_id");
        if(StringUtils.isBlank(scanId)){
            scanId = request.getParameter("scanId");
        }
        if(StringUtils.isBlank(deviceId)){
            deviceId = request.getParameter("deviceId");
        }
        String token = request.getParameter("token");
        String redisKey = ConfigConstant.REDIS_VISTOKEN_KEY;
        R r = new R();
        logger.info("++++++++++获取体测结果绑定二维码，token=" + token +"/scan_id="+ scanId +"/device_id="+ deviceId);
        logger.info("++++++++++正确token=" + redisUtils.get(redisKey) );
        if (token == null || redisUtils.get(redisKey) == null || !redisUtils.get(redisKey).equals(token) ) {
            r.put("code", 30001);
            r.put("error_msg", "ERROR_MSG");
        }
        //根据device_id获取visKey,便于生成请求维塑的token(需根据门店设备参数生成独立的token，否则推送体测报告时会绑定失败/scan_id无效)
        StoreDeviceV2 storeDevice = storeDeviceV2Service.queryDeviceByDeviceId(deviceId);
        logger.info("设备信息："+ deviceId +"/"+ storeDevice.getVisKey() +"/"+ storeDevice.getVisSecret() +"/"+ storeDevice.getSource() );


        //将参数scan_id、device_id写入二维码中,等待APP扫码后将user_id与scan_id、device_id一起提交给维塑提供的用户绑定接口
        String filename = "";
        try {
            String qrContent = "scan_id=" + scanId + "&device_id=" + deviceId;
            filename = QrcodeUtil.generateQRCodeImage(qrContent, 350, 350, rpro3QrcodePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        String qrcodePath = "http://" + request.getServerName() + request.getContextPath() + "/statics/images/qrcode/" + filename;
        System.out.println("二维码路径" + qrcodePath);

        if(storeDevice.getSource() == 101){
            r.put("data", qrcodePath);
        }else if(storeDevice.getSource() == 102){
            HashMap data = new HashMap();
            data.put("url", qrcodePath);
            data.put("scan_id", scanId);
            data.put("device_id", deviceId);
            r.put("data", data);
        }
        r.put("code", 0);
        return r;
    }

    /**
     * 从缓存中获取维塑接口token
     * @param deviceId 设备ID
     * 注意：3D体型追踪仪/3D智能体测仪请求的接口域名不同，同时token也不同，token是根据设备关联的key/Secret生成的
     * @return
     */
    public String getVisbodyTokenByCache(String deviceId){
        StoreDeviceV2 storeDevice = storeDeviceV2Service.queryDeviceByDeviceId(deviceId);
        logger.info("设备信息："+ deviceId +"/"+ storeDevice.getVisKey() +"/"+ storeDevice.getVisSecret() +"/"+ storeDevice.getSource() );
        String rkey = "";
        if(storeDevice.getSource() == 101){
            rkey = ConfigConstant.REDIS_CMSPRO_TOKEN_KEY + storeDevice.getVisKey() ;
        }else if(storeDevice.getSource() == 102){
            rkey = ConfigConstant.REDIS_RPRO3_TOKEN_KEY + storeDevice.getVisKey() ;
        }else{
            return null;
        }
        if (redisUtils.get(rkey) == null) {
            getVisbodyToken(rkey, storeDevice.getVisKey(), storeDevice.getVisSecret(), storeDevice.getSource());
        }
        String token = redisUtils.get(rkey);
        return token;
    }

    /**
     * 获取维塑API接口凭证
     * @param redisKey 对应的redis key
     * @param visKey 维塑凭证/key（每台设备独立）
     * @param visSecret 维塑凭证/秘钥（每台设备独立）
     * @param source
     * @return
     */
    public String getVisbodyToken(String redisKey, String visKey, String visSecret, int source) {
        if(source == 101){
            String result = SendPushPost.sendGet(ConfigConstant.GET_VISTOKEN + "?visid=" + visKey + "&secret=" + visSecret);
            JSONObject resMap = JSONObject.parseObject(result);
            if (resMap.get("code").toString().equalsIgnoreCase("0")) {
                redisUtils.set(redisKey, resMap.get("token").toString(), Long.parseLong(resMap.get("expires_in").toString()));
                logger.info("3D体型追踪仪token=" + resMap.get("token").toString());
                return resMap.get("token").toString();
            }
        }else if(source == 102){
            String result = SendPushPost.sendGet(ConfigConstant.GET_RPRO_TOKEN + "?key=" + visKey + "&secret=" + visSecret);
            JSONObject resMap = JSONObject.parseObject(result);
            if (resMap.get("code").toString().equalsIgnoreCase("0")) {
                String token = resMap.getJSONObject("data").getString("token");
                redisUtils.set(redisKey, token, Long.parseLong(resMap.getJSONObject("data").get("expires_in").toString()));
                logger.info("3D智慧体测仪token=" + token);
                return token;
            }
        }
        return null;
    }

    /**
     * 获取维塑API接口凭证(cmspro/体态评估信息)，注意：与上面的获取token的域名不同
     * @param redisKey 对应的redis key
     * @param visKey 维塑凭证/key（每台设备独立）
     * @param visSecret 维塑凭证/秘钥（每台设备独立）
     * @param source
     * @return
     */
    public String getVisbodyStatusToken(String redisKey, String visKey, String visSecret, int source) {
        String result = SendPushPost.sendGet(ConfigConstant.GET_StATUS_VISTOKEN + "?visid=" + visKey + "&secret=" + visSecret);
        JSONObject resMap = JSONObject.parseObject(result);
        System.out.println("体态评估token：" + resMap);
        if (resMap.get("code").toString().equalsIgnoreCase("0")) {
            redisUtils.set(redisKey, resMap.get("token").toString(), Long.parseLong(resMap.get("expires_in").toString()));
            logger.info("3D体型追踪仪token=" + resMap.get("token").toString());
            return resMap.get("token").toString();
        }

        return null;
    }

    /**
     * 用户信息绑定
     * @param scan_id
     * @param device_id
     * @param user_id
     * tips：3D体型追踪仪、3D智能体测精灵，提交的接口、参数、返回的字段均与3D智能体测设备不同，注意区分
     * @return
     */
    @RequestMapping(value = "v1/userDataBind", method = RequestMethod.POST)
    public R userDataBind(String scan_id, String device_id, String user_id){
        R r = new R();
        logger.info("++++++++++调用维塑用户信息绑定接口，scan_id=" + scan_id +"/device_id=" + device_id +"/user_id=" + user_id);
        Map userinfo = userInfoService.findUserInfoById(Long.parseLong(user_id));
        logger.info("用户信息=" + userinfo);
        Long proxyId = userInfoService.getDeviceProxyId(Long.parseLong(user_id));
        logger.info("用户设备proxyId=" + proxyId);
        //根据device_id获取visKey,便于生成请求维塑的token(需根据门店设备参数生成独立的token，否则推送体测报告时会绑定失败/scan_id无效)
        StoreDeviceV2 storeDevice = storeDeviceV2Service.queryDeviceByDeviceId(device_id);
        logger.info("设备信息："+ device_id +"/"+ storeDevice.getVisKey() +"/"+ storeDevice.getVisSecret() +"/"+ storeDevice.getSource() );

        if (userinfo == null) {
            r.put("code", 40001);
            r.put("msg", "ERROR_MSG");
        }else if(proxyId == null){
            r.put("code", 40001);
            r.put("msg", "非会员卡用户，请购卡后使用");
        }else if(storeDevice == null){
            r.put("code", 40001);
            r.put("msg", "device_id无匹配配置");
        }else if(storeDevice.getSource() == 101){
            Date birthday = DateUtils.toDate(userinfo.get("birthday").toString());
            int age = DateUtils.getYear(birthday, new Date());
            String rkey = ConfigConstant.REDIS_CMSPRO_TOKEN_KEY + storeDevice.getVisKey() ;
            if(redisUtils.get(rkey) == null){
                getVisbodyToken(rkey, storeDevice.getVisKey(), storeDevice.getVisSecret(), storeDevice.getSource());
            }
            Map paramMap = new HashMap<>();
            paramMap.put("scanid", scan_id);
            paramMap.put("deviceid", device_id);
            paramMap.put("userid", user_id);
            paramMap.put("mobile", userinfo.getOrDefault("phone", "").toString() );
            paramMap.put("sex", Integer.parseInt(userinfo.getOrDefault("sex", "1").toString()) );
            paramMap.put("age", age);
            paramMap.put("height", Integer.parseInt(userinfo.getOrDefault("height", "0").toString()) );
            paramMap.put("token", redisUtils.get(rkey) );
            System.out.println("提交的用户信息转换" + JSONObject.toJSONString(paramMap) );

            String result = HttpClientUtils.postJson(ConfigConstant.VIS_DATABIND_URL, JSONObject.toJSONString(paramMap) );
            logger.info("用户信息绑定返回结果：{}", result);

            JSONObject resultObject = JSONObject.parseObject(result);
            if(resultObject.getString("code").equalsIgnoreCase("0")){
                r.put("code", 0);
                r.put("data", paramMap);
                r.put("msg", "成功");
            }else{
                r.put("code", resultObject.getString("code"));
                r.put("msg", resultObject.getString("errmsg"));
            }
        }else if(storeDevice.getSource() == 102){
            String rkey = ConfigConstant.REDIS_CMSPRO_TOKEN_KEY + storeDevice.getVisKey() ;
            if(redisUtils.get(rkey) == null){
                getVisbodyToken(rkey, storeDevice.getVisKey(), storeDevice.getVisSecret(), storeDevice.getSource());
            }

            String third_uid = String.format("%08d", Integer.parseInt(user_id));
            Map paramMap = new HashMap<>();
            paramMap.put("scan_id", scan_id);
            paramMap.put("device_id", device_id);
            paramMap.put("third_uid", third_uid);
            paramMap.put("mobile", userinfo.getOrDefault("phone", "").toString() );
            paramMap.put("sex", Integer.parseInt(userinfo.getOrDefault("sex", "1").toString()) );
            paramMap.put("height", Integer.parseInt(userinfo.getOrDefault("height", "0").toString()) );
            String birthday = userinfo.getOrDefault("birthday", "0").toString().substring(0, 10);
            paramMap.put("birthday", birthday);
            paramMap.put("token", redisUtils.get(rkey) );
            System.out.println("提交的用户信息转换" + JSONObject.toJSONString(paramMap) );

            String result = HttpClientUtils.postJson(ConfigConstant.RPRO_DATABIND_URL, JSONObject.toJSONString(paramMap) );
            logger.info("rpro3/用户信息绑定返回结果：" + result);

            JSONObject resultObject = JSONObject.parseObject(result);
            if(resultObject.getString("code").equalsIgnoreCase("0")){
                r.put("code", 0);
                r.put("data", paramMap);
                r.put("msg", "成功");
            }else{
                r.put("code", resultObject.getString("code"));
                r.put("msg", resultObject.getString("error_msg"));
            }
        }
        return r;
    }


    /**
     * 维塑回调通知-3D智能体测精灵
     *
     * @param // String deviceId,String userId,String scanId,String time,String token,Integer status,Integer type,String msg,Object data
     * @return ?token= 【VISTOKEN】&id=【ID】
     */
    @PostMapping("v1/rpro3/notifyResult")
    public R rproVisNotifyResult(@RequestBody JSONObject paramsObj) {
        R r = new R();
        r.put("code", 0);
        logger.info("3D智能体测精灵，推送合成通知消息param=" + paramsObj);
        JSONObject actionStatus = paramsObj.getJSONObject("action_status");
        logger.info("3D智能体测精灵，合成状态信息:" + actionStatus);
        if(actionStatus == null){
            return r;
        }
        //根据device_id获取visKey,便于生成请求维塑的token(需根据门店设备参数生成独立的token，否则推送体测报告时会绑定失败/scan_id无效)
        String deviceId = paramsObj.getString("device_id");
        StoreDeviceV2 storeDevice = storeDeviceV2Service.queryDeviceByDeviceId(deviceId);
        logger.info("设备信息："+ deviceId +"/"+ storeDevice.getVisKey() +"/"+ storeDevice.getVisSecret() +"/"+ storeDevice.getSource() );
        String rkey = ConfigConstant.REDIS_RPRO3_TOKEN_KEY + storeDevice.getVisKey() ;
        if (redisUtils.get(rkey) == null) {
            getVisbodyToken(rkey, storeDevice.getVisKey(), storeDevice.getVisSecret(), storeDevice.getSource());
        }
        String token = redisUtils.get(rkey);

        String scanId = paramsObj.get("scan_id").toString();
        String phone = paramsObj.getJSONObject("data").getString("phone");
        Map<String, Object> userMap = userInfoService.getUserInfoByPhone(phone);
        Long userId = Long.parseLong(userMap.getOrDefault("userId", "0").toString());
        Long vid =  Long.parseLong("0");
        Long gymEngineId = Long.parseLong("47");
        Long source = Long.valueOf( storeDevice.getSource() );

        //获取提测数据（维塑提供）
        //体测成分信息 URL:http://api.rpro3.visbody.com/v1/measure/mass?token=【VISTOKEN】&scanid= 【xxx】
        int saveResult = 0;
        if("1".equalsIgnoreCase(actionStatus.getString("bia_status")) ){
            String bodyInfo = SendPushPost.sendGet(ConfigConstant.RPRO_MEASURE_MASS + "?token=" + token + "&scan_id=" + scanId);
            logger.info("体测信息：" + bodyInfo);
            JSONObject bodyInfoResult = JSONObject.parseObject(bodyInfo);
            if(bodyInfoResult.getString("code").equalsIgnoreCase("0")){
                JSONObject bodyinfoData = bodyInfoService.bodyinfoFormatData(bodyInfoResult.getJSONObject("data"));
                bodyinfoData.put("userId", userId);
                bodyinfoData.put("vid", vid);
                bodyinfoData.put("ScanId", scanId);
                bodyinfoData.put("gymEngineId", source);    //3D智能提测精灵设备ID

                saveResult = bodyInfoService.saveOrUpdateBodyInfo(bodyinfoData);
                if (saveResult > 0) {
                    logger.info("++++++++++保存拉取的体测成分数据成功+++++++++");
                }
            }
        }
        //围度信息 URL:http://api.rpro3.visbody.com/v1/measure/girth
        int saveDimesion = 0;
        if("1".equalsIgnoreCase(actionStatus.getString("girth_status")) ){
            String bodyDimension = SendPushPost.sendGet(ConfigConstant.RPRO_MEASURE_GIRTH + "?token=" + token + "&scan_id=" + scanId);
            logger.info("维塑围度信息结果 " + bodyDimension);
            JSONObject bodyDimensionResult = JSONObject.parseObject(bodyDimension);
            if(bodyDimensionResult.getString("code").equalsIgnoreCase("0")){
                JSONObject bodyDimensData = bodyInfoService.bodyDimensFormatData(bodyDimensionResult.getJSONObject("data"));
                bodyDimensData.put("userId", userId);
                bodyDimensData.put("vid", vid);
                bodyDimensData.put("ScanId", scanId);
                bodyDimensData.put("gymEngineId", source);  //3D智能提测精灵设备ID

                saveDimesion = bodyInfoService.saveOrUpdateBodyDimension(bodyDimensData);
                if (saveDimesion > 0) {
                    logger.info("++++++++++保存拉取的围度信息成功+++++++++");
                }
            }
        }
        //体态评估信息 URL:http://api.rpro3.visbody.com/v1/shape/points
        int saveResultBodyShape = 0;
        if("1".equalsIgnoreCase(actionStatus.getString("eval_status"))){
            String bodyShape = SendPushPost.sendGet(ConfigConstant.RPRO_SHAPE_POINTS + "?token=" + token + "&scan_id=" + scanId );
            logger.info("维塑体态评估结果:" + bodyShape);
            JSONObject bodyShapeResult = JSONObject.parseObject(bodyShape);
            JSONObject bodyShapeData = bodyInfoService.bodyShapeFormatData(bodyShapeResult.getJSONObject("data"));
            bodyShapeData.put("userId", userId);
            bodyShapeData.put("ScanId", scanId);
            bodyShapeData.put("gymEngineId", source);  //3D智能提测精灵设备ID

            saveResultBodyShape = bodyInfoService.saveOrUpdateBodyShape(bodyShapeData);
            if (saveResultBodyShape > 0) {
                logger.info("++++++++++保存拉取的体态评估成功+++++++++");
            }
        }

        return r;
    }

    /**
     * 维塑回调通知-3D体型追踪仪
     *
     * @param // String deviceId,String userId,String scanId,String time,String token,Integer status,Integer type,String msg,Object data
     * @return ?token= 【VISTOKEN】&id=【ID】
     */
    @PostMapping("v1/cmspro/notifyResult")
    public R cmsproVisNotifyResult(@RequestBody JSONObject paramsObj) {
        R r = new R();
        r.put("code", 0);
        logger.info("3D体型追踪仪，推送合成通知消息param=" + paramsObj);
        logger.info("3D体型追踪仪，合成状态信息:" + paramsObj.get("status"));
        if (paramsObj.get("status").toString().equalsIgnoreCase("0")) {
            return r;
        }
        //根据device_id获取visKey,便于生成请求维塑的token(需根据门店设备参数生成独立的token，否则推送体测报告时会绑定失败/scan_id无效)
        String deviceId = paramsObj.getString("deviceId");
        StoreDeviceV2 storeDevice = storeDeviceV2Service.queryDeviceByDeviceId(deviceId);
        logger.info("设备信息："+ deviceId +"/"+ storeDevice.getVisKey() +"/"+ storeDevice.getVisSecret() +"/"+ storeDevice.getSource() );
        String rkey = ConfigConstant.REDIS_CMSPRO_TOKEN_KEY + storeDevice.getVisKey() ;
        if (redisUtils.get(rkey) == null) {
            getVisbodyToken(rkey, storeDevice.getVisKey(), storeDevice.getVisSecret(), storeDevice.getSource());
        }
        String token = redisUtils.get(rkey);
        Long source = Long.valueOf( storeDevice.getSource() );

        //获取维塑用户标识id（维塑提供）
        String result = SendPushPost.sendGet(ConfigConstant.GET_VISUSERID_URL + "?token=" + token + "&id=" + paramsObj.get("userId").toString());
        JSONObject jsonObject = JSONObject.parseObject(result);
        logger.info("获取维塑内部用户{}", jsonObject);
        if (!jsonObject.get("code").toString().equalsIgnoreCase("0")) {
            return R.reError("用户不存在");
        }
        //维塑内部用户id
        Long vid = Long.parseLong(jsonObject.get("vid").toString());
        String date = DateUtils.formatYYYMMDD(new Date());

        //从维塑获取-体成分数据
        String bodyInfo = SendPushPost.sendGet(ConfigConstant.GETVIS_BODYSINFO_URL + "?token=" + token + "&scanid=" + paramsObj.get("scanId").toString() + "&vid=" + vid + "&date=" + date);
        JSONObject bodyInfoMap = JSONObject.parseObject(bodyInfo);
        logger.info("获取维塑体测信息结果：" + bodyInfo);

        //保存拉取的体侧数据
        bodyInfoMap.put("userId", Long.parseLong(paramsObj.get("userId").toString()));
        bodyInfoMap.put("vid", vid);
        bodyInfoMap.put("gymEngineId", source);
        int svaeResult = bodyInfoService.saveOrUpdateBodyInfo(bodyInfoMap);
        if (svaeResult > 0) {
            logger.info("++++++++++保存拉取的体测数据成功+++++++++");
        }

        //从维塑获取-围度信息
        String bodyDimension = SendPushPost.sendGet(ConfigConstant.BODY_DIMENSION_URL + "?token=" + token + "&scanid=" + paramsObj.get("scanId").toString() + "&vid=" + vid + "&date=" + date);
        JSONObject bodyDimensionMap = JSONObject.parseObject(bodyDimension);
        logger.info("获取维塑围度信息结果：" + bodyDimension);

        //保存或更新围度信息
        bodyDimensionMap.put("userId", Long.parseLong(paramsObj.get("userId").toString()));
        bodyDimensionMap.put("vid", vid);
        bodyDimensionMap.put("gymEngineId", source);
        int saveDimesion = bodyInfoService.saveOrUpdateBodyDimension(bodyDimensionMap);
        if (saveDimesion > 0) {
            logger.info("++++++++++保存拉取的围度信息成功+++++++++");
        }


        //体态评估信息
        String rkey2 = ConfigConstant.REDIS_CMSPRO_STATUS_TOKEN_KEY + storeDevice.getVisKey() ;
        if (redisUtils.get(rkey2) == null) {
            getVisbodyStatusToken(rkey2, storeDevice.getVisKey(), storeDevice.getVisSecret(), storeDevice.getSource());
        }
        String token2 = redisUtils.get(rkey2);
        String bodyStatus = SendPushPost.sendGet(ConfigConstant.BODY_STATUS_URL + "?token=" + token2 + "&scanid=" + paramsObj.get("scanId").toString() + "&vid=" + vid + "&date=" + date);
        JSONObject bodyStatusMap = JSONObject.parseObject(bodyStatus);
        logger.info("获取维塑体态评估结果：" + bodyStatus);

        if ("0".equals(bodyStatusMap.get("code").toString())){
            bodyStatusMap.put("userId", Long.parseLong(paramsObj.get("userId").toString()));
            bodyStatusMap.put("vid", vid);
            bodyStatusMap.put("gymEngineId", source);

            //保存拉取的体态信息
            int saveResultBodyStatus = bodyInfoService.saveOrUpdateBodyStatus(bodyStatusMap);
            if (saveResultBodyStatus > 0) {
                logger.info("++++++++++保存拉取的体态信息成功+++++++++");
            }
        }

        r.put("code", 0);
        return r;
    }

    /**
     *  @Auther:YD
     *  首页门店
     */
    @RequestMapping("/getMyStore")
    public R getMyStore(@RequestParam Map<String,Object> params, HttpServletRequest req){
    	UserInfoVo userVo = getUserVoIgnore(req);
        List<Map<String,Object>> list = storeAddressService.getMyStore(userVo, params);
        if(list != null && !list.isEmpty()) {
        	for(Map<String, Object> item : list) {
        		int st = openDoorRecordMapper.getStorePeopleTotal(String.valueOf(item.get("storeId")));
        		item.put("onlineUser", st);
        	}
        }
        return R.reOk(list);
    }
    
    /**
     *  @Auther:YD
     *  切换门店
     */
    @RequestMapping("/setNowStore")
    public R setNowStore(int storeId, HttpServletRequest req){
    	UserInfoVo userVo = getUserVoIgnore(req);
        if(userVo != null) {
        	UserInfo userInfo = new UserInfo();
            userInfo.setNowStoreId(storeId);
            userInfo.setUserId(userVo.getUserId());
            this.userInfoService.updateUserAccount(userInfo);
        }
        return R.reOk();
    }
    
    /**
     *  @Auther:YD
     *  开门二维码
     */
    @RequestMapping("/getOpenDoorQR")
    public R getOpenDoorQR(@RequestParam Map<String,Object> params, HttpServletRequest req){
    	UserInfoVo userVo = getUserVo(req);
    	// 开门码走带停卡排除的入场专用查询:停卡期间该卡被 excludePausingDevice 排除,不再出码
    	Device device = deviceMapper.selectUserValidityForEntry(userVo.getUserId());
    	if(device == null) {
    		if(cardPauseRecordMapper.countActivePauseByUser(userVo.getUserId()) > 0) {
    			return R.reError("会员卡停卡中，暂不可使用");
    		}
    		return R.reError("未购买会员或过期");
    	}
    	int rand = new Random().nextInt(1000000);
    	String qrCode = "hzjsf_" + userVo.getUserId() + "-" + System.currentTimeMillis() + 
    			"-" + String.valueOf(params.get("userLat")) + "-" + String.valueOf(params.get("userLng") + "-" + rand);
    	redisUtils.set(ConfigConstant.DEVICE_RAND + device.getDeviceId(), rand, 5);
    	
    	String validityDate = null;
    	try {
	    	Date nowTime = new Date();
	    	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	    	String endTime = df.format(nowTime);
	        if (device != null){
	            //判断当前时间和有效期时间大小 按时间大的更新
	            String cpTime = df.format(device.getValidityDate());
	            Date bt = df.parse(cpTime);
	            Date et = df.parse(df.format(nowTime));
	            if(et.before(bt)){    //当前时间小于过期时间按过期时间算， 否则按当前时间算起
	                endTime = cpTime;
	            }
	        }
	        Long days = Long.valueOf(device.getValidity());
	        validityDate = df.format(df.parse(endTime).getTime() + (days - 1) * 24l * 60l * 60l * 1000l);    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	String lasttime = redisUtils.get(ConfigConstant.DEVICE + device.getDeviceId());
    	if(StringUtils.isBlank(lasttime)) { 
    		//判断次卡
    		if(device.getType().equals(10) && device.getUseCount() <= device.getUsedCount()) {
    			return R.reError("会员卡次数已用完");
    		}
    		
    		Date newtime = DateUtils.addMin(new Date(), 120);
    		lasttime = DateUtils.formatFull(newtime);
    		redisUtils.set(ConfigConstant.DEVICE + device.getDeviceId(), lasttime, 120 * 60);
    	}
    	String createtime = DateUtils.formatFull(DateUtils.addMin(DateUtils.toDateFull(lasttime), -120));
    	
        return R.reOk(device).put("qrCode", qrCode)
        		.put("wtState", userVo.getWtState())
        		.put("nextValidityDate", validityDate)
        		.put("lasttime", lasttime)
        		.put("createtime", createtime);
    }

}
