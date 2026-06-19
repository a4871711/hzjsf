package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StoreDeviceV2;
import com.dlc.modules.api.vo.RidoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface StoreDeviceV2Mapper {

    StoreDeviceV2 queryDeviceKey(String key);

    StoreDeviceV2 queryDeviceByDeviceId(String deviceId);

    RidoVo queryRidoDeviceByDeviceId(String deviceId);

    RidoVo queryRidoDeviceByDeviceUserId(@Param("userId") String userId, @Param("source") String source);

    int setRidoDeviceState(@Param("deviceId") String deviceId, @Param("state") int state, @Param("userId") Long userId  );

    List<Map<String,Object>> queryDeviceList(Query query);

    int queryDeviceTotal(Query query);

    int addDevice(Map device);

    int updateDevice(Map device);

}
