package com.dlc.modules.api.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.VipBenefitService;
import com.dlc.modules.api.service.VipCardService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.SortedMap;

/**
 * VIP 权益卡商品(移动端):list/detail 公开浏览;buy/myBenefits 需登录
 */
@RestController
@RequestMapping("/api/vipCard")
public class VipCardController extends BaseController {
    @Autowired
    private VipCardService vipCardService;
    @Autowired
    private VipBenefitService vipBenefitService;

    /**
     * 权益卡列表(小程序浏览),分页 + 后端实时算的动态价 currentPrice
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        // page/limit 规范成正整数:挡住 0/负数/非数字脏参数,
        // 否则 Query 算出负 offset → SQL "LIMIT 负数" 报错
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(vipCardService.queryVipCardList(params));
    }

    /** 把入参解析为正整数;null / 非数字 / <=0 一律取默认值 */
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

    /**
     * 权益卡详情,后端实时算动态价;不存在/已下架返回 ERROR_VIP_CARD_OFF_SHELF
     */
    @RequestMapping("/detail")
    public R detail(Long vipCardId) {
        if (vipCardId == null) {
            return R.reError("缺少参数 vipCardId");
        }
        return R.reOk(vipCardService.queryVipCardDetail(vipCardId));
    }

    /**
     * 权益卡购买下单(仅微信,需登录):后端按当前 sold_count 重算价、建待支付权益,返回微信调起参数
     */
    @RequestMapping("/buy")
    public R buy(Long vipCardId, HttpServletRequest request) {
        if (vipCardId == null) {
            return R.reError("缺少参数 vipCardId");
        }
        // 登录 + 封禁校验(未登录/被禁由 getUserVo 抛对应业务码)
        UserInfoVo user = getUserVo(request);
        try {
            SortedMap<String, String> map = vipBenefitService.buy(user, vipCardId);
            return R.reOk(map);
        } catch (RRException e) {
            // 业务码(如卡已下架 ERROR_VIP_CARD_OFF_SHELF)原样透传
            throw e;
        } catch (Exception e) {
            throw new RRException("微信支付失败", e);
        }
    }

    /**
     * 我的权益:已购权益卡列表,默认查 status=0 正常;过期与否由前端按 expireTime 实时判断
     */
    @RequestMapping("/myBenefits")
    public R myBenefits(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        // status 默认 0(正常),允许 0/2/3 等取值
        params.put("status", toIntOrDefault(params.get("status"), 0));
        // page/limit 规范成正整数,挡住 0/负数/非数字脏参数
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(vipBenefitService.myBenefits(params));
    }

    /** 解析为整数;null / 非数字一律取默认值(允许 0 与负值,区别于 toPositiveInt) */
    private int toIntOrDefault(Object val, int defaultVal) {
        if (val == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val.toString().trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
