package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysInformAtionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysInformAtionDao extends BaseDao<SysInformAtionEntity>{
}
