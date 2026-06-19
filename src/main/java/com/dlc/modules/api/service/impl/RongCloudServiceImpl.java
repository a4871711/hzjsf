package com.dlc.modules.api.service.impl;

import com.dlc.common.constant.ConstantProperty;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.RongCloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RongCloudServiceImpl implements RongCloudService {

    static Logger log = LoggerFactory.getLogger(RongCloudServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private ConstantProperty constantProperty;

    private RedisUtils redisUtils;

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * 注册融云token
     * @param userInfo
     */
    @Override
    public void getRongCloudToken(UserInfo userInfo) {
        /*final RongCloud rongCloud = RongCloud.getInstance(constantProperty.RONG_CLOUD_APP_KEY,
                constantProperty.RONG_CLOUD_APP_SECRET);

        //RongCloud rongCloud = RongCloud.getInstance(RongCloudUtils.APP_KEY, RongCloudUtils.APP_SECRET);
        //自定义 api 地址方式
        // RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);
        User User = rongCloud.user;
        *//**
         * API 文档: http://rongcloud.github.io/server-sdk-nodejs/docs/v1/user/user.html#register
         *
         * 注册用户，生成用户在融云的唯一身份标识 Token
         *//*
        String headImgUrl = userInfo.getHeadImgUrl();
        UserModel user = new UserModel()
                .setId("ZHJSF_RC" + userInfo.getUserId())
                .setName(null==userInfo.getPhone()?userInfo.getNickname():userInfo.getPhone())
                .setPortrait(headImgUrl.contains("http") ? headImgUrl:constantProperty.PROJECT_URL+headImgUrl);

        log.info("UserModel->user:" + user);
        TokenResult result = null;
        try {
            result = User.register(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("融云账号注册失败");
        }
        log.info("rongCloud->getToken:  " + result.toString());
        userInfo.setRongCloudId(result.getUserId());
        userInfo.setRongCloudToken(result.getToken());*/
        //userInfoMapper.updateByPrimaryKeySelective(userInfo);
        String rongId = "ZHJSF_RC3";
        String rongToken = "Lh19nIQGzOFtdtQtCy0r4+SrvO5/kHVQbYQZUTYwvT6HhTf+IjRoEF20VVek6pzW12p2eqk9F/jjpexf+1bwzQKRLRtkBFSu";
        userInfo.setRongCloudId(rongId);
        userInfo.setRongCloudToken(rongToken);
        userInfoMapper.updateRongYunInfo(userInfo);   //优化：只需要更新融云信息即可
    }

    /**
     * 获取融云token
     * @param id
     * @return
     */
    public String getToken(String id) {
        if(StringUtils.isEmpty(id)){
            return null;
        }
        String token = redisUtils.get(id);
        if (StringUtils.isEmpty(token)) {
            log.info("获取融云token失败，该用户还没有注册融云账号信息");
        }
        return token;
    }
}
