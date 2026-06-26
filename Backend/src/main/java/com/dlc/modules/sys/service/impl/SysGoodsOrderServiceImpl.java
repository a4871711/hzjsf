package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.GoodsOrderDao;
import com.dlc.modules.sys.entity.GoodsOrderEntity;
import com.dlc.modules.sys.service.SysGoodsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysGoodsOrderService")
public class SysGoodsOrderServiceImpl implements SysGoodsOrderService {
	@Autowired
	private GoodsOrderDao goodsOrderDao;

	@Override
	public GoodsOrderEntity queryObject(Long goodsOrderId){
		return goodsOrderDao.queryObject(goodsOrderId);
	}

	@Override
	public List<GoodsOrderEntity> queryList(Map<String, Object> map){
		return goodsOrderDao.queryLists(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map){
		return goodsOrderDao.queryListsCount(map);
	}

	@Override
	public void save(GoodsOrderEntity goodsOrder){
		goodsOrderDao.save(goodsOrder);
	}

	@Override
	public void update(GoodsOrderEntity goodsOrder){
		goodsOrderDao.update(goodsOrder);
	}

	@Override
	public void delete(Long goodsOrderId){
		goodsOrderDao.delete(goodsOrderId);
	}

	@Override
	public void deleteBatch(Long[] goodsOrderIds){
		goodsOrderDao.deleteBatch(goodsOrderIds);
	}

	@Override
	public List<Map<String, Object>> queryDetailList(Long goodsOrderId) {

		return goodsOrderDao.queryDetails(goodsOrderId);
	}
}
