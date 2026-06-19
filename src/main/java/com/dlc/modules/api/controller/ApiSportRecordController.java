package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.SportActive;
import com.dlc.modules.api.service.AboutUsService;
import com.dlc.modules.api.service.SportRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/17 9:06
 * 运动记录
 */
@RestController
@RequestMapping("/api/sport")//sport
public class ApiSportRecordController extends BaseController{
    @Autowired
    private SportRecordService sportRecordService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AboutUsService aboutUsService;
    /**
     * 今日步数
     * */
    /*@RequestMapping("/todaySportStep")
    public R todaySportStep(HttpServletRequest request){
        //Long userId = getUserVo(request).getUserId();
        Long userId = 2L;//测试写死
        Integer sportStep = sportRecordService.queryTodaySportStep(userId);
        return R.reOk(sportStep);
    }*/

    /**
     * 团课/私教 已经完成课程总节数  活跃天数
     * */
    @RequestMapping("/sportRecord")
    public R sportRecord(HttpServletRequest request){
        Long userId = getUserVo(request).getUserId();
        Map<String,Object> map = this.sportRecordService.querySportRecord(userId);

        if(map==null){
            map = new HashMap<>();
            map.put("createdDate",0);
            map.put("activity",0);
            map.put("teamClassKcal",0);
            map.put("teamClassCount",0);
            map.put("privateClassCount",0);
            map.put("privateClassKcal",0);
        }
        return R.reOk(map);
    }

    /**
     * 运动分类列表
     * */
    @RequestMapping("/sportTypeList")
    public R sportTypeList(Integer sportType,HttpServletRequest request){
        Long userId = getUserVo(request).getUserId();
        String brandLogoUrl =  aboutUsService.queryBrandLogo();
        Map<String,Object> map = this.sportRecordService.querySportListByType(sportType,userId);
        map.put("brandLogoUrl",brandLogoUrl);
        return R.reOk(map);
    }

    /**
     * 数据统计
     * */
    @RequestMapping("/sportDataCount")
    public R sportDataCount(SportActive sportActive, HttpServletRequest request){
        Long userId = getUserVo(request).getUserId();
        sportActive.setUserId(userId);
        JSONObject jsonObject = this.sportRecordService.sportDataCount(sportActive);

        if(null==jsonObject){
            return R.reOk(null);
        }
        return R.reOk(jsonObject);
    }

    /**
     * 运动数据保存
     * */
    @RequestMapping("/saveSportData")
    public R saveSportData(@RequestParam Map<String,Object> map, HttpServletRequest request){
        log.info("进入运动数据保存接口"+">>>");
        Long userId = getUserVo(request).getUserId();
        map.put("userId",userId);
        int reult = this.sportRecordService.saveSportData(map);
        if(reult>0){
            log.info("进入运动数据保存成功"+">>>");
            return R.reOk();
        }
        return R.reError("保存数据失败");
    }

    /**
     * 保存今日步数（调用硬件接口）
     * */
    /*@RequestMapping("/todaySteps")
    public R todaySteps(String deviceNo){
        Integer result = this.sportRecordService.todaySteps(deviceNo);
        return R.reOk(result);
    }*/
}
