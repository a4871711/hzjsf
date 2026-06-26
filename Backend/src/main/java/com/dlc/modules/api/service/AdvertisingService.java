package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.Advertising;

import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/11 10:59
 */
public interface AdvertisingService {

    List<Map<String,Object>> advertisingList(Advertising advertising);


    Map<String,Object>  advertisingDetails(Long advId);

    Map<String,Object> customizationAdv(Long userId);

}
