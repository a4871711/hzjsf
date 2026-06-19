/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: UserInfoServiceImpl
 * Author:   Administrator
 * Date:     2018/5/18 17:48
 * Description: 用户级别信息查找
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.constant.ConstantProperty;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.*;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.RongCloudService;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.api.service.ZfbService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.GetWeiXinCode;
import com.dlc.modules.qd.utils.MD5Util;
import com.dlc.modules.qd.utils.SendPushPost;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    static Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private UserPointsMapper userPointsMapper;
    @Autowired
    private PointsExchangeMapper pointsExchangeMapper;
    @Autowired
    private SportRecordMapper sportRecordMapper;
    @Autowired
    private CoachMapper coachMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private RongCloudService rongCloudService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ConstantProperty constantProperty;
    @Autowired
    private ZfbService zfbService;
    @Autowired
    private UserSportDeviceRecordMapper userSportDeviceRecordMapper;

    @Autowired
    private UserTokenLoginLogsMapper userTokenLoginLogsMapper;

    @Override
    public Map<String, Object> getUserInfoByPhone(String phone) {
        return userInfoMapper.findUserInfoByPhone(phone);
    }

    @Override
    public void saveUserInfo(UserInfo userInfo) {
        //默认头像
        String img = constantProperty.PROJECT_URL + "/statics/img/head/default_avatar.jpg";
        userInfo.setHeadImgUrl(img);
        userInfo.setNickname(userInfo.getPhone());//初始化用户昵称，使用手机号代替
        userInfo.setPassword(MD5Util.MD5Encode(userInfo.getPassword(), "utf-8"));
        this.userInfoMapper.insertSelective(userInfo);
        //注册融云账号
        rongCloudService.getRongCloudToken(userInfo);
        //add by 20181011 to(初始化相关信息)
        initUserInfo(userInfo.getUserId());
    }

    /**
     * 初始化用户信息
     * @param userId
     */
    private void initUserInfo(Long userId) {
        if(null == userId){
            return;
        }
        try {
            //1.生成二维码标识
            UserInfo ui = new UserInfo();
            ui.setUserId(userId);
            ui.setQrCode(ConfigConstant.QRCODE_PRF + userId.toString());
            userInfoMapper.updateByPrimaryKeySelective(ui);
            //2.初始化钱包信息
            UserWallet userWallet = new UserWallet();
            userWallet.setUserId(userId);
            userWallet.setMoney(BigDecimal.ZERO);
            userWallet.setUpdateDate(new Date());
            userWalletMapper.insertSelective(userWallet);
            //3.初始化积分
            UserPoints userPoints = new UserPoints();
            userPoints.setUserId(userId);
            userPoints.setPointsCount(0);
            userPoints.setSumPoints(0);
            userPoints.setUpdateDate(new Date());
            userPointsMapper.insertSelective(userPoints);
            //4.初始化运动记录表
            SportRecord sportRecord = new SportRecord();
            sportRecord.setUserId(userId);
            sportRecord.setCreatedDate(new Date());
            sportRecordMapper.insertSelective(sportRecord);
        } catch (Exception e) {
            logger.info("初始化信息失败。。。saveUserInfo"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
    }

    @Override
    public JSONObject login(UserInfo userInfo, HttpServletRequest req) {
        JSONObject obj = new JSONObject();
        userInfo.setPassword(MD5Util.MD5Encode(userInfo.getPassword(),"utf-8"));
        List<UserInfoVo> lists = this.userInfoMapper.findUserByPhoneAndPwd(userInfo);
        if(lists.size()>0){
            UserInfoVo temp = lists.get(0);
            //处理表情
            String nickname = temp.getNickname();
            if(StringUtils.isNotBlank(nickname)){
                temp.setNickname(EmojiParser.parseToUnicode(nickname));
            }
            obj.put("userInfo",temp);
            String sessionId = redisUtils.get(ConfigConstant.USER+temp.getUserId());
            logger.info("sessionId1=="+sessionId);
            if(null != sessionId){
                redisUtils.delete(ConfigConstant.USER+temp.getUserId());
            }
            sessionId = req.getSession().getId();
            logger.info("sessionId2=="+sessionId);
            redisUtils.set(ConfigConstant.USER+temp.getUserId(),sessionId);
            //下面是更新用户的设备token
            UserInfo userInfoTemp = new UserInfo();
            userInfoTemp.setUserId(temp.getUserId());
            if(null != userInfo.getDeviceToken()){
                userInfoTemp.setDeviceToken(userInfo.getDeviceToken());
                this.userInfoMapper.updateByPrimaryKeySelective(userInfoTemp);
            }
            if(StringUtils.isNotBlank(userInfo.getDeviceToken())){
                logger.info("用户风控数据：{}/{}/{}", temp.getUserId(), temp.getDeviceToken(), userInfo.getDeviceToken() );
                int state = 0;
                HashMap loginLog = new HashMap();
                loginLog.put("userId", temp.getUserId() );
                loginLog.put("deviceToken", userInfo.getDeviceToken() );
                HashMap whiteLog = userTokenLoginLogsMapper.getWhiteLoginLog(loginLog);
                if(whiteLog != null){
                    state = 0;
                }else if(StringUtils.isNotBlank(temp.getDeviceToken()) &&
                        !userInfo.getDeviceToken().equalsIgnoreCase(temp.getDeviceToken()) ){
                    state = 1;
                }
                loginLog.put("state", state);
                logger.info("用户登录风控数据插入：{}", loginLog);
                userTokenLoginLogsMapper.insert(loginLog);
            }
        }
        return obj;
    }

    @Override
    public R updateUserInfo(UserInfo userInfo, String code) {
        //当前账户信息
        UserInfo ui = selectByPrimaryKey(userInfo.getUserId());
        //下面是为了合并账号的判断
        if (StringUtils.isNotBlank(userInfo.getPhone())) {//如果手机号非空表示 绑定手机（绑定手机号和修改其他信息不是同时进行的）
            if (StringUtils.isEmpty(code) || !code.equals(redisUtils.get(ConfigConstant.PHONE + userInfo.getPhone()))) {
                return R.reError("验证码有误");
            }
            //add by 2019-01-11 查询该手机号是否存在
            UserInfo oldUser = querySamePhoneUser(userInfo.getPhone());

            if(ui == null){return R.reError(CodeAndMsg.ERROR_LOGIN_OUT);}
            if(oldUser != null){
                //排除注册后更新手机
                if(StringUtils.isNotBlank(ui.getPhone())){
                    return R.reError("该手机号已存在");
                }
                //如果当前账户手机号不存在，则为不同渠道的账号整合。
                if(StringUtils.isBlank(ui.getPhone())){
                    //去更新整合老账户 该手机号已注册，整合账号（删除传进来的用户，并将传进来的用户整合到老用户上面）
                    ui.setPhone(userInfo.getPhone());
                    ui.setUserId(oldUser.getUserId());
                    logger.info("===更换手机号updateUserInfo=={}", ui);
                    int res = updateUserAccount(ui);
                    if(res > 0){
                        //整合账号后删除原纪录，防止查询同一用户时出现多条数据
                        deleteUserByUserId(userInfo.getUserId());
                        if(StringUtils.isNotBlank(ui.getGzhOpenId())){
                            return R.reError(CodeAndMsg.ERROR_LOGIN_OUT2);  //公众号返回102
                        }else{
                            return R.reError(CodeAndMsg.ERROR_LOGIN_OUT);   //其他渠道返回101
                        }

                    }else{
                        return R.reError("失败");
                    }
                }

            }else{
                if(StringUtils.isBlank(ui.getPhone())){
                    //第一次注册绑定手机号，注册融云绑定手机号初始化用户信息
                    ui.setPhone(userInfo.getPhone());
                    initUserExtInfo(ui);
                }else {//此处是单独修改手机号
                    this.userInfoMapper.updateByPrimaryKeySelective(userInfo);
                    return R.reError(CodeAndMsg.ERROR_LOGIN_OUT);
                }

            }
            //add end
        }

        //下面是第一次选择性别时默认头像
        if(userInfo.getSex() != null && 0 == ui.getSex()){
            String man = constantProperty.PROJECT_URL + "/statics/img/head/man.png";
            String woman = constantProperty.PROJECT_URL + "/statics/img/head/woman.png";
            if(userInfo.getSex() == 1){
                userInfo.setHeadImgUrl(man);
            }else{
                userInfo.setHeadImgUrl(woman);
            }
        }
        //处理表情
        String nickname = userInfo.getNickname();
        if(StringUtils.isNotBlank(nickname)){
            userInfo.setNickname(EmojiParser.parseToHtmlHexadecimal(nickname));
        }
        this.userInfoMapper.updateByPrimaryKeySelective(userInfo);
        //更新教练头像（如果已注册教练）
        try {
            if(StringUtils.isNotBlank(userInfo.getHeadImgUrl())){
                coachMapper.updateCoachInfo(userInfo.getUserId(), userInfo.getHeadImgUrl());
            }
        } catch (Exception e) {
            R.reError("更新教练头像报错");
            throw new RRException("请求失败",e);
        }
        return R.reOk();
    }

    @Override
    public Map<String, Object> findUserInfoById(Long userId) {
        Map<String, Object> map = this.userInfoMapper.findUserInfoById(userId);
        if(null != map){
            //处理表情
            String nickname = (String) map.get("nickname");
            if(StringUtils.isNotBlank(nickname)){
                map.put("nickname", EmojiParser.parseToUnicode(nickname));
            }
            for (Map.Entry<String, Object> m :map.entrySet())  {
                if(!"wristId".equals(m.getKey()) && null == m.getValue()){  //排除手环标识
                    map.put(m.getKey(),0);
                }
            }
        }
        return map;
    }

    @Override
    public JSONObject wxLogin(UserInfo userInfo, HttpServletRequest req) {
        JSONObject obj = new JSONObject();
        String openId = userInfo.getOpenId();


        UserInfoVo userInfoVo = this.userInfoMapper.findUserInfoByOpenId(openId);
        if(null == userInfoVo){//如果map==null，表示第一次登录，所以要先注册
            //处理表情
            userInfo.setSex(0);
            String nickname = userInfo.getNickname();
            if(StringUtils.isNotBlank(nickname)){
                userInfo.setNickname(EmojiParser.parseToHtmlHexadecimal(nickname));
            }

            this.userInfoMapper.insertSelective(userInfo);
            //注册融云账号（暂停用，在绑定手机号码处触发）
            //rongCloudService.getRongCloudToken(userInfo);
            //注册完上面的动作之后，再进行登录操作
            userInfoVo = this.userInfoMapper.findUserInfoByOpenId(openId);
            //add by 20181011 to(初始化相关信息)（暂停用，在绑定手机号码处触发）
            //initUserInfo(userInfo.getUserId());
            //add end
        }
        //处理表情
        String nickname = userInfoVo.getNickname();
        if(StringUtils.isNotBlank(nickname)){
            userInfoVo.setNickname(EmojiParser.parseToUnicode(nickname));
        }
        //下面进行登录操作
        obj.put("userInfo",userInfoVo);
        String sessionId = redisUtils.get(ConfigConstant.USER+userInfoVo.getUserId());
        logger.info("wxLogin---->sessionId1=="+sessionId);
        if(null != sessionId){
            redisUtils.delete(ConfigConstant.USER+userInfoVo.getUserId());
        }
        sessionId = req.getSession().getId();
        logger.info("wxLogin---sessionId2----->"+sessionId);
        redisUtils.set(ConfigConstant.USER+userInfoVo.getUserId(),sessionId);
        //下面是更新用户的设备token
        UserInfo temp = new UserInfo();
        temp.setUserId(userInfoVo.getUserId());
        if(null != userInfo.getDeviceToken()){
            temp.setDeviceToken(userInfo.getDeviceToken());
            this.userInfoMapper.updateByPrimaryKeySelective(temp);
        }
        return obj;
    }

    @Override
    public JSONObject zfbLogin(String authCode, String zfbUserId, String deviceToken, HttpServletRequest req) {
        JSONObject obj = new JSONObject();
        UserInfoVo userInfoVo = this.userInfoMapper.findUserInfoByZfbOpenId(zfbUserId);
        if(null == userInfoVo){//如果map==null，表示第一次登录，所以要先注册
            UserInfo userInfo = this.zfbService.getAlipayUser(authCode);
            if(null == userInfo){
                return obj;
            }
            //处理表情
            String nickname = userInfo.getNickname();
            if(StringUtils.isNotBlank(nickname)){
                userInfo.setNickname(EmojiParser.parseToHtmlHexadecimal(nickname));
            }
            //userInfo.setOpenId(zfbUserId);
            userInfo.setZfbOpenId(zfbUserId);
            this.userInfoMapper.insertSelective(userInfo);
            //注册融云账号（暂停用，在绑定手机号码处触发）
            //rongCloudService.getRongCloudToken(userInfo);
            //注册完上面的动作之后，再进行登录操作
            userInfoVo = this.userInfoMapper.findUserInfoByZfbOpenId(zfbUserId);
            //add by 20181011 to(初始化相关信息)（暂停用，在绑定手机号码处触发）
            //initUserInfo(userInfo.getUserId());
            //add end

        }
        //处理表情
        String nickname = userInfoVo.getNickname();
        if(StringUtils.isNotBlank(nickname)){
            userInfoVo.setNickname(EmojiParser.parseToUnicode(nickname));
        }
        //下面进行登录操作
        obj.put("userInfo",userInfoVo);
        String sessionId = redisUtils.get(ConfigConstant.USER+userInfoVo.getUserId());
        logger.info("wxLogin---->sessionId1=="+sessionId);
        if(null != sessionId){
            redisUtils.delete(ConfigConstant.USER+userInfoVo.getUserId());
        }
        sessionId = req.getSession().getId();
        logger.info("wxLogin---sessionId2----->"+sessionId);
        redisUtils.set(ConfigConstant.USER+userInfoVo.getUserId(),sessionId);
        //下面是更新用户的设备token
        UserInfo temp = new UserInfo();
        temp.setUserId(userInfoVo.getUserId());
        if(StringUtils.isNotBlank(deviceToken)){
            temp.setDeviceToken(deviceToken);
            this.userInfoMapper.updateByPrimaryKeySelective(temp);
        }
        return obj;
    }

    @Override
    public JSONObject h5Login(String wxCode, HttpServletRequest req) {
        JSONObject obj = new JSONObject();
        JSONObject result = GetWeiXinCode.getOpenId(req,wxCode);
        logger.info("h5Login--->result--->"+result.toString());
        String accessToken = result.get("access_token").toString();
        logger.info("============gzh accessToken========="+accessToken);
        String openId = result.get("openid").toString();
        logger.info("============gzh openId========="+openId);
        UserInfoVo userInfoVo = this.userInfoMapper.findUserInfoByGzhOpenId(openId);
//        redisUtils.set("wxCode",wxCode);
//        redisUtils.set(wxCode,userInfoVo.getOpenId());
        if(null == userInfoVo){//如果map==null，表示第一次登录，所以要先注册
            JSONObject object = GetWeiXinCode.getInfoUrlByAccessToken(accessToken,openId);
            //String openId = jsonObject.get("openid").toString();
            UserInfo userInfo = new UserInfo();
            userInfo.setHeadImgUrl(object.get("headimgurl").toString());
            userInfo.setNickname(EmojiParser.parseToHtmlHexadecimal(object.get("nickname").toString()));
            //userInfo.setOpenId(openId);
            userInfo.setGzhOpenId(openId);
            userInfo.setSex((int) object.get("sex"));

            this.userInfoMapper.insertSelective(userInfo);
            //注册融云账号（暂停用，在绑定手机号码处触发）
            //rongCloudService.getRongCloudToken(userInfo);
            //注册完上面的动作之后，再进行登录操作
            userInfoVo = this.userInfoMapper.findUserInfoByGzhOpenId(openId);
            //add by 20181011 to(初始化相关信息)（暂停用，在绑定手机号码处触发）
            //initUserInfo(userInfo.getUserId());
            //add end
        }
        //处理表情
        String nickname = userInfoVo.getNickname();
        if(StringUtils.isNotBlank(nickname)){
            userInfoVo.setNickname(EmojiParser.parseToUnicode(nickname));
        }
        //下面进行登录操作
        obj.put("userInfo",userInfoVo);
        String sessionId = redisUtils.get(ConfigConstant.USER+userInfoVo.getUserId());
        logger.info("wxLogin---->sessionId1=="+sessionId);
        if(null != sessionId){
            redisUtils.delete(ConfigConstant.USER+userInfoVo.getUserId());
        }
        sessionId = req.getSession().getId();
        logger.info("wxLogin---sessionId2----->"+sessionId);
        redisUtils.set(ConfigConstant.USER+userInfoVo.getUserId(),sessionId);
//        req.getSession().setAttribute(ConfigConstant.ACCOUNT,userInfoVo);
        return obj;
    }

    @Override
    public JSONObject proLogin(String wxCode, HttpServletRequest req) {
        JSONObject obj = new JSONObject();
        JSONObject result = GetWeiXinCode.getOpenIdSmall(wxCode);
        logger.info("proLogin--->result--->"+result.toString());
        String openId = result.get("openid").toString();
        logger.info("============gzh openId========="+openId);
        UserInfoVo userInfoVo = this.userInfoMapper.findUserInfoByOpenId(openId);
//        redisUtils.set("wxCode",wxCode);
//        redisUtils.set(wxCode,userInfoVo.getOpenId());
        if(null == userInfoVo){//如果map==null，表示第一次登录，所以要先注册
            //String openId = jsonObject.get("openid").toString();
            UserInfo userInfo = new UserInfo();
            userInfo.setHeadImgUrl("");
            userInfo.setNickname("");
            userInfo.setOpenId(openId);
            userInfo.setSex(0);

            this.userInfoMapper.insertSelective(userInfo);
            //注册融云账号（暂停用，在绑定手机号码处触发）
            //rongCloudService.getRongCloudToken(userInfo);
            //注册完上面的动作之后，再进行登录操作
            userInfoVo = this.userInfoMapper.findUserInfoByOpenId(openId);
            //add by 20181011 to(初始化相关信息)（暂停用，在绑定手机号码处触发）
            //initUserInfo(userInfo.getUserId());
            //add end
        }else if(userInfoVo.getAuditStatus() == 2) {
        	throw new RRException("该微信已被禁用，请联系客服处理", CodeAndMsg.ERROR_USER_IS_LOCK.getCode());
        }
        //处理表情
        String nickname = userInfoVo.getNickname();
        if(StringUtils.isNotBlank(nickname)){
            userInfoVo.setNickname(EmojiParser.parseToUnicode(nickname));
        }
        //下面进行登录操作
        userInfoVo.setToken(MD5Utils.MD5Encode(UUIDUtil.getUniqueIdByUUId(), "utf-8", false));
        obj.put("userInfo",userInfoVo);
        //String sessionId = redisUtils.get(ConfigConstant.USER+userInfoVo.getUserId());
        //logger.info("proLogin---->sessionId1=="+sessionId);
        //if(null != sessionId){
        //    redisUtils.delete(ConfigConstant.USER+userInfoVo.getUserId());
        //}
        //sessionId = req.getSession().getId();
        //logger.info("proLogin---sessionId2----->"+sessionId);
        //redisUtils.set(ConfigConstant.USER+userInfoVo.getUserId(),sessionId);
        redisUtils.set(ConfigConstant.USER+userInfoVo.getToken(),userInfoVo.getUserId());
        //req.getSession().setAttribute(ConfigConstant.ACCOUNT,userInfoVo);
        return obj;
    }

    @Override
    public UserInfo querySamePhoneUser(String phone) {
        return userInfoMapper.querySamePhoneUser(phone);
    }

    @Override
    public UserInfoVo findUserInfoByOpenId(String openId) {
        return userInfoMapper.findUserInfoByOpenId(openId);
    }

    @Override
    public UserInfo queryUserIdByCardid(String cardid) {
       return userInfoMapper.queryUserIdByCardid(cardid);
    }

    @Override
    public Map<String, Object> userDataBind(UserInfo userInfo,String scanid,String deviceid,String cardid,String token) {
        Date birthday = userInfo.getBirthday();
        int age = DateUtils.getYear(birthday, new Date());
        /*if(age<10){
            age=10;
        }*/
        String height = userInfo.getHeight();
        Integer sex = userInfo.getSex();
       /* if (Integer.parseInt(userInfo.getHeight())<140){
            height="140";
        }
        if (Integer.parseInt(userInfo.getHeight())>200){
            height="200";
        }
        if (sex!=1||sex!=2){
            sex = 1;
        }*/
        Map<String,Object> param = new HashMap<>();
        param.put("userid",userInfo.getUserId()+"");
        param.put("sex",sex+"");
        param.put("age",age+"");
        param.put("mobile",userInfo.getPhone()+"");
        param.put("scanid",scanid+"");
        param.put("deviceid",deviceid+"");
        param.put("height",height);
        param.put("token",redisUtils.get("visToken").toString()+"");
        String name = userInfo.getNickname();
        if(name != null){
            name = EmojiParser.parseToUnicode(name);
        }
        param.put("name",name+"");
        param.put("headimgurl",userInfo.getHeadImgUrl()+"");
        String result = SendPushPost.sendPost(ConfigConstant.VIS_DATABIND_URL, param);
        logger.info(getClass().getName()+"dataBind==="+param);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }

    @Override
    public int saveUserSportRecord(UserSportDeviceRecord userSportDeviceRecord) {
        //int res = userSportDeviceRecordMapper.saveUserSportRecord(userSportDeviceRecord);
        int res = userSportDeviceRecordMapper.insertSelective(userSportDeviceRecord);
        //能量兑换积分 add new function
        try {
            Long userId = userSportDeviceRecord.getUserId();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", userId);
            params.put("energy", userSportDeviceRecord.getCalorie());
            //可兑换积分
            Integer pointEx = userPointsMapper.getPointByCal(params);
            //查询当天积分获取情况和是否为卡类用户以及对应积分上限
            Integer dayPoint = pointsExchangeMapper.querySumDayPoint(userId);  //当天积分兑换总和
            Integer rulePoint = pointsExchangeMapper.queryRulePoints(userId);   //规则积分上限值
            if(dayPoint == null){dayPoint = 0;}
            if(rulePoint == null){rulePoint = 0;}
            if(dayPoint < rulePoint) {
                //判断兑换后积分是否超出用户一天兑换上限,如果超出
                if ((dayPoint + pointEx) > rulePoint) {
                    pointEx = rulePoint - dayPoint;
                }
                if (pointEx > 0) {
                    params.put("point", pointEx);
                    //更新用户积分
                    int rr = userPointsMapper.updatePointByCal(params);
                    if (rr > 0) {
                        //积分兑换记录
                        PointsExchange pointsExchange = new PointsExchange();
                        pointsExchange.setUserId(userId);
                        pointsExchange.setPointsCount(pointEx);
                        pointsExchange.setDetailType((byte) 1);  //运动积分+
                        pointsExchange.setExchangeDate(new Date());
                        pointsExchangeMapper.insertSelective(pointsExchange);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        //add end
        return res;
    }

    @Override
    public List<Map<String, Object>> queryUserRecordByUserId(Query query) {
       return userSportDeviceRecordMapper.queryUserRecordByUserId(query);
    }

    @Override
    public Map<String, Object> queryUserSportTotal(Query query) {
        //查询运动器械记录总参数值 totalCalorie/totalDistance/totalSportTime
        Map<String, Object> sportTotalMap = userSportDeviceRecordMapper.queryUserSportTotal(query);
        //查询私教课团体课总能量总次数
        Map<String, Object> classTotalMap = userSportDeviceRecordMapper.queryClassTotal(query);
        //运动数据
        BigDecimal totalCalorieS = new BigDecimal("0");
        BigDecimal totalDistanceS = new BigDecimal("0");
        BigDecimal oxy = new BigDecimal("0");
        BigDecimal uno = new BigDecimal("0");
        BigDecimal totalSportTimeS = new BigDecimal("0");
        if(sportTotalMap != null){
            totalCalorieS = (BigDecimal) sportTotalMap.get("totalCalorie");
            totalDistanceS = (BigDecimal) sportTotalMap.get("totalDistance");
            totalSportTimeS = (BigDecimal) sportTotalMap.get("totalSportTime");
            oxy = (BigDecimal) sportTotalMap.get("oxyKcal");
            uno = (BigDecimal) sportTotalMap.get("unoxyKcal");

        }
        //上课数据
        BigDecimal totalCalorieC = new BigDecimal("0");
        BigDecimal totalDistanceC = new BigDecimal("0");
        BigDecimal tc = new BigDecimal("0");
        BigDecimal pc = new BigDecimal("0");
        BigDecimal totalSportTimeC = new BigDecimal("0");
        if(classTotalMap != null){
            totalCalorieC = BigDecimal.valueOf((Double) classTotalMap.get("totalCalorie"));
            totalDistanceC = BigDecimal.valueOf((Double) classTotalMap.get("totalDistance"));
            totalSportTimeC = (BigDecimal) classTotalMap.get("totalSportTime");
            tc = BigDecimal.valueOf((Double) classTotalMap.get("teamClassKcal"));
            pc = BigDecimal.valueOf((Double) classTotalMap.get("privateClassKcal"));
        }

        //总卡路里
        BigDecimal totalCalorie = totalCalorieS.add(totalCalorieC);
        //总距离
        BigDecimal totalDistance = totalDistanceS.add(totalDistanceC);
        //总时间（秒）
        BigDecimal totalSportTime = totalSportTimeS.add(totalSportTimeC);

        Map<String, Object> sportMap = new HashMap<>();
        sportMap.put("totalCalorie", totalCalorie);
        sportMap.put("totalDistance", totalDistance);
        sportMap.put("totalSportTime", totalSportTime);
        //计算百分比
        BigDecimal energySum = oxy.add(uno).add(tc).add(pc);
        if(energySum.compareTo(BigDecimal.ZERO) == 1){  //总能量大于0
            oxy = (oxy.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            uno = (uno.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            tc = (tc.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            pc = (pc.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            //精确百分比
            if(pc.compareTo(BigDecimal.ZERO) == 1){
                pc = new BigDecimal("100.00").subtract(oxy.add(uno).add(tc));
            }else if(tc.compareTo(BigDecimal.ZERO) == 1){
                tc = new BigDecimal("100.00").subtract(oxy.add(uno).add(pc));
            }else if(uno.compareTo(BigDecimal.ZERO) == 1){
                uno = new BigDecimal("100.00").subtract(oxy.add(pc).add(tc));
            }else if(oxy.compareTo(BigDecimal.ZERO) == 1){
                oxy = new BigDecimal("100.00").subtract(uno.add(tc).add(pc));
            }

            sportMap.put("oxyKcal", oxy + "%");
            sportMap.put("unoxyKcal", uno + "%");
            sportMap.put("teamClassKcal", tc + "%");
            sportMap.put("privateClassKcal", pc + "%");
        }else{
            sportMap.put("oxyKcal",  "0%");
            sportMap.put("unoxyKcal", "0%");
            sportMap.put("teamClassKcal", "0%");
            sportMap.put("privateClassKcal", "0%");
        }
        return sportMap;

    }

    @Override
    public int queryTotalSportRecord(Query query) {
        return userSportDeviceRecordMapper.queryTotalSportRecord(query);
    }

    @Override
    public BigDecimal queryUserWalletByUserId(Long userId) {
       return userWalletMapper.queryUserMoney(userId);
    }

    @Override
    public UserInfo selectByPrimaryKey(Long userId) {
        return userInfoMapper.selectByPrimaryKey(userId);
    }

    @Override
    public int updateUserAccount(UserInfo userInfo) {
        return userInfoMapper.updateUserAccount(userInfo);
    }

    @Override
    public void initUserExtInfo(UserInfo userInfo) {
        try {
            //注册融云账号
            rongCloudService.getRongCloudToken(userInfo);
            //初始化相关信息
            initUserInfo(userInfo.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByUserId(Long userId) {
        userInfoMapper.deleteByPrimaryKey(userId);
    }

    @Override
    public int updateUserWallet(BigDecimal newUserWallet,Long userId) {
       return userWalletMapper.updateUserWallet(newUserWallet,userId);
    }

    @Override
    public int queryIsForbiddenStatus(Long userId, String cardid) {
        return deviceMapper.queryDeviceIsForbidden(userId, cardid);
    }

    @Override
    public int queryClassTotal(Query query) {
        return userSportDeviceRecordMapper.queryClassCount(query);
    }

    @Override
    public UserInfoVo selectByIosUserId(String iosUserId) {
        return userInfoMapper.queryByIosUserId(iosUserId);
    }

    @Override
    public UserInfoVo selectByUserId(String userId) {
        return userInfoMapper.queryByUserId(userId);
    }

    @Override
    public JSONObject appleLogin(String userID, String email, String fullName, String deviceToken, HttpServletRequest request) {
        JSONObject obj = new JSONObject();

        UserInfoVo userInfoVo = this.selectByIosUserId(userID);
        if(null == userInfoVo){//如果map==null，表示第一次登录，所以要先注册
            UserInfo userInfo = new UserInfo();
            //处理表情
            if(StringUtils.isNotBlank(email)){
                userInfo.setNickname(email);
            }
            userInfo.setIosUserId(userID);
            String man = constantProperty.PROJECT_URL + "/statics/img/head/man.png";
            userInfo.setHeadImgUrl(man); //默认头像
            this.userInfoMapper.insertSelective(userInfo);
            //注册融云账号（暂停用，在绑定手机号码处触发）
            //rongCloudService.getRongCloudToken(userInfo);
            //注册完上面的动作之后，再进行登录操作
            userInfoVo = this.selectByIosUserId(userID);

        }
        //处理表情
        userInfoVo.setNickname(email);
        //下面进行登录操作
        obj.put("userInfo",userInfoVo);
        String sessionId = redisUtils.get(ConfigConstant.USER+userInfoVo.getUserId());
        logger.info("appleLogin---->sessionId1=="+sessionId);
        if(null != sessionId){
            redisUtils.delete(ConfigConstant.USER+userInfoVo.getUserId());
        }
        sessionId = request.getSession().getId();
        logger.info("appleLogin---sessionId2----->"+sessionId);
        redisUtils.set(ConfigConstant.USER+userInfoVo.getUserId(),sessionId);
        //下面是更新用户的设备token
        UserInfo temp = new UserInfo();
        temp.setUserId(userInfoVo.getUserId());
        if(StringUtils.isNotBlank(deviceToken)){
            temp.setDeviceToken(deviceToken);
            this.userInfoMapper.updateByPrimaryKeySelective(temp);
        }
        return obj;
    }

    @Override
    public Long getDeviceProxyId(Long userId){
        return deviceMapper.checkUserValidity(userId);
    }

    @Override
    public Long getUserIdByContractId(String contractId){
        return userInfoMapper.getUserIdByContractId(contractId);
    }

    @Override
    public void doCancelContract(Long userId){
        userInfoMapper.cancelContractV2(userId);
    }

}
