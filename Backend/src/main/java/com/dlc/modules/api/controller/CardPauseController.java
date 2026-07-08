package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.CardPauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 会员自助停卡(移动端,需登录)
 * 定期停卡:precheck 预检(免费额度/付费档位)、apply 申请(免费立即生效/付费返回支付单)、
 * cancel 提前取消(按未用天数扣回顺延)、list 我的记录
 */
@RestController
@RequestMapping("/api/cardPause")
public class CardPauseController extends BaseController {

    @Autowired
    private CardPauseService cardPauseService;

    /** 停卡预检:免费额度是否可用/下次可用时间/付费档位列表 */
    @RequestMapping("/precheck")
    public R precheck(HttpServletRequest request) {
        Long userId = getUserId(request);
        return R.reOk(cardPauseService.precheck(userId));
    }

    /** 申请停卡:pauseType=0 免费(需 pauseDays 1~7)/1 付费(需 tierIndex,金额天数以后端规则为准) */
    @RequestMapping("/apply")
    public R apply(Long cardOrderId, Integer pauseType, Integer pauseDays, Integer tierIndex,
                   HttpServletRequest request) {
        if (cardOrderId == null || pauseType == null) {
            return R.reError("缺少参数 cardOrderId/pauseType");
        }
        Long userId = getUserId(request);
        return R.reOk(cardPauseService.apply(userId, cardOrderId, pauseType, pauseDays, tierIndex));
    }

    /** 提前取消停卡(按未使用天数扣回预顺延的有效期;付费停卡不退款) */
    @RequestMapping("/cancel")
    public R cancel(Long pauseId, HttpServletRequest request) {
        if (pauseId == null) {
            return R.reError("缺少参数 pauseId");
        }
        Long userId = getUserId(request);
        cardPauseService.cancel(userId, pauseId);
        return R.reOk("取消成功");
    }

    /** 恢复停卡(兼容旧客户端入口,语义同 cancel:提前取消并结算天数) */
    @RequestMapping("/resume")
    public R resume(Long pauseId, HttpServletRequest request) {
        if (pauseId == null) {
            return R.reError("缺少参数 pauseId");
        }
        Long userId = getUserId(request);
        cardPauseService.cancel(userId, pauseId);
        return R.reOk("恢复成功");
    }

    /** 我的停卡记录(可按 cardOrderId 过滤) */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(cardPauseService.myList(params));
    }

    /** 入参解析为正整数;null / 非数字 / <=0 取默认值 */
    private int toPositiveInt(Object val, int defaultVal) {
        if (val == null) {
            return defaultVal;
        }
        try {
            int n = Integer.parseInt(val.toString().trim());
            return n > 0 ? n : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
