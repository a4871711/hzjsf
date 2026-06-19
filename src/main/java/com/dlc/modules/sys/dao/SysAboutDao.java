package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysAboutEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysAboutDao extends  BaseDao<SysAboutEntity>{
}
