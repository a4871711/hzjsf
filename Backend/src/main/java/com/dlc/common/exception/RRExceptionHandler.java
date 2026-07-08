package com.dlc.common.exception;

import com.alibaba.fastjson.JSONException;
import com.dlc.common.utils.R;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月27日 下午10:16:19
 */
@RestControllerAdvice
public class RRExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RRException.class)
	public R handleRRException(RRException e){
		R R = new R();
		R.put("code", e.getCode());
		R.put("msg", e.getMessage());
		return R;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public R handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return R.error("数据库中已存在该记录");
	}

	@ExceptionHandler(AuthorizationException.class)
	public R handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage(), e);
		return R.error("没有权限，请联系管理员授权");
	}

	@ExceptionHandler(JSONException.class)
	public R handleJSONException(JSONException e) {
		logger.error(e.getMessage(), e);
		return R.error("数据解析异常");
	}

	@ExceptionHandler(NumberFormatException.class)
	public R handleNumberFormatException(NumberFormatException e) {
		logger.error(e.getMessage(), e);
		return R.error("数据格式异常");
	}

	/**
	 * @RequestBody 参数绑定/反序列化失败（如日期、数字格式不对）。
	 * 没有这个 handler 时会落到下面的 Exception 兜底，只返回"操作有误"，完全无法定位是哪个字段的问题
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public R handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		logger.error(e.getMessage(), e);
		return R.error(-1, "请求参数格式错误：" + e.getMostSpecificCause().getMessage());
	}

	@ExceptionHandler(Exception.class)
	public R handleException(Exception e){
		logger.error(e.getMessage(), e);
		return R.error(-1,"操作有误");
	}
}
