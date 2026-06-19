package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.TeamClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TeamClassMapper {
    int deleteByPrimaryKey(Long teamClassId);

    int insert(TeamClass record);

    int insertSelective(TeamClass record);

    TeamClass selectByPrimaryKey(Long teamClassId);

    Map<String, Object> selectByPrimaryKeyMap(Long teamClassId);

    int updateByPrimaryKeySelective(TeamClass record);

    int updateByPrimaryKeyWithBLOBs(TeamClass record);

    int updateByPrimaryKey(TeamClass record);

    List<Map<String,Object>> selectStoreTeamClassList(Map<String,Object> params);

    int queryTotal(Map<String, Object> params);
}