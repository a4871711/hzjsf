package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateCoachScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 教练端固定周排班接口。教练身份只从登录 token 反查，不接收客户端 coachId。
 */
@RestController
@RequestMapping("/api/privateCoachSchedule")
public class PrivateCoachScheduleController extends BaseController {

    @Autowired
    private PrivateCoachScheduleService privateCoachScheduleService;

    /** 当前教练所属门店与全部周排班。 */
    @RequestMapping("/list")
    public R list(HttpServletRequest request) {
        return R.reOk(privateCoachScheduleService.list(getUserId(request)));
    }

    /** 新增一个星期、一个门店下的排班段。 */
    @RequestMapping("/save")
    public R save(Long storeId, Integer weekday, String startTime, String endTime,
                  Integer isEnabled, HttpServletRequest request) {
        privateCoachScheduleService.save(getUserId(request), storeId, weekday,
                startTime, endTime, isEnabled);
        return R.reOk();
    }

    /** 修改本人排班段的门店、星期与起止时间。 */
    @RequestMapping("/update")
    public R update(Long id, Long storeId, Integer weekday, String startTime,
                    String endTime, HttpServletRequest request) {
        privateCoachScheduleService.update(getUserId(request), id, storeId,
                weekday, startTime, endTime);
        return R.reOk();
    }

    /** 启用或停用本人排班段。 */
    @RequestMapping("/changeEnabled")
    public R changeEnabled(Long id, Integer isEnabled, HttpServletRequest request) {
        privateCoachScheduleService.changeEnabled(getUserId(request), id, isEnabled);
        return R.reOk();
    }

    /** 删除本人排班段；已被未来预约占用时仍由共用 Service 拦截。 */
    @RequestMapping("/delete")
    public R delete(Long id, HttpServletRequest request) {
        privateCoachScheduleService.delete(getUserId(request), id);
        return R.reOk();
    }
}
