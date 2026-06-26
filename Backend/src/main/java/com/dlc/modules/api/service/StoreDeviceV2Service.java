package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StoreDeviceV2;
import com.dlc.modules.api.vo.RidoVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门店设备
 */
public interface StoreDeviceV2Service {

    StoreDeviceV2 queryDeviceKey(String key);

    StoreDeviceV2 queryDeviceByDeviceId(String deviceId);

    RidoVo queryRidoDeviceByDeviceId(String deviceId);

    RidoVo queryRidoDeviceByDeviceUserId(String userId, String source);

    int setRidoDeviceState(String deviceId, int state, Long userId);

    List<Map<String,Object>> queryDeviceList(Query query);

    int queryDeviceTotal(Query query);

    int addDevice(Map device);

    int updateDevice(Map device);

    /**
     * 解密舒华设备ID
     * @param decodeId
     * @return
     */
    String getDeviceIdFromDecode(String decodeId);

}
