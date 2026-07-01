package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教商品类型 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductTypeDao extends BaseDao<PtProductTypeEntity> {

    /** 名称在未删数据内是否重复（excludeId 为空表示新增） */
    int countByName(@Param("typeName") String typeName, @Param("excludeId") Long excludeId);

    /** 该类型被多少未删商品引用（删除护栏） */
    int countByTypeIdInProduct(Long typeId);

    /** 启用状态类型下拉项（供商品表单选择） */
    List<PtProductTypeEntity> queryOptions();
}
