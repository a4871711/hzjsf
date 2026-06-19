package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-29 20:48
 */
public interface PrivateClassOrderService {
    List<Map<String,Object>> queryOrderList(Query query);

    int queryOrderListTotal(Query query);

    Map<String,Object> queryPrivateClassDetail(Long privateClassOrderId);
}
