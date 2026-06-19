/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: BaseController
 * Author:   Administrator
 * Date:     2018/5/23 20:11
 * Description: 基础公共类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.modules.api.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.R;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.service.UserInfoService;
//import com.dlc.modules.api.vo.AgentVo;
import com.dlc.modules.api.vo.UserInfoVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
//import sun.management.resources.agent;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenyuexin
 * @date 2018-05-23 20:11
 * @version 1.0
 */
@Controller
public class BaseController {
	@Autowired
    private UserInfoService userInfoService;
	@Autowired
    private RedisUtils redisUtils;

   public Long getUserId(HttpServletRequest req){
	   //UserInfoVo userInfoVo = (UserInfoVo) req.getSession().getAttribute(ConfigConstant.ACCOUNT);
		String token = req.getHeader("token");
		//如果header中不存在token，则从参数中获取token
		if(token == null || token.isEmpty()){
		    token = req.getParameter("token");
		}
		if(token == null || token.isEmpty()){
			throw new RRException("登录token不能为空", CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode());
		}
		String userId = redisUtils.get(ConfigConstant.USER + token);
		if(userId == null || userId.isEmpty()){
			throw new RRException("登录token已失效", CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode());
		}
        return Long.parseLong(userId);
    }

    public UserInfoVo getUserVo(HttpServletRequest req){
        //UserInfoVo userInfoVo = (UserInfoVo) req.getSession().getAttribute(ConfigConstant.ACCOUNT);
    	String token = req.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(token == null || token.isEmpty()){
            token = req.getParameter("token");
        }
        if(token == null || token.isEmpty()){
        	throw new RRException("登录token不能为空", CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode());
        }
    	String userId = redisUtils.get(ConfigConstant.USER + token);
        if(userId == null || userId.isEmpty()){
        	throw new RRException("登录token已失效", CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode());
        }
        UserInfoVo userInfoVo = userInfoService.selectByUserId(userId);
        if(userInfoVo == null){
        	throw new RRException("请登录微信账号", CodeAndMsg.ERROR_USER_NOT_LOGIN.getCode());
        }
        if(userInfoVo.getAuditStatus() == 2) {
        	throw new RRException("该微信已被禁用，请联系客服处理", CodeAndMsg.ERROR_USER_IS_LOCK.getCode());
        }
        return userInfoVo;
    }
    
    public UserInfoVo getUserVoIgnore(HttpServletRequest req){
        //UserInfoVo userInfoVo = (UserInfoVo) req.getSession().getAttribute(ConfigConstant.ACCOUNT);
    	String token = req.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(token == null || token.isEmpty()){
            token = req.getParameter("token");
        }
        if(token == null || token.isEmpty()){
        	return null;
        }
    	String userId = redisUtils.get(ConfigConstant.USER + token);
        if(token == null || token.isEmpty()){
        	return null;
        }
        UserInfoVo userInfoVo = userInfoService.selectByUserId(userId);
        if(userInfoVo == null){
        	return null;
        }
        if(userInfoVo.getAuditStatus() == 2) {
        	return null;
        }
        return userInfoVo;
    }
}
