package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.Protocol;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysProtocolMapper {
    int deleteByPrimaryKey(Long pId);

    int insert(Protocol record);

    int insertSelective(Protocol record);

    Protocol selectByPrimaryKey(Long pId);

    int updateByPrimaryKeySelective(Protocol record);

    int updateByPrimaryKeyWithBLOBs(Protocol record);

    int updateByPrimaryKey(Protocol record);

    List<Protocol> queryList(Query query);

    int queryTotal(Query query);

    int selectIfExist(Integer type);

    int deleteBatch(Long[] pIds);
}