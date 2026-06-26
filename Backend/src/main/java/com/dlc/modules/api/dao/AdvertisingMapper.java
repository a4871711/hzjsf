package com.dlc.modules.api.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/11 11:01
 */
@Mapper
@Repository
public interface AdvertisingMapper {

    Map<String,Object>  selectByPrimaryKey(@Param("advId") Long advId);

    List<Map<String,Object>> advertisingList(Integer advType);

    Map<String,Object> customizationAdv();
}
