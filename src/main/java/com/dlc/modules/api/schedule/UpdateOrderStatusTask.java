package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.OrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 更新订单状态定时任务
 */
public class UpdateOrderStatusTask {

    private Logger log =  LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderTaskService orderTaskService;

    public void updateOrderStatus(){
        log.info("定时任务更新订单状态start...");
        //健身卡订单
        try {
            orderTaskService.selectAllCardOrderByStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("定时任务更新订单状态end...");
    }
}
