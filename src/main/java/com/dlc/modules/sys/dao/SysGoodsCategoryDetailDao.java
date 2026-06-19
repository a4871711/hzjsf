package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysGoodsCategoryDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysGoodsCategoryDetailDao extends BaseDao<SysGoodsCategoryDetailEntity> {
    List<SysGoodsCategoryDetailEntity> queryGoodsCategoryDetailList();
}
