package com.dlc.modules.api.service;

public interface OrderTaskService {
    //检查更新所有未付款订单
    void selectAllCardOrderByStatus();

    /**
     * 第16步私教单分支:pt_private_order 待支付(order_status=0)超30分钟未付
     * → 条件 UPDATE 置3已取消(幂等) + 释放占用的优惠券(mk_member_coupon 3使用中→0未使用,条件CAS)。
     * 与既有卡/商城/私教课/团课分支互不影响,由 UpdateOrderStatusTask 同一 cron 触发。
     */
    void cancelTimeoutPrivateOrders();
}
