package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateCoachCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/** 教练端“我的”：本人资料与私教收入，只按登录 token 识别教练。 */
@RestController
@RequestMapping("/api/privateCoachCenter")
public class PrivateCoachCenterController extends BaseController {

    @Autowired
    private PrivateCoachCenterService privateCoachCenterService;

    /** 教练“我的”首页数据。 */
    @RequestMapping("/mine")
    public R mine(HttpServletRequest request) {
        return R.reOk(privateCoachCenterService.mine(getUserId(request)));
    }

    /** 当前普通私教负责的会员；归属只认会员权益上的服务教练。 */
    @RequestMapping("/members")
    public R members(Integer page, Integer limit, String keyword,
                     HttpServletRequest request) {
        return R.reOk(privateCoachCenterService.members(
                getUserId(request), page, limit, keyword));
    }

    /** 修改本人可自助维护的基础资料，不开放等级、门店、状态和绑定关系。 */
    @RequestMapping("/updateProfile")
    public R updateProfile(String coachName, String mobile, Integer gender,
                           String avatarUrl, String intro, HttpServletRequest request) {
        privateCoachCenterService.updateProfile(getUserId(request), coachName,
                mobile, gender, avatarUrl, intro);
        return R.reOk();
    }

    /** 私教收入明细；month 可选 yyyy-MM，type 可选 lesson、sale。 */
    @RequestMapping("/incomeList")
    public R incomeList(Integer page, Integer limit, String type, String month,
                        HttpServletRequest request) {
        return R.reOk(privateCoachCenterService.incomeList(
                getUserId(request), page, limit, type, month));
    }

    /** 教练提现记录和当前可提现余额；银行卡号只返回后四位。 */
    @RequestMapping("/withdrawalList")
    public R withdrawalList(Integer page, Integer limit, String month,
                            HttpServletRequest request) {
        return R.reOk(privateCoachCenterService.withdrawalList(
                getUserId(request), page, limit, month));
    }

    /** 发起提现只接受金额和收款账户信息，coachId 一律从登录 token 反查。 */
    @RequestMapping("/withdrawalApply")
    public R withdrawalApply(BigDecimal amount, String accountName, String bankName,
                             String bankCardNo, HttpServletRequest request) {
        privateCoachCenterService.applyWithdrawal(getUserId(request), amount,
                accountName, bankName, bankCardNo);
        return R.reOk();
    }
}
