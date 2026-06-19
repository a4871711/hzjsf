package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.StoreDevice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StoreDeviceMapper {
    int deleteByPrimaryKey(Long sdId);

    int insert(StoreDevice record);

    int insertSelective(StoreDevice record);

    StoreDevice selectByPrimaryKey(Long sdId);

    int updateByPrimaryKeySelective(StoreDevice record);

    int updateByPrimaryKey(StoreDevice record);
}