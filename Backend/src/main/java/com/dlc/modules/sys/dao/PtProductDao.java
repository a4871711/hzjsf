package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 私教商品主表 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductDao extends BaseDao<PtProductEntity> {

    /** 按商品列表同口径统计总数、上下架和售罄数量。 */
    Map<String, Object> queryStats(Map<String, Object> params);

    /** 统计某编号前缀已有的商品数，用于撞号兜底重试 */
    int countByNoPrefix(@Param("prefix") String prefix);

    /** 分类改名时同步商品名称快照，兼容旧接口和移动端读取 category_name。 */
    int updateCategoryNameByCategoryId(@Param("categoryId") Long categoryId,
                                       @Param("categoryName") String categoryName);
}
