package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.Information;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface InformationMapper {
    int deleteByPrimaryKey(Long informationId);

    int insert(Information record);

    int insertSelective(Information record);

    Information selectByPrimaryKey(Long informationId);

    int updateByPrimaryKeySelective(Information record);

    int updateByPrimaryKeyWithBLOBs(Information record);

    int updateByPrimaryKey(Information record);

    int queryTotal(Map<String, Object> params);

    List<Map<String,Object>> queryInformationList(Map<String, Object> params);

    Map<String,Object> queryInformationInfo(Long informationId);

    List<Map<String,Object>> selectSowingMap(Integer infType);

    List<Map<String,Object>> querySliveList();
}