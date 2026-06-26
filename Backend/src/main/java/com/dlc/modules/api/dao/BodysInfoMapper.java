package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.BodyDimension;
import com.dlc.modules.api.entity.BodyStatus;
import com.dlc.modules.api.entity.BodysInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface BodysInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BodysInfo record);

    int insertSelective(BodysInfo record);

    BodysInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BodysInfo record);

    int updateByPrimaryKey(BodysInfo record);

    BodysInfo queryBodyInfoByUserId(Map<String,Object> map);

    Map<String,Object> queryBodyInfoLastRecord(Long userId);

    BodyDimension queryBodyDimensionByUserId(Map<String, Object> param);

    BodyStatus queryBodyStatusByUserId(Map<String, Object> param);
}