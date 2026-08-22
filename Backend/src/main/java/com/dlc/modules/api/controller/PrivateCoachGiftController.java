package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateCoachGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** 自由教练赠课接口；教练身份只从登录 token 绑定账号解析。 */
@RestController
@RequestMapping("/api/privateCoachGift")
public class PrivateCoachGiftController extends BaseController {

    @Autowired
    private PrivateCoachGiftService privateCoachGiftService;

    @RequestMapping(value = "/giftableBenefits", method = RequestMethod.POST)
    public R giftableBenefits(HttpServletRequest request) {
        return R.reOk(privateCoachGiftService.giftableBenefits(getUserId(request)));
    }

    @RequestMapping(value = "/memberLookup", method = RequestMethod.POST)
    public R memberLookup(String keyword, HttpServletRequest request) {
        return R.reOk(privateCoachGiftService.lookupMember(getUserId(request), keyword));
    }

    @RequestMapping(value = "/gift", method = RequestMethod.POST)
    public R gift(Long sourceBenefitId, Long toMemberId, Integer lessonCount,
                  String requestNo, HttpServletRequest request) {
        return R.reOk(privateCoachGiftService.gift(getUserId(request), sourceBenefitId,
                toMemberId, lessonCount, requestNo));
    }

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    public R history(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        params.put("page", normalizePositive(params.get("page"), 1));
        params.put("limit", normalizePositive(params.get("limit"), 10));
        return R.reOk(privateCoachGiftService.history(getUserId(request), params));
    }

    private String normalizePositive(Object value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(String.valueOf(value));
            return String.valueOf(parsed > 0 ? parsed : defaultValue);
        } catch (Exception e) {
            return String.valueOf(defaultValue);
        }
    }
}
