package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.CoachAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
@RestController
@RequestMapping("/api/coachApc")
public class CoachAppointmentController {

    @Autowired
    private CoachAppointmentService coachAppointmentService;
    /**
     *  @Auther:YD
     *  @parameters:
     *  可约时间
     */
    @RequestMapping("/reducibleTime")
    public R reducibleTime(Long coachId){
        return R.reOk(coachAppointmentService.reducibleTime(coachId));
    }
}
