package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateOrderService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 私教订单(移动端):quote 试算 / create 下单 / myOrders 我的订单 / detail 订单详情,全部需登录。
 * 金额一律后端重算不信前端;create 只建待支付单+占券+微信统一下单,支付回调推进在第13步。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/privateOrder")
public class PrivateOrderController extends BaseController {

    @Autowired
    private PrivateOrderService privateOrderService;

    /**
     * 试算:入参 productId/storeId/quantity(固定1)/memberCouponId(可选)/marketingType+marketingActivityId(可选)。
     * 返回 原价/应付/券抵扣/活动优惠 明细;活动单不叠加券。
     */
    @RequestMapping("/quote")
    public R quote(Long productId, Long storeId, Integer quantity, Long memberCouponId,
                   Integer marketingType, Long marketingActivityId, HttpServletRequest request) {
        R check = checkOrderParams(productId, storeId, quantity);
        if (check != null) {
            return check;
        }
        // 登录 + 封禁校验(试算按会员身份取价口径预留)
        UserInfoVo user = getUserVo(request);
        return R.reOk(privateOrderService.quote(user, productId, storeId,
                memberCouponId, marketingType, marketingActivityId));
    }

    /**
     * 下单(不收钱不扣库存):校验+金额重算+建待支付单+券占用+微信统一下单。
     * 返回 {orderNo, payableAmount, payParams},前端用 payParams 直接调起小程序支付。
     */
    @RequestMapping("/create")
    public R create(Long productId, Long storeId, Integer quantity, Integer payMethod, Long memberCouponId,
                    Integer marketingType, Long marketingActivityId, HttpServletRequest request) {
        R check = checkOrderParams(productId, storeId, quantity);
        if (check != null) {
            return check;
        }
        // 本步只接微信统一下单;储值(3)第19步、分期(4)第20步、支付宝(2)按需接入
        if (payMethod != null && payMethod != 1) {
            return R.reError("当前仅支持微信支付");
        }
        UserInfoVo user = getUserVo(request);
        return R.reOk(privateOrderService.create(user, productId, storeId,
                memberCouponId, marketingType, marketingActivityId, request));
    }

    /** 我的订单分页,可选 orderStatus 过滤(0待支付/1首付已付/2已结清/3已取消/4已退款) */
    @RequestMapping("/myOrders")
    public R myOrders(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        // page/limit 规范成正整数:挡住 0/负数/非数字脏参数,否则 Query 算出负 offset → SQL 报错
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(privateOrderService.myOrders(params));
    }

    /** 订单详情(含券明细);非本人订单按不存在返回 */
    @RequestMapping("/detail")
    public R detail(String orderNo, HttpServletRequest request) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return R.reError("缺少参数 orderNo");
        }
        Long userId = getUserId(request);
        return R.reOk(privateOrderService.detail(userId, orderNo.trim()));
    }

    /** quote/create 公共入参护栏;quantity 一期固定为 1(单/权益均按一份建模) */
    private R checkOrderParams(Long productId, Long storeId, Integer quantity) {
        if (productId == null) {
            return R.reError("缺少参数 productId");
        }
        if (storeId == null) {
            return R.reError("缺少参数 storeId");
        }
        if (quantity != null && quantity != 1) {
            return R.reError("每单固定购买1件");
        }
        return null;
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
}
