package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.GymEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/10/010
 */
@RestController
@RequestMapping("/api/gymEngine")
public class GymEngineController {

    @Autowired
    private GymEngineService gymEngineService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  查询器械一级分类列表
     */
    @RequestMapping("/list")
    public R queryGymEngineList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = gymEngineService.queryGymEngineList(query);
        int total = gymEngineService.queryTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  查询器械列表
     */
    @RequestMapping("/gymSecondList")
    public R queryGymEngineSecondList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = gymEngineService.querySecondGymEngineList(query);
        int total = gymEngineService.querySecondTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }


    /**
     *  @Auther:YD
     *  @parameters:
     *  器械详情查询
     */
    @RequestMapping("/info")
    public R queryGymEngineInfo(Long id){
        Map<String,Object> map = gymEngineService.queryGymEngineInfo(id);
        if (map != null){
            return R.reOk(map);
        }else {
            return R.reError("loading...");
        }
    }

}
