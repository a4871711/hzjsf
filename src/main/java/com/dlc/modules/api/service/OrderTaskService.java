package com.dlc.modules.api.service;

public interface OrderTaskService {
    //检查更新所有未付款订单
    void selectAllCardOrderByStatus();
}
