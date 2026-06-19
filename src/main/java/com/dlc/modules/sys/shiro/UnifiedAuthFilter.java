package com.dlc.modules.sys.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

/**
 * 认证
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 上午11:55:49
 */
public class UnifiedAuthFilter extends AuthenticatingFilter {
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        // 1. 优先尝试获取 Token
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("X-Token");
        if (StringUtils.isEmpty(token)) {
        	token = httpRequest.getHeader("x-token");
        }
        if (StringUtils.isNotEmpty(token)) {
            return new WxAuthenticationToken(token); // Token 认证方式
        }
        
        // 2. 没有 Token 时使用传统 Session 认证
        return new UsernamePasswordToken(); // 实际会使用已存在的 Session
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    	try {
            return executeLogin(request, response);
        } catch (Exception e) {
            sendUnauthorizedResponse(response, "未登录");
            return false;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        // 同时允许已认证用户或记住我功能
        return subject.isAuthenticated() || subject.isRemembered();
    }
    
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        sendUnauthorizedResponse(response, "认证失败");
        return false;
    }

    private void sendUnauthorizedResponse(ServletResponse response, String message) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.setContentType("application/json;charset=UTF-8");
        try {
			httpResponse.getWriter().write("{\"code\":401, \"msg\":\"" + message + "\"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}