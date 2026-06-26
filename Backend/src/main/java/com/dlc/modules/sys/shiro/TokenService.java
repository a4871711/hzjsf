package com.dlc.modules.sys.shiro;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dlc.common.utils.RedisKeys;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.sys.entity.SysUserEntity;

/**
 * 认证
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 上午11:55:49
 */
@Service
public class TokenService {
    @Autowired
    private RedisUtils redisUtils;

    public String createToken(SysUserEntity user) {
        String token = UUID.randomUUID().toString();
        String redisKey = RedisKeys.getShiroSessionKey(token);
        
        // 存储用户信息并设置过期时间
        redisUtils.set(redisKey, user, 7 * 86400);        
        return token;
    }
}