package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateAppointmentService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 私教预约(移动端):coaches 可约教练 / slots 可约时段 / book 预约 / cancel 取消 / myList 我的预约,全部需登录。
 * 容量并发护栏与课时冻结在 service 单事务内完成;核销(finish)与教练代约(coachBook)仅提供 service 方法,
 * 由 sys 后台(第15步)委托调用,api 端不暴露。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/privateAppointment")
public class PrivateAppointmentController extends BaseController {

    @Autowired
    private PrivateAppointmentService privateAppointmentService;

    /** 本人某权益可约教练列表;benefitId 须属本人且生效中 */
    @RequestMapping("/coaches")
    public R coaches(Long benefitId, HttpServletRequest request) {
        if (benefitId == null) {
            return R.reError("缺少参数 benefitId");
        }
        Long userId = getUserId(request);
        return R.reOk(privateAppointmentService.coaches(userId, benefitId));
    }

    /**
     * 某教练某日可约时段(含余量);storeId 可选,不传则返回该商品适用门店 ∩ 教练门店的全部窗口。
     * 余量口径与 book 下单护栏共用同一条 COUNT SQL。
     */
    @RequestMapping("/slots")
    public R slots(Long benefitId, Long coachId, Long storeId, String date, HttpServletRequest request) {
        if (benefitId == null || coachId == null || date == null || date.trim().isEmpty()) {
            return R.reError("缺少参数 benefitId/coachId/date");
        }
        Long userId = getUserId(request);
        return R.reOk(privateAppointmentService.slots(userId, benefitId, coachId, storeId, date.trim()));
    }

    /**
     * 预约:单事务内完成 权益校验+时段合法+容量并发护栏(路线A行锁+路线B唯一键)+冻结课时。
     * 返回 {appointmentId, appointmentNo}。
     */
    @RequestMapping("/book")
    public R book(Long benefitId, Long coachId, Long storeId, String date,
                  String startTime, String endTime, HttpServletRequest request) {
        if (benefitId == null || coachId == null || storeId == null
                || isBlank(date) || isBlank(startTime) || isBlank(endTime)) {
            return R.reError("缺少参数 benefitId/coachId/storeId/date/startTime/endTime");
        }
        // 登录 + 封禁校验(下单/预约用 getUserVo)
        UserInfoVo user = getUserVo(request);
        return R.reOk(privateAppointmentService.book(user.getUserId(), benefitId, coachId, storeId,
                date.trim(), startTime.trim(), endTime.trim()));
    }

    /** 取消预约:仅本人、仅已预约状态;距开课不足无责取消窗口直接拒绝(ERROR_CANCEL_WINDOW) */
    @RequestMapping("/cancel")
    public R cancel(String appointmentNo, String cancelReason, HttpServletRequest request) {
        if (isBlank(appointmentNo)) {
            return R.reError("缺少参数 appointmentNo");
        }
        Long userId = getUserId(request);
        privateAppointmentService.cancelByMember(userId, appointmentNo.trim(), cancelReason);
        return R.reOk();
    }

    /** 我的预约分页,可选 status 过滤(1已预约/2已取消/3已完成/4爽约) */
    @RequestMapping("/myList")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        // page/limit 规范成正整数:挡住 0/负数/非数字脏参数,否则 Query 算出负 offset → SQL 报错
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(privateAppointmentService.myList(params));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
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
