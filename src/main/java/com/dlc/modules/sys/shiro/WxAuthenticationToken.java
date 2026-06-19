package com.dlc.modules.sys.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 认证
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 上午11:55:49
 */
public class WxAuthenticationToken implements AuthenticationToken {
    private String token;

    public WxAuthenticationToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}