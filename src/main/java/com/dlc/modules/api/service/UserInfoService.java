package com.dlc.modules.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.entity.UserSportDeviceRecord;
import com.dlc.modules.api.vo.UserInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserInfoService {
    /**
     * 根据手机号查找用户
     * @param phone
     * @return
     */
    Map<String, Object> getUserInfoByPhone(String phone);
    /**
     * 用户注册
     * @param userInfo
     */
    void saveUserInfo(UserInfo userInfo);

    /**
     * 用户登录
     * @param userInfo
     */
    JSONObject login (UserInfo userInfo, HttpServletRequest req);

    /**
     * 更新用户信息
     * @param userInfo
     */
    R updateUserInfo(UserInfo userInfo, String code);
    /**
     * 根据ID 查找用户信息
     * @param userId
     */
    Map<String, Object> findUserInfoById(Long userId);

    /**
     * 微信登录
     * @param userInfo
     */
    JSONObject wxLogin (UserInfo userInfo, HttpServletRequest req);
    
    /**
     * 微信小程序登录
     * @param userInfo
     */
    JSONObject proLogin (String wxCode, HttpServletRequest req);
    
    /**
     * 支付宝登录
     * @param authCode
     * @param zfbUserId
     * @return
     */
    JSONObject zfbLogin (String authCode, String zfbUserId, String deviceToken, HttpServletRequest req);
    /**
     * h5微信登录
     * @param wxCode
     * @return
     */
    JSONObject h5Login (String wxCode, HttpServletRequest req);
    //是否存在同一手机号
    UserInfo querySamePhoneUser(String phone);

    UserInfoVo findUserInfoByOpenId(String openId);

    //void userInfoBind(Map<String, Object> userInfoMap);

    UserInfo queryUserIdByCardid(String cardid);

    Map<String,Object> userDataBind(UserInfo userInfo,String scanid,String deviceid,String cardid,String token);

    int saveUserSportRecord(UserSportDeviceRecord userSportDeviceRecord);

    List<Map<String,Object>> queryUserRecordByUserId(Query query);

    Map<String,Object> queryUserSportTotal(Query query);

    int queryTotalSportRecord(Query query);

    BigDecimal queryUserWalletByUserId(Long userId);

    UserInfo selectByPrimaryKey(Long userId);

    int updateUserAccount(UserInfo userInfo);

    void initUserExtInfo(UserInfo userInfo);

    void deleteUserByUserId(Long userId);

    int updateUserWallet(BigDecimal newUserWallet,Long userId);

    int queryIsForbiddenStatus(Long userId, String cardid);

    int queryClassTotal(Query query);

    /**
     * 根据ios唯一标识查询
     * @param iosUserId
     * @return
     */
    UserInfoVo selectByIosUserId(String iosUserId);

    /**
     * 苹果登录
     * @param userID
     * @param email
     * @param fullName
     * @param request
     * @return
     */
    JSONObject appleLogin(String userID, String email, String fullName, String deviceToken, HttpServletRequest request);

    /**
     * 查询用户会员卡有效账户ID（效验会员有效期）
     * @param userId
     * @return
     */
    Long getDeviceProxyId(Long userId);


    Long getUserIdByContractId(String contractId);

    void doCancelContract(Long userId);
	UserInfoVo selectByUserId(String userId);
}
