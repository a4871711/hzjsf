package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysGoodsCategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysGoodsCategoryDao extends BaseDao<SysGoodsCategoryEntity> {
	List<SysGoodsCategoryEntity> queryGoodsCategoryList();
}
