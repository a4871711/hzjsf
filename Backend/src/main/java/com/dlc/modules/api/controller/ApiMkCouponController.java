package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.MkCouponService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * 私教营销券(移动端,第18步):领券 / 我的券 / 下单可用券试算。全部需登录。
 *
 * 命名规避:项目已有旧 {@code ApiCouponController} 占用 /api/coupon(Coupon/user_coupon 旧券体系,不可动),
 * 本控制器专管私教营销券(mk_coupon 体系),独立路径 /api/mkCoupon,与旧券互不干扰。
 * 身份走 BaseController.getUserId/getUserVo(token 见 header 或参数,uniapp 约定);返回 R.reOk/reError。
 *
 * 下单链路(占券/核销/释放)不在本控制器,归交易域私教下单接口 + PtPrivateOrderCouponRelDao;
 * 本控制器只做「领券 + 查询 + 可用券试算」,抵扣算法委托 MkCouponService(§5.1)。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/mkCoupon")
public class ApiMkCouponController extends BaseController {

    @Autowired
    private MkCouponService mkCouponService;

    /**
     * 可领券列表:入参 storeId(可选,门店过滤)。返回 status=1 上架且门店匹配的券。
     */
    @RequestMapping("/canReceiveList")
    public R canReceiveList(Long storeId, HttpServletRequest request) {
        Long memberId = getUserId(request);
        return R.reOk(mkCouponService.canReceiveList(memberId, storeId));
    }

    /**
     * 领券:入参 couponId。校验上架+未删,写 mk_member_coupon(expire_time=now+valid_days,use_status=0)。
     */
    @RequestMapping("/receive")
    public R receive(Long couponId, HttpServletRequest request) {
        Long memberId = getUserId(request);
        mkCouponService.receive(memberId, couponId);
        return R.reOk();
    }

    /**
     * 我的券:入参 useStatus(可选,0未使用/1已使用/2已过期/3使用中过滤;空=全部)。
     */
    @RequestMapping("/myList")
    public R myList(Integer useStatus, HttpServletRequest request) {
        Long memberId = getUserId(request);
        return R.reOk(mkCouponService.myList(memberId, useStatus));
    }

    /**
     * 下单可用券试算:入参 productId/storeId/amount(应付金额)/marketingType(可选,活动单返空)。
     * 返回该会员对该商品/门店/金额可用的券,每张含 calcDiscountAmount(试算抵扣)。
     */
    @RequestMapping("/usableForOrder")
    public R usableForOrder(Long productId, Long storeId, BigDecimal amount,
                            Integer marketingType, HttpServletRequest request) {
        UserInfoVo user = getUserVo(request);
        return R.reOk(mkCouponService.usableForOrder(user, productId, storeId, amount, marketingType));
    }
}
