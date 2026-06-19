package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.DataMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/17/017
 *
 */
@RestController
@RequestMapping("/api/dataMap")//dataMap
public class DataMapController {

    @Autowired
    private DataMapService dataMapService;
    /**
     *  @Auther:YD
     *  @parameters:
     *  查询手环价格
     */
    @RequestMapping("/braceletPrice")
    public R findBraceletPrice(){
        return R.reOk(dataMapService.findBraceletPrice());
    }
}
