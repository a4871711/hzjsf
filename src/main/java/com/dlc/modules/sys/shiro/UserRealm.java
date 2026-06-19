package com.dlc.modules.sys.shiro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.dlc.common.utils.RedisKeys;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.controller.UserInfoController;
import com.dlc.modules.sys.dao.SysMenuDao;
import com.dlc.modules.sys.dao.SysUserDao;
import com.dlc.modules.sys.entity.SysMenuEntity;
import com.dlc.modules.sys.entity.SysUserEntity;

/**
 * 认证
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 上午11:55:49
 */
@Component
public class UserRealm extends AuthorizingRealm {
	final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysMenuDao sysMenuDao;
	@Autowired
    private RedisUtils redisUtils;
	
	@Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken || token instanceof WxAuthenticationToken;
    }
	
	@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		if (authcToken instanceof WxAuthenticationToken) {
			return processTokenAuthentication((WxAuthenticationToken) authcToken);
        } else {
        	return processPasswordAuthentication((UsernamePasswordToken) authcToken);
        }
    }

    private AuthenticationInfo processTokenAuthentication(WxAuthenticationToken token) {
        String accessToken = (String) token.getPrincipal();
        String redisKey = RedisKeys.getShiroSessionKey(accessToken);
        SysUserEntity user = redisUtils.get(redisKey, SysUserEntity.class);
        logger.info(redisKey, user);
        if (user == null) {
            throw new UnknownAccountException("Token已失效");
        }
        return new SimpleAuthenticationInfo(user, accessToken, getName());
    }

    private AuthenticationInfo processPasswordAuthentication(UsernamePasswordToken token) {
    	// 查询用户信息
		SysUserEntity user = sysUserDao.queryByUserName(token.getUsername());

		// 账号不存在
		if (user == null) {
			throw new UnknownAccountException("账号或密码不正确");
		}
		if(user.getStatus() == 0){
			throw new LockedAccountException("账号已被锁定,请联系管理员");
		}

		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), getName());
		return info;
    }

	/**
	 * 授权(验证权限时调用)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SysUserEntity user = (SysUserEntity) principals.getPrimaryPrincipal();
		Long userId = user.getUserId();

		List<String> permsList = null;

		// 系统管理员，拥有最高权限
		if (userId == 1) {
			List<SysMenuEntity> menuList = sysMenuDao.queryList(new HashMap<String, Object>());
			permsList = new ArrayList<>(menuList.size());
			for (SysMenuEntity menu : menuList) {
				permsList.add(menu.getPerms());
			}
		} else {
			permsList = sysUserDao.queryAllPerms(userId);
		}

		// 用户权限列表
		Set<String> permsSet = new HashSet<String>();
		for (String perms : permsList) {
			if (StringUtils.isBlank(perms)) {
				continue;
			}
			permsSet.addAll(Arrays.asList(perms.trim().split(",")));
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(permsSet);
		return info;
	}
	
	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
	    HashedCredentialsMatcher shaMatcher = new HashedCredentialsMatcher();
	    shaMatcher.setHashAlgorithmName(ShiroUtils.hashAlgorithmName);
	    shaMatcher.setHashIterations(ShiroUtils.hashIterations);

	    CredentialsMatcher compositeMatcher = new CredentialsMatcher() {
	        @Override
	        public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
	            if (token instanceof WxAuthenticationToken) {
	                // 直接比较Token字符串
	                return token.getCredentials().equals(info.getCredentials());
	            } else {
	                // 密码认证使用哈希验证
	                return shaMatcher.doCredentialsMatch(token, info);
	            }
	        }
	    };
	    super.setCredentialsMatcher(compositeMatcher);
	}
}
