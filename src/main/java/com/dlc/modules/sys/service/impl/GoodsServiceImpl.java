package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.dao.GoodsDao;
import com.dlc.modules.sys.entity.GoodsEntity;
import com.dlc.modules.sys.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {
	@Autowired
	private GoodsDao goodsDao;

	@Override
	public GoodsEntity queryObject(Long id) {
		return goodsDao.queryObject(id);
	}

	@Override
	public List<GoodsEntity> queryList(Map<String, Object> map) {
		return goodsDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return goodsDao.queryTotal(map);
	}

	@Override
	public int save(GoodsEntity goods) {
		return goodsDao.save(goods);
	}

	@Override
	public void update(GoodsEntity goods) {
		goodsDao.update(goods);
	}

	@Override
	public void delete(Long id) {
		goodsDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		goodsDao.deleteBatch(ids);
	}

	@Override
	public void updateGoodsId(Long id) {
		goodsDao.updateGoodsId(id);
	}

	@Override
	public void insertGoods(GoodsEntity goods) {
		goodsDao.insertGoods(goods);
	}

	@Override
	public List<Map<String, Object>> selectGoodsDetailList(Map<String, Object> map) {
		return goodsDao.selectGoodsDetailList(map);
	}

	@Override
	public int selectGoodsDetailCount(Map<String, Object> map) {
		return goodsDao.selectGoodsDetailCount(map);
	}

	@Override
	public int updateDetail(GoodsEntity goods) {
		return goodsDao.updateDetail(goods);
	}

	@Override
	public void deleteGoodsPropertyBatch(Long[] ids) {
		goodsDao.deleteGoodsPropertyBatch(ids);
	}

	@Override
	public Long[] queryGoodsId(Long[] ids) {
		return goodsDao.queryGoodsId(ids);
	}

	@Override
	public List<Map<String, Object>> queryGoodsSelectList(Query query) {
		return goodsDao.queryGoodsSelectList(query);
	}

	@Override
	public int queryGoodsSelectTotal(Query query) {
		return goodsDao.queryGoodsSelectTotal(query);
	}

	@Override
	public int updateDeleteByGid(Long goodsId) {
		return goodsDao.updateDeleteByGid(goodsId);
	}

    @Override
    public int updateDeleteById(Long id) {
        return goodsDao.updateDeleteById(id);
    }
}
