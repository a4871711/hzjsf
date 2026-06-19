package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.ActivityTrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/10/010
 * 动作训练
 */
@RestController
@RequestMapping("/api/activityTrain")
public class ActivityTrainController {
    @Autowired
    private ActivityTrainService activityTrainService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  动作一级分类列表查询
     */
    @RequestMapping("/list")
    public R queryActivityTrainList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = activityTrainService.queryActivityTrainList(query);
        int total = activityTrainService.queryTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  动作2级分类列表查询
     */
    @RequestMapping("/trainSecondList")
    public R queryTrainSecondList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = activityTrainService.querySecondActivityTrainList(query);
        int total = activityTrainService.querySecondActivityTrainTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  动作训练详情
     */
    @RequestMapping("/info")
    public R queryActivityTrainInfo(Long id){
        Map<String,Object> map = activityTrainService.queryActivityTrainInfo(id);
        if (map != null){
            return R.reOk(map);
        }else {
            return R.reError("loading...");
        }
    }
}
