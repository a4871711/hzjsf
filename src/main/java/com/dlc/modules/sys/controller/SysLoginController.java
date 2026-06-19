package com.dlc.modules.sys.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

import com.dlc.common.annotation.SysLog;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.R;
import com.dlc.common.utils.RedisUtils;
import com.dlc.common.validator.code.ImageCode;
import com.dlc.common.validator.code.ImageCodeGenerator;
import com.dlc.modules.qd.utils.PhoneCodeVer;
import com.dlc.modules.sys.dao.SysUserDao;
import com.dlc.modules.sys.entity.SysUserEntity;
import com.dlc.modules.sys.shiro.ShiroUtils;
import com.dlc.modules.sys.shiro.TokenService;

/**
 * 登录相关
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 下午1:15:31
 */
@Controller
public class SysLoginController {
	
	public static final String SESSION_KEY = "SESSION_KEY_IMAGE_CODE";
	
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
    private RedisUtils redisUtils;
	@Autowired
    private TokenService tokenService;
	
	@Autowired
	private ImageCodeGenerator imageCodeGenerator;
	
	@RequestMapping("/captcha.jpg")
	public void captcha(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        ImageCode imageCode = imageCodeGenerator.generate(new ServletWebRequest(request));
        //生成文字验证码
//        String text = producer.createText();
        //生成图片验证码
//        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        ShiroUtils.setSessionAttribute(SESSION_KEY, imageCode.getCode());
        
        ServletOutputStream out = response.getOutputStream();
//        ImageIO.write(image, "jpg", out);
        ImageIO.write(imageCode.getImage(), "JPEG", out);
	}
	
	/**
	 * 登录
	 */
	@SysLog("登录系统")
	@ResponseBody
	@RequestMapping(value = "/sys/login", method = RequestMethod.POST)
	public R login(String username, String password, String captcha)throws IOException {
		String kaptcha = ShiroUtils.getKaptcha(SESSION_KEY);
		if(!captcha.equalsIgnoreCase(kaptcha)){
			return R.error("验证码不正确");
		}
		SysUserEntity map = sysUserDao.queryByUserName(username);
        if (map == null) {
        	map = sysUserDao.queryByMobile(username);  
            if(map != null) {
            	username = map.getUsername();
            }
        }
		
		try{
			Subject subject = ShiroUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			subject.login(token);
		}catch (UnknownAccountException e) {
			return R.error(e.getMessage());
		}catch (IncorrectCredentialsException e) {
			return R.error("账号或密码不正确");
		}catch (LockedAccountException e) {
			return R.error("账号已被锁定,请联系管理员");
		}catch (AuthenticationException e) {
			return R.error("账户验证失败");
		}
	    map.setPassword(null);
	    map.setSalt(null);
		return R.ok().put("data", map);
	}
	
	/**
	 * 登录
	 */
	@SysLog("登录系统")
	@ResponseBody
	@RequestMapping(value = "/sys/prologin", method = RequestMethod.POST)
	public R prologin(String username, String password)throws IOException {
		SysUserEntity map = sysUserDao.queryByUserName(username);
        if (map == null) {
        	map = sysUserDao.queryByMobile(username);  
            if(map != null) {
            	username = map.getUsername();
            }
        }
		
		try{
			Subject subject = ShiroUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			subject.login(token);
		}catch (UnknownAccountException e) {
			return R.error(e.getMessage());
		}catch (IncorrectCredentialsException e) {
			return R.error("账号或密码不正确");
		}catch (LockedAccountException e) {
			return R.error("账号已被锁定,请联系管理员");
		}catch (AuthenticationException e) {
			return R.error("账户验证失败");
		}
	    map.setPassword(null);
	    map.setSalt(null);	    
	    String authToken = tokenService.createToken(map);
	    map.setToken(authToken);
		return R.ok().put("data", map);
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
	@ResponseBody
    @RequestMapping(value = "/sys/forgetPassword", method = RequestMethod.POST)
    public R forgetPassword(String phone, String code, String password) {

        if (StringUtils.isEmpty(phone) || !PhoneCodeVer.isPhoneNum(phone)) {
            return R.reError("手机号码有误");
        }
        if (StringUtils.isEmpty(password)) {
            return R.reError("密码不能为空!");
        }
        SysUserEntity map = sysUserDao.queryByUserName(phone);
        if (map == null) {
        	map = sysUserDao.queryByMobile(phone);
        	if(map == null)return R.reError("手机号码未注册用户");
        }
        if (StringUtils.isEmpty(code) || !code.equals(redisUtils.get(ConfigConstant.PHONE + phone))) {
            return R.reError("验证码有误");
        }
        String newPassword = ShiroUtils.sha256(password, map.getSalt());
        Map<String, Object> map1 = new HashMap<>();
		map1.put("userId", map.getUserId());
		map1.put("password", null);
		map1.put("newPassword", newPassword);
		sysUserDao.updatePassword(map1);
        return R.ok();
    }
	
	/**
	 * 退出
	 */
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout() {
		ShiroUtils.logout();
		return "redirect:login.html";
	}
	
}
