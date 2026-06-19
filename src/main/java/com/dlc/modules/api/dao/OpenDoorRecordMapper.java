package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.OpenDoorRecord;
import com.dlc.modules.sys.vo.OpenRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OpenDoorRecordMapper {

    List<OpenRecordVo> selectOpenDoorRecordList(Query query);

    int selectOpenDoorRecordTotal(Query query);

    int insert(OpenDoorRecord record);

    OpenDoorRecord selectByPrimaryKey(Long id);

    int getStorePeopleTotal(@Param("storeIds") String storeIds);

    Map getNearOpenLog(@Param("userId") Long userId, @Param("startTime") String date);

    Map getLastOpenLog(@Param("userId") Long userId, @Param("id") String id);

}
