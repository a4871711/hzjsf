package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.controller.UserInfoController;
import com.dlc.modules.api.dao.StoreDeviceV2Mapper;
import com.dlc.modules.api.entity.StoreDeviceV2;
import com.dlc.modules.api.service.StoreDeviceV2Service;
import com.dlc.modules.api.vo.RidoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门店设备
 */

@Service
@Transactional
public class StoreDeviceV2ServiceImpl implements StoreDeviceV2Service {

    @Autowired
    private StoreDeviceV2Mapper storeDeviceV2Mapper;

    final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    /**
     * 获取设备信息
     */
    @Override
    public StoreDeviceV2 queryDeviceKey(String key){
        return storeDeviceV2Mapper.queryDeviceKey(key);
    }


    /**
     * 查询设备信息
     */
    @Override
    public StoreDeviceV2 queryDeviceByDeviceId(String deviceId){
        return storeDeviceV2Mapper.queryDeviceByDeviceId(deviceId);
    }

    /**
     * 查询设备信息
     */
    @Override
    public RidoVo queryRidoDeviceByDeviceId(String deviceId){
        return storeDeviceV2Mapper.queryRidoDeviceByDeviceId(deviceId);
    }

    /**
     * 查询设备信息
     */
    @Override
    public RidoVo queryRidoDeviceByDeviceUserId(String userId, String source){
        return storeDeviceV2Mapper.queryRidoDeviceByDeviceUserId(userId, source);
    }

    /**
     * 更新设备运行状态
     */
    @Override
    public int setRidoDeviceState(String deviceId, int state, Long userId){
        System.out.println(deviceId +"/"+ state +"/"+ userId );
        return storeDeviceV2Mapper.setRidoDeviceState(deviceId, state, userId);
    }

    @Override
    public List<Map<String,Object>> queryDeviceList(Query query) {
        return storeDeviceV2Mapper.queryDeviceList(query);
    }

    @Override
    public int queryDeviceTotal(Query query) {
        return storeDeviceV2Mapper.queryDeviceTotal(query);
    }

    @Override
    public int addDevice(Map device) {
        System.out.println("添加数据" + device);
        return storeDeviceV2Mapper.addDevice(device);
    }

    @Override
    public int updateDevice(Map device) {
        System.out.println("更新数据" + device);
        return storeDeviceV2Mapper.updateDevice(device);
    }

    @Override
    public String getDeviceIdFromDecode(String decodeId){
        byte[] decodeBytes = Base64.getDecoder().decode(decodeId);
        String decodeString = new String(decodeBytes);
        int pos = decodeString.indexOf("|");
        String deviceId = decodeString.substring(0, pos);
        logger.info("设备ID：{}，解码后ID：{}", decodeString, deviceId);
        return deviceId;
    }

}
