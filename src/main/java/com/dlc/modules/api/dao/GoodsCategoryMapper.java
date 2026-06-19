package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GoodsCategory;
import com.dlc.modules.api.entity.GoodsCategoryDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsCategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsCategory record);

    int insertSelective(GoodsCategoryDetail goodsCategoryDetail);

    GoodsCategory selectByPrimaryKey(Long goodsCategoryId);

    int updateByPrimaryKeySelective(GoodsCategory record);

    int updateByPrimaryKey(GoodsCategory record);

    List<GoodsCategory> goodsCateList();

    List<Map<String,Object>> goodsCateDetail(Map<String, Object> query);

    int goodsCateDetailCount(Query query);

    List<Map<String,Object>> queryCateDetailName(Long goodsCategoryId);
}