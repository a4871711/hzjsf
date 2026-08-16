package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateCoachCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    /** 修改本人可自助维护的基础资料，不开放等级、门店、状态和绑定关系。 */
    @RequestMapping("/updateProfile")
    public R updateProfile(String coachName, String mobile, Integer gender,
                           String avatarUrl, String intro, HttpServletRequest request) {
        privateCoachCenterService.updateProfile(getUserId(request), coachName,
                mobile, gender, avatarUrl, intro);
        return R.reOk();
    }

    /** 私教收入明细；type 可选 lesson、sale，不传表示全部。 */
    @RequestMapping("/incomeList")
    public R incomeList(Integer page, Integer limit, String type, HttpServletRequest request) {
        return R.reOk(privateCoachCenterService.incomeList(
                getUserId(request), page, limit, type));
    }
}
