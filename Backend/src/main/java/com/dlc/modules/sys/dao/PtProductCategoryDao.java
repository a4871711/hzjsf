package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductCategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教商品分类 Dao。
 */
@Mapper
@Repository
public interface PtProductCategoryDao extends BaseDao<PtProductCategoryEntity> {

    int countByName(@Param("categoryName") String categoryName, @Param("excludeId") Long excludeId);

    int countByCategoryIdInProduct(Long categoryId);

    List<PtProductCategoryEntity> queryOptions();
}
