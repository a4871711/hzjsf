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
 * 开放式停卡:apply 申请、resume 恢复(按实际天数顺延有效期)、list 我的记录
 */
@RestController
@RequestMapping("/api/cardPause")
public class CardPauseController extends BaseController {

    @Autowired
    private CardPauseService cardPauseService;

    /** 申请停卡 */
    @RequestMapping("/apply")
    public R apply(Long cardOrderId, Integer pauseDays, HttpServletRequest request) {
        if (cardOrderId == null) {
            return R.reError("缺少参数 cardOrderId");
        }
        Long userId = getUserId(request);
        return R.reOk(cardPauseService.apply(userId, cardOrderId, pauseDays));
    }

    /** 恢复停卡(按实际停卡天数顺延会员卡有效期) */
    @RequestMapping("/resume")
    public R resume(Long pauseId, HttpServletRequest request) {
        if (pauseId == null) {
            return R.reError("缺少参数 pauseId");
        }
        Long userId = getUserId(request);
        cardPauseService.resume(userId, pauseId);
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
