package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ShareMapper {
    int deleteByPrimaryKey(Long shareId);

    int insert(Share record);

    int insertSelective(Share record);

    Share selectByPrimaryKey(Long shareId);

    int updateByPrimaryKeySelective(Share record);

    int updateByPrimaryKey(Share record);

    Map<String,Object> share(@Param("shareType") Long shareType, @Param("userId") Long userId);
}