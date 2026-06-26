package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.ArrangeClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ArrangeClassMapper {
    int deleteByPrimaryKey(Long arrangeClassId);

    int deleteBySpsId(Long spsId);

    int insert(ArrangeClass record);

    int insertSelective(ArrangeClass record);

    ArrangeClass selectByPrimaryKey(Long arrangeClassId);

    int updateByPrimaryKeySelective(ArrangeClass record);

    int updateByPrimaryKey(ArrangeClass record);

    /**
     * 查询我的约课
     * @param params
     * @return
     */
    List<Map<String, Object>> queryAppointClassByUserId(Map<String, Object> params);

    int queryAppointClassCount(Map<String, Object> params);

    int updateClassStatus(@Param("arrangeClassId") Long arrangeClassId, @Param("status") Integer status);

    List<Map<String,Object>> queryArrClassByStatus();
}