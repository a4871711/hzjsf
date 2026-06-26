package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysActivityTrainEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 动作训练
 */
@Mapper
@Repository
public interface SysActivityTrainDao extends BaseDao<SysActivityTrainEntity> {
}
