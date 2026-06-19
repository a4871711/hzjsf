package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.DataMap;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DataMapMapper {
    int deleteByPrimaryKey(Long dataMapId);

    int insert(DataMap record);

    int insertSelective(DataMap record);

    DataMap selectByPrimaryKey(Long dataMapId);

    int updateByPrimaryKeySelective(DataMap record);

    int updateByPrimaryKey(DataMap record);

    Map<String,Object> findBraceletPrice();

    List<String> queryHobbies();

    List<Map<String, Object>> queryReportList();
    List<String> queryReportNameList(List<Long> list);
    String queryImgConstant();
}