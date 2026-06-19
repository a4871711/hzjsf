package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.WxPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自动续费定时器轮询--暂定每隔15分钟一次
 * 扣款时间北京时间每天6:00～22:00 24小时扣费规则执行
 */
public class AutoPayTask {

    private Logger log =  LoggerFactory.getLogger(getClass());
    @Autowired
    private WxPayService wxPayService;

    public void papAutoPay(){
//        log.info("测试环境，不执行自动续费定时器轮询..");

        log.info("新版本自动续费定时器轮询start...");
        //健身卡订单-自动续费-提前24小时
        try {
            wxPayService.papAutoPay();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("自动续费定时器轮询end...");
    }
}
