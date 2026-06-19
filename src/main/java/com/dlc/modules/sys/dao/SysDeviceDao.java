package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysDeviceEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 我的设备
 * 
 * @author daibenting
 * @email 
 * @date 2018-09-13 10:16:12
 */
@Repository
public interface SysDeviceDao extends BaseDao<SysDeviceEntity> {

	List<Map<String, Object>> queryDeviceByUserId(Long userId);

    int updateUserInfoWrist(Map<String, Object> objectMap);

    int updateDevice(SysDeviceEntity sysDeviceEntity);

    int insertDeviceSelective(SysDeviceEntity sysDeviceEntity);

    int batchForbidden(Long[] deviceId);

    int batchUnForbidden(Long[] deviceIds);

    int queryIfExistDeciceNo(SysDeviceEntity device);

    int updateDeviceInit(String deviceNo);

    int updateWristIdInit(String deviceNo);

    int batchUpdateMemberStatus(@Param("deviceIds") Long[] deviceIds, @Param("status") int status);

    SysDeviceEntity queryDeviceById(@Param("userId") Long userId, @Param("storeAddressId")Long storeAddressId);

    SysDeviceEntity queryDeviceMemberById(@Param("userId") Long userId, @Param("storeAddressId")Long storeAddressId);

    SysDeviceEntity queryDeviceHistoryById(@Param("userId") Long userId, @Param("storeAddressId")Long storeAddressId);

    int updateUserStatusByUid(Long userId);

    int updateDeviceStatusById(SysDeviceEntity sysDeviceEntity);

    int updateDeviceValidate(SysDeviceEntity sysDeviceEntity);

    int updateDeviceSelective(SysDeviceEntity sysDeviceEntity);

    List<SysDeviceEntity> selectOverdueMember();

    int updateBatchDeviceOverTime(List<Long> deviceId);

    int updateBatchUserWristByUid(List<Long> proxyId);

    int selectOtherCard(@Param("userId") Long userId, @Param("storeId") Long storeId);

    int updateMemberValidity(Map<String, Object> params);

    int batchUpdateMemberStatusStart(Long[] deviceId);

    int updateValidityAndSta(Map<String, Object> params);

    int updateAutoPayStatus(SysDeviceEntity sysDeviceEntity);

    List getUserIdsByDeviceId(@Param("deviceIds") Long[] deviceIds);
    
    Map<String, Object> selectStoreStats(Map<String, Object> query);

}
