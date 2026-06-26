package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PrivateClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PrivateClassMapper {
    int deleteByPrimaryKey(Long privateClassId);

    int insert(PrivateClass record);

    int insertSelective(PrivateClass record);

    PrivateClass selectByPrimaryKey(Long privateClassId);

    int updateByPrimaryKeySelective(PrivateClass record);

    int updateByPrimaryKey(PrivateClass record);

    List<Map<String,Object>> courseList(Long coachId);

    Map<String,Object> courseInfo(Map<String, Object> params);

    int queryIsOrTyclass(Long privateClassId);
}