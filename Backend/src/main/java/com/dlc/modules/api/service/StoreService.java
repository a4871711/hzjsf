package com.dlc.modules.api.service;

import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
public interface StoreService {


    Map<String,Object> queryStoreInfo(Map<String,Object> params);

    Map<String,Object> recommendedStores(Map<String, Object> params);

    String queryStoreName(Long id);
    /*定时任务删除一周之前的刷脸数据*/
    int deleteFaceRecord();
}
