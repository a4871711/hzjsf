package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.BodyShape;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface BodyShapeMapper {

    int insertSelective(BodyShape record);

    int updateByPrimaryKeySelective(BodyShape record);

    BodyShape queryBodyShapeByUserId(Map<String, Object> param);

    List<Map<String,Object>> queryBodyShapeLastRecord(Long userId);

}