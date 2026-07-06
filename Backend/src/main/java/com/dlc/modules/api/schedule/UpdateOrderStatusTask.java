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
        //私教购买单(第16步,pt_private_order):待支付超30分钟置3已取消+释放占用优惠券;
        //独立 try/catch + 独立 service 方法(独立事务),不影响上面既有卡/商城/私教课/团课分支。
        //改本类或 OrderTaskServiceImpl 须重启 Tomcat 生效。
        try {
            orderTaskService.cancelTimeoutPrivateOrders();
        } catch (Exception e) {
            log.error("私教订单超时取消分支异常", e);
        }
        log.info("定时任务更新订单状态end...");
    }
}
