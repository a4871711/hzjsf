package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.Protocol;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProtocolMapper {
    int deleteByPrimaryKey(Long pId);

    int insert(Protocol record);

    int insertSelective(Protocol record);

    Protocol selectByPrimaryKey(Long pId);

    int updateByPrimaryKeySelective(Protocol record);

    int updateByPrimaryKeyWithBLOBs(Protocol record);

    int updateByPrimaryKey(Protocol record);

    Map<String,Object> queryProtocolByType(int type);

	List<Map<String, Object>> queryProtocol();
}