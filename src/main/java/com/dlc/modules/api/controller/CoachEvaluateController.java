package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.CoachEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
@RestController
@RequestMapping("/api/coachEva")
public class CoachEvaluateController {

    @Autowired
    private CoachEvaluateService coachEvaluateService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  评价条数
     */
    @RequestMapping("/countCoachEva")
    public R countCoachEva(Long coachId){
        //int total = coachEvaluateService.countCoachEva(coachId);
        int total = coachEvaluateService.countStoreCoachEva(coachId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("EvaNum",total);
        return R.reOk(jsonObject);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  教练评价列表
     */
    @RequestMapping("/coachEvaList")
    public R coachEvaList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        //List<Map<String,Object>> list = coachEvaluateService.coachEvaList(query);
        //int total = coachEvaluateService.queryTotal(query);
        List<Map<String,Object>> list = coachEvaluateService.storeCoachEvaList(query);
        int total = coachEvaluateService.queryStoreCoachTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }
}
