package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PtInstallmentService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 会员端私教分期 API(第20步资金域)。路径 /api/ptInstallment。
 * <p>身份用 BaseController.getUserId/getUserVo;返回 R.reOk(data)/R.reError。
 * 分期下单(首付)走交易域下单接口(pay_method=4),本控制器只管"我的分期/账单/付某期"。</p>
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/ptInstallment")
public class ApiPtInstallmentController extends BaseController {

    @Autowired
    private PtInstallmentService ptInstallmentService;

    /**
     * 我的分期计划分页(倒序)。入参 page/limit。
     */
    @RequestMapping("/myList")
    public R myList(Integer page, Integer limit, HttpServletRequest request) {
        Long userId = getUserId(request);
        PageUtils pageUtil = ptInstallmentService.myList(userId, page, limit);
        return R.reOk(pageUtil);
    }

    /**
     * 分期计划详情:计划汇总 + 全部账单期明细(含逾期标记、可付期)。入参 planId。
     */
    @RequestMapping("/detail")
    public R detail(Long planId, HttpServletRequest request) {
        Long userId = getUserId(request);
        Map<String, Object> data = ptInstallmentService.detail(userId, planId);
        return R.reOk(data);
    }

    /**
     * 支付某一期账单(生成后续期微信单,后缀 a),返回调起参数。入参 billId。
     * 需登录且校验封禁(付款属资金动作)。
     */
    @RequestMapping("/payBill")
    public R payBill(Long billId, HttpServletRequest request) {
        UserInfoVo user = getUserVo(request);
        Map<String, Object> data = ptInstallmentService.payBill(user.getUserId(), billId, request);
        return R.reOk(data);
    }
}
