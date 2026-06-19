package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.AdvertisingMapper;
import com.dlc.modules.api.dao.SportRecordMapper;
import com.dlc.modules.api.entity.Advertising;
import com.dlc.modules.api.service.AdvertisingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/11 10:59
 */
@Service
public class AdvertisingServiceImpl implements AdvertisingService {
    @Autowired
    private AdvertisingMapper advertisingMapper;
    @Autowired
    private SportRecordMapper sportRecordMapper;

    public List<Map<String, Object>> advertisingList(Advertising advertising) {
        //Long advId = advertising.getAdvId();
        Integer advType = advertising.getAdvType();
        return advertisingMapper.advertisingList(advType);
    }


    public Map<String,Object>  advertisingDetails(Long advId) {
        return advertisingMapper.selectByPrimaryKey(advId);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  定制广告
     * @param userId
     */
    @Override
    public Map<String, Object> customizationAdv(Long userId) {
        //add new to:首页统计活跃天数+1 不影响原流程在此对异常捕获
        try {
            sportRecordMapper.updateActivity(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //add end
        Map<String,Object> map = advertisingMapper.customizationAdv();
        if (map != null){
            return map;
        }else {
            return new HashMap<>();
        }
    }
}
