package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StoreMapper {
    int deleteByPrimaryKey(Long storeId);

    int insert(Store record);

    int insertSelective(Store record);

    Store selectByPrimaryKey(Long storeId);

    int updateByPrimaryKeySelective(Store record);

    int updateByPrimaryKey(Store record);


    Map<String,Object> queryStoreInfo(Long id);

    String queryStoreName(Long storeId);
}