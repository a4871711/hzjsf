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
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // page/limit 规范成正整数:挡住 0/负数/非数字脏参数,
        // 否则 Query 算出负 offset → SQL "LIMIT 负数" 报错
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        // 已登录则识别用户(未登录静默 null):已持有效权益的用户只看到其持有的那张卡(下架也展示)
        UserInfoVo user = getUserVoIgnore(request);
        Long userId = user == null ? null : user.getUserId();
        return R.reOk(vipCardService.queryVipCardList(params, userId));
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
    public R detail(Long vipCardId, HttpServletRequest request) {
        if (vipCardId == null) {
            return R.reError("缺少参数 vipCardId");
        }
        // 已登录则识别用户(未登录静默返回 null),用于填充 hasBenefit;不强制登录,保持公开浏览
        UserInfoVo user = getUserVoIgnore(request);
        Long userId = user == null ? null : user.getUserId();
        return R.reOk(vipCardService.queryVipCardDetail(vipCardId, userId));
    }

    /**
     * 权益卡购买下单(仅微信,需登录):后端按当前 sold_count 重算价、建待支付权益,返回微信调起参数
     */
    @RequestMapping("/buy")
    public R buy(Long vipCardId, Long storeId, Long storeAddrId, Long flashSaleActivityId, HttpServletRequest request) {
        if (vipCardId == null) {
            return R.reError("缺少参数 vipCardId");
        }
        // 登录 + 封禁校验(未登录/被禁由 getUserVo 抛对应业务码;卡已下架由 service 抛 ERROR_VIP_CARD_OFF_SHELF)
        UserInfoVo user = getUserVo(request);
        // storeId/storeAddrId=购买时所在门店(可空),写入购买记录的门店归属供后台展示
        // flashSaleActivityId=限时秒杀活动ID(可空);命中则按秒杀价成交
        // 返回 {orderNo, paySum},前端据此调 /wx/proPay 调起小程序支付
        return R.reOk(vipBenefitService.buy(user, vipCardId, storeId, storeAddrId, flashSaleActivityId));
    }

    /**
     * 我的权益:已购权益卡列表,默认查 status=0 正常;过期与否由前端按 expireTime 实时判断
     */
    @RequestMapping("/myBenefits")
    public R myBenefits(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        // status 显式传才精确筛选(0正常/2已冻结/4已注销...);不传则返回名下全部状态,由 SQL 默认排除 9 待支付占位单。
        // 原先无条件默认 0 会被 MyBatis 的 OGNL 空串比较吞掉过滤(Integer 0 时 status!='' 恒 false),
        // 反而返回全部状态且被前端标成「正常」,故改为按是否传参决定是否精确筛选。
        Object statusParam = params.get("status");
        if (statusParam != null && !"".equals(String.valueOf(statusParam).trim())) {
            params.put("status", toIntOrDefault(statusParam, 0));
        } else {
            params.remove("status");
        }
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
