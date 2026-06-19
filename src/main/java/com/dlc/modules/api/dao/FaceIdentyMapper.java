package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.FaceIdenty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FaceIdentyMapper {
    int deleteByPrimaryKey(Long faceId);

    int insert(FaceIdenty record);

    int insertSelective(FaceIdenty record);

    FaceIdenty selectByPrimaryKey(Long faceId);

    int updateByPrimaryKeySelective(FaceIdenty record);

    int updateByPrimaryKey(FaceIdenty record);
}