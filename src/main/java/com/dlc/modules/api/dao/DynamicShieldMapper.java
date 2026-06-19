package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.DynamicShield;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DynamicShieldMapper {
    int deleteByPrimaryKey(Long shieldId);

    int insert(DynamicShield record);

    int insertSelective(DynamicShield record);

    DynamicShield selectByPrimaryKey(Long shieldId);

    int updateByPrimaryKeySelective(DynamicShield record);

    int updateByPrimaryKey(DynamicShield record);
}