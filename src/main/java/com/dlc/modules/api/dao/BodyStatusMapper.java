package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.BodyStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface BodyStatusMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BodyStatus record);

    int insertSelective(BodyStatus record);

    BodyStatus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BodyStatus record);

    int updateByPrimaryKey(BodyStatus record);

    List<Map<String,Object>> queryBodyInfoBetweenTime(@Param("paramMap") Map<String, Object> paramMap);

    List<Map<String,Object>> queryBodyBodyStatusByUserId(Long userId);
}