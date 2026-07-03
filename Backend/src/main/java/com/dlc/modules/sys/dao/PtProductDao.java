package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 私教商品主表 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductDao extends BaseDao<PtProductEntity> {

    /** 统计某编号前缀已有的商品数，用于撞号兜底重试 */
    int countByNoPrefix(@Param("prefix") String prefix);
}
