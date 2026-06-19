package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.StudentTeamclassShip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StudentTeamclassShipMapper {
    int deleteByPrimaryKey(Long stuTeamClassId);

    int insert(StudentTeamclassShip record);

    int insertSelective(StudentTeamclassShip record);

    StudentTeamclassShip selectByPrimaryKey(Long stuTeamClassId);

    int updateByPrimaryKeySelective(StudentTeamclassShip record);

    int updateByPrimaryKey(StudentTeamclassShip record);

    List<Map<String, Object>> queryTeamClassByUserId(Map<String, Object> parms);

    int queryTeamClassCount(Map<String, Object> parms);

    List<StudentTeamclassShip> selectByStoreId(Long storeId);

    int selectCountById(@Param("teamClassId") Long teamClassId, @Param("userId") Long userId);
}