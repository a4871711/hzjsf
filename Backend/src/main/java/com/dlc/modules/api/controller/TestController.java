package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.WxPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author jiangkang
 * @Date 2022/8/8 12:49
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private Logger log =  LoggerFactory.getLogger(getClass());
    @Autowired
    private WxPayService wxPayService;

    /**
     *  自动续费扣款调试
     */
    @RequestMapping("/pay")
    public void testPay(@RequestParam Map<String,Object> params){
//        wxPayService.papAutoPay();
    }

}
