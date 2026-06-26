package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.GoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsDao extends BaseDao<GoodsEntity> {
    void updateGoodsId(Long id);
    void insertGoods(GoodsEntity goods);

    List<Map<String,Object>> selectGoodsDetailList(Map<String,Object> map);

    int selectGoodsDetailCount(Map<String,Object> map);

    int updateDetail(GoodsEntity goods);

    void deleteGoodsPropertyBatch(Long[] ids);

    Long[] queryGoodsId(Long[] ids);
    /**广告详情用到*/
    List<Map<String,Object>> queryGoodsSelectList(Query query);

    int queryGoodsSelectTotal(Query query);
    /**根据goodsId删除*/
    int updateDeleteByGid(Long id);
    /**根据主键id删除*/
    int updateDeleteById(Long id);
}
