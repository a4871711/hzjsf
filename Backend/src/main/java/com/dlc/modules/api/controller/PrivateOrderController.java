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
     * 下单：校验+金额重算+建订单+券占用；微信/分期返回 payParams，储值在本次请求内完成结算。
     */
    @RequestMapping("/create")
    public R create(Long productId, Long storeId, Long coachId, Integer quantity, Integer payMethod,
                    Long memberCouponId, Integer marketingType, Long marketingActivityId,
                    HttpServletRequest request) {
        R check = checkOrderParams(productId, storeId, quantity);
        if (check != null) {
            return check;
        }
        if (coachId == null) {
            return R.reError("请选择教练");
        }
        int selectedPayMethod = payMethod == null ? 1 : payMethod;
        // 私教确认订单只开放微信、储值和分期；支付宝不属于本业务支付方式。
        if (selectedPayMethod != 1 && selectedPayMethod != 3 && selectedPayMethod != 4) {
            return R.reError("不支持的支付方式");
        }
        UserInfoVo user = getUserVo(request);
        return R.reOk(privateOrderService.create(user, productId, storeId, coachId, selectedPayMethod,
                memberCouponId, marketingType, marketingActivityId, request));
    }

    /** 待支付订单继续支付：复用原订单号和金额快照，不重复建单/占券 */
    @RequestMapping("/repay")
    public R repay(String orderNo, HttpServletRequest request) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return R.reError("缺少参数 orderNo");
        }
        UserInfoVo user = getUserVo(request);
        return R.reOk(privateOrderService.repay(user, orderNo.trim(), request));
    }

    /** 主动取消本人待支付订单，同事务释放占用券 */
    @RequestMapping("/cancel")
    public R cancel(String orderNo, HttpServletRequest request) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return R.reError("缺少参数 orderNo");
        }
        if (!privateOrderService.cancelUnpaid(getUserId(request), orderNo.trim())) {
            return R.reError("微信已支付，订单已自动确认，不能取消");
        }
        return R.reOk();
    }

    /** 客户端支付返回后主动向微信查单，并以服务端验签结果补偿异步回调 */
    @RequestMapping("/confirmWechatPay")
    public R confirmWechatPay(String orderNo, HttpServletRequest request) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return R.reError("缺少参数 orderNo");
        }
        return R.reOk(privateOrderService.confirmWechatPay(getUserId(request), orderNo.trim()));
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

    /** 我的私教权益分页，可选 status(1生效中/2已用完/3已过期/4已退款) */
    @RequestMapping("/myBenefits")
    public R myBenefits(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        params.put("userId", getUserId(request));
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(privateOrderService.myBenefits(params));
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
