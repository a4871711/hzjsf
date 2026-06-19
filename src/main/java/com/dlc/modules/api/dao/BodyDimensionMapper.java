package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.BodyDimension;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface BodyDimensionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BodyDimension record);

    int insertSelective(BodyDimension record);

    BodyDimension selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BodyDimension record);

    int updateByPrimaryKey(BodyDimension record);

    List<Map<String,Object>> queryBodyDimensionList(Long userId);

    Map<String,Object> queryBodyDimensionScanId(Long userId);
}