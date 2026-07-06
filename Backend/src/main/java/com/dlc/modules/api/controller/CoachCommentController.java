package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.CoachCommentApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 教练评价（移动端，第21步·运营域）。路径 /api/coachComment。
 * 会员在私教课完成后评价教练：submit 提交、myList 我的评价（含后台回复），全部需登录（getUserId）。
 * 评价的「产生」按勘误#7 归运营域（本步），不在交易域。
 * ⚠️ 关联的 mapper/api/CoachCommentApiDao.xml 改动须重启 Tomcat。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/coachComment")
public class CoachCommentController extends BaseController {

    @Autowired
    private CoachCommentApiService coachCommentApiService;

    /**
     * 提交评价：{token, appointmentId, score(1-5), commentContent}。
     * 校验预约属本人且已完成（3）；一预约一评价（重复 → ERROR_COMMENT_EXISTS）。
     */
    @RequestMapping("/submit")
    public R submit(Long appointmentId, Integer score, String commentContent, HttpServletRequest request) {
        if (appointmentId == null) {
            return R.reError("缺少参数 appointmentId");
        }
        Long userId = getUserId(request);
        coachCommentApiService.submit(userId, appointmentId, score, commentContent);
        return R.reOk();
    }

    /** 我的评价分页（含后台回复）；page/limit 规范成正整数，挡住脏参数导致的负 offset */
    @RequestMapping("/myList")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(coachCommentApiService.myList(params));
    }

    /** 把入参解析为正整数；null / 非数字 / <=0 一律取默认值 */
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
