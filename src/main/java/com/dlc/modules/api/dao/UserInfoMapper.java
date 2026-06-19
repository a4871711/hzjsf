package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserInfoMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    /**
     * 查詢個人中心
     * @return
     */
    Map<String, Object> queryUserInfoById(Map<String, Object> queryMap);

    /**
     * 等級查詢
     * @param userEnergy
     * @return
     */
    Integer queryUserLevel(Integer userEnergy);
    //查datamap表等级规则
    Map<String, Object> queryLevel(Integer userEnergy);

    /**
     * 根据电话查找用户信息
     * @param phone
     * @return
     */
    Map<String, Object> findUserInfoByPhone(String phone);

    /**
     * 根据手机号及密码查找用户
     * @param userInfo
     * @return
     */
    List<UserInfoVo> findUserByPhoneAndPwd(UserInfo userInfo);

    /**
     * 根据用户ID查找用户
     * @param userId
     * @return
     */
    Map<String, Object> findUserInfoById(Long userId);

    /**
     * 根据微信openId 查找用户信息
     * @param openId
     * @return
     */
    UserInfoVo findUserInfoByOpenId(String openId);
    //查询该手机号下的用户
    UserInfo querySamePhoneUser(String phone);

    UserInfo queryUserIdByCardid(String cardid);
    //人脸认证状态修改
    int updateFaceStatus(Long userId);

    Map<String,Object> getCardStatus(Long userId);

    /**
     * 根据公众号openid 查找用户信息
     * @param openId
     * @return
     */
    UserInfoVo findUserInfoByGzhOpenId(String openId);

    int updateUserAccount(UserInfo userInfo);

    void updateRongYunInfo(UserInfo userInfo);

    /**
     * 根据支付宝openid(zfbUserId) 查找用户信息
     * @param openId
     * @return
     */
    UserInfoVo findUserInfoByZfbOpenId(String openId);

    Long queryStoreIdByUserId(Long userId);

    Long queryStoreAddrIdByUserId(Long userId);

    UserInfoVo queryByIosUserId(String iosUserId);
    //更新签约
    int updateContract(UserInfo userInfo);
    //解除
    int updateCancelContract(UserInfo userInfo);

    //接除协议
    void cancelContractV2(Long userId);

    //根据协议ID获取用户ID
    Long getUserIdByContractId(String contractId);

	UserInfoVo queryByUserId(String userId);

	Map<String, Object> userStats(Map<String, Object> queryMap);
}