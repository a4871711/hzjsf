package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.FaceIdentyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FaceIdentyRecordMapper {
    int deleteByPrimaryKey(Long firId);

    int insert(FaceIdentyRecord record);

    int insertSelective(FaceIdentyRecord record);

    FaceIdentyRecord selectByPrimaryKey(Long firId);

    int updateByPrimaryKeySelective(FaceIdentyRecord record);

    int updateByPrimaryKey(FaceIdentyRecord record);
    /*查询当天刷脸次数*/
    int selectRecordCount(Long userId);
    /*删除记录*/
    int deleteRecode(Long userId);
    /*定时任务删除1天之前的数据*/
    int deleteRecodeHistory();

    List<Map<String,Object>> selectfaceRecordList(Query query);

    int selectfaceRecordTotal(Query query);
}