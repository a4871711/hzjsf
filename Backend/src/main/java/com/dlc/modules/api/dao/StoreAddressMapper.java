package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.StoreAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreAddressMapper {
    int deleteByPrimaryKey(Long storeAddrId);

    int insert(StoreAddress record);

    int insertSelective(StoreAddress record);

    StoreAddress selectByPrimaryKey(Long storeAddrId);

    int updateByPrimaryKeySelective(StoreAddress record);

    int updateByPrimaryKey(StoreAddress record);

    /**
     *  @Auther:YD
     *  @parameters:
     *  门店列表
     */
    List<Map<String,Object>> storeAddressList(Map<String, Object> params);

    int queryStoreAddressTotal(Map<String, Object> params);
    //门店详情里的门店位置和门店距离
    Map<String,Object> findAddAndDistance(Map<String,Object> params);

    Map<String,Object> findStoreByListDistance(Map<String, Object> params);

    StoreAddress findStoreAddressByStoreAddressId(Long storeAddrId);

    //用户与门店的距离
    int getDistanceByUser(@Param("storeId") Long storeId, @Param("userLng") Double userLng, @Param("userLat") Double userLat);

	List<Map<String, Object>> findStoreListByListDistance(Map<String, Object> map);

	List<Map<String, Object>> queryMyStoreList(Map<String, Object> params);

	Map<String, Object> queryStoreAddressByStoreId(long storeId);

	/** 按门店地址 id 批量查门店名(权益卡详情"适用哪些门店"展示) */
	List<String> selectStoreNamesByIds(@Param("ids") List<Long> ids);

}