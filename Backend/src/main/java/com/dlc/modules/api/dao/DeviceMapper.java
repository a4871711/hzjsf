package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DeviceMapper {
    int deleteByPrimaryKey(Long deviceId);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(Long deviceId);

    Device queryHaveDeviceById(@Param("userId") Long userId, @Param("storeAddressId") Long storeAddressId);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);

    int queryIsForbidden(Long userId);

    int updateValiditByUid(Device record);

    Map<String,Object> selectByProxyId(Long userId);

    int queryDeviceIsForbidden(@Param("userId") Long userId, @Param("deviceNo") String deviceNo);

    int queryIsCardUserById(Long userId);

    int updateInOutNumByUId(@Param("userId") Long userId, @Param("deviceNo") String deviceNo);

    List<Long> queryIsForbiddenByIds(List<Long> list);

    List<Device> selectAutoPayUser();

    //用户二维码检查用户会员卡有效期
    Long checkUserValidity(Long userId);

	Device selectUserValidity(Long userId);
	Device selectByUser(Long userId);
	/** 用户当前全部有效(状态正常且未过期)的会员卡实例;会员可能同时持有多张(如次卡+月卡并存) */
	List<Device> selectAllValidByUser(Long userId);

	void updateCountByPrimaryKey(Long deviceId);

	/** 购卡后更新门店适用范围（支持 storeId 置 null） */
	int updateStoreScope(@Param("deviceId") Long deviceId,
	                     @Param("storeId") Long storeId,
	                     @Param("storeAddrIds") String storeAddrIds);
}