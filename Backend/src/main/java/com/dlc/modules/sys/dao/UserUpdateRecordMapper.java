package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.UserUpdateRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserUpdateRecordMapper {
    int deleteByPrimaryKey(Long uprId);

    int insert(UserUpdateRecord record);

    int insertSelective(UserUpdateRecord record);

    UserUpdateRecord selectByPrimaryKey(Long uprId);

    int updateByPrimaryKeySelective(UserUpdateRecord record);

    int updateByPrimaryKey(UserUpdateRecord record);

    List<Map<String,Object>> selectValidityRecordList(Query query);

    int selectValidityRecordTotal(Query query);
}