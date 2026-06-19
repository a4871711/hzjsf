package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.AppointClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 约课
 * @author LINNINGXIA
 * @version 1.0
 * @date 2018-09-15 11:06
 */
@RestController
@RequestMapping("/api/appointClass")
public class ApiAppointClassController extends BaseController {

    @Autowired
    private AppointClassService appointClassService;

    /**
     * 发起预约
     * @return
     */
    @RequestMapping("/appoint")
    public R appoint(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(StringUtils.isEmpty(params.get("classId")) || StringUtils.isEmpty(params.get("coachId"))
                || StringUtils.isEmpty(params.get("spsId"))){
            return R.reError("参数缺失");
        }
        if(StringUtils.isEmpty(params.get("storeAddrId")) || StringUtils.isEmpty(params.get("storeName"))){
            return R.reError("请选择上课门店");
        }
        if(StringUtils.isEmpty(params.get("dataTime")) || StringUtils.isEmpty(params.get("dataJsonArray"))){
            return R.reError("请选择上课时间");
        }

        return appointClassService.appointClass(params, getUserId(request));
    }

    /**
     * 删除约课
     * @param spsId
     * @param request
     * @return
     */
    @RequestMapping("/appointDel")
    public R appointDel(Long spsId, HttpServletRequest request){
        if(spsId == null){
            return R.reError("参数缺失");
        }
        return appointClassService.appointDel(spsId);
    }
    /**
     * 取消约课
     */
    @RequestMapping("/appointCancel")
    public R appointCancel(Long arrangeClassId, HttpServletRequest request){
        if(arrangeClassId == null){
            return R.reError("参数缺失");
        }
        return appointClassService.appointCancel(arrangeClassId);
    }
    /**
     * 参数校验
     * @param params
     */
    private R validata(Map<String, Object> params) {
        if(StringUtils.isEmpty(params.get("classId")) || StringUtils.isEmpty(params.get("coachId"))
                || StringUtils.isEmpty(params.get("spsId"))){
            return R.reError("参数缺失");
        }
        if(StringUtils.isEmpty(params.get("storeAddrId")) || StringUtils.isEmpty(params.get("storeName"))){
            return R.reError("请选择上课门店");
        }
        if(StringUtils.isEmpty(params.get("dataTime")) || StringUtils.isEmpty(params.get("dataJsonArray"))){
            return R.reError("请选择上课时间");
        }
        return null;
    }

}
