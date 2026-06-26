package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysPrivateClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysPrivateClassDao extends BaseDao<SysPrivateClassEntity> {
    SysPrivateClassEntity queryLast(SysPrivateClassEntity entity);

    int deleteBatchCcShip(Long[] id);

    int deleteBatchGpShip(Long[] id);
}
