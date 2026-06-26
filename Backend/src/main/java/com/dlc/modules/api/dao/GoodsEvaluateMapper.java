package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GoodsEvaluate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsEvaluateMapper {
    int deleteByPrimaryKey(Long goodsEvaluatId);

    int insert(GoodsEvaluate record);

    int insertSelective(GoodsEvaluate record);

    GoodsEvaluate selectByPrimaryKey(Long goodsEvaluatId);

    int updateByPrimaryKeySelective(GoodsEvaluate record);

    int updateByPrimaryKey(GoodsEvaluate record);

    List<Map<String,Object>> goodsEvaluateList(Map<String, Object> query);

    int goodsEvaluateListCount(Query query);
    //批量插入评论
    int insertEvelutBatch(List<GoodsEvaluate> goodsEvaluate);
}