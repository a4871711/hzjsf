package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysDataMapEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysDataMapDao {
    //查询所有
    List<SysDataMapEntity> queryList(Map<String, Object> map);
    //新增
    int save(SysDataMapEntity dataMapEntity);
    //修改
    int update(SysDataMapEntity dataMapEntity);

    //总数
    int queryTotal(Map<String, Object> map);
    SysDataMapEntity queryObject(Long dataMapId);
    void delete(Long datamapId);

    void deleteBatch(Long[] datamapIds);
    List<SysDataMapEntity> getType();

    Integer getGradeRule(Double money);

    Integer queryMaxGrade();
}
