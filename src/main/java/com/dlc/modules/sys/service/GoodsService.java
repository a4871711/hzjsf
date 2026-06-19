package com.dlc.modules.sys.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.GoodsEntity;

import java.util.List;
import java.util.Map;

public interface GoodsService {
	
	GoodsEntity queryObject(Long id);
	
	List<GoodsEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	int save(GoodsEntity goods);
	
	void update(GoodsEntity goods);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	void updateGoodsId(Long id);

	void insertGoods(GoodsEntity goods);

	List<Map<String,Object>> selectGoodsDetailList(Map<String,Object> map);

	int selectGoodsDetailCount(Map<String,Object> map);

    int updateDetail(GoodsEntity goods);

	void deleteGoodsPropertyBatch(Long[] ids);

	Long[] queryGoodsId(Long[] ids);

    List<Map<String,Object>> queryGoodsSelectList(Query query);

	int queryGoodsSelectTotal(Query query);

    int updateDeleteByGid(Long goodsId);

	int updateDeleteById(Long id);
}
