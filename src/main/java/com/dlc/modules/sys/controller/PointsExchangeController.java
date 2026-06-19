package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;

import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dlc.modules.sys.entity.PointsExchangeEntity;
import com.dlc.modules.sys.service.PointsExchangeService;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;




/**
 * 积分兑换明细表
 * 
 * @author LINGKANGMING
 * @email 1647595314@qq.com
 * @date 2018-12-30 14:19:37
 */
@RestController
@RequestMapping("/sys/pointsexchange")
public class PointsExchangeController {
	@Autowired
	private PointsExchangeService pointsExchangeService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:pointsexchange:list")
	public R list(@RequestParam Map<String, Object> params){
		params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		//查询列表数据
        Query query = new Query(params);

		List<PointsExchangeEntity> pointsExchangeList = pointsExchangeService.queryList(query);
		int total = pointsExchangeService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(pointsExchangeList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{pointsExchangeId}")
	@RequiresPermissions("sys:pointsexchange:info")
	public R info(@PathVariable("pointsExchangeId") Long pointsExchangeId){
		PointsExchangeEntity pointsExchange = pointsExchangeService.queryObject(pointsExchangeId);
		
		return R.ok().put("pointsExchange", pointsExchange);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:pointsexchange:save")
	public R save(@RequestBody PointsExchangeEntity pointsExchange){
		pointsExchangeService.save(pointsExchange);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:pointsexchange:update")
	public R update(@RequestBody PointsExchangeEntity pointsExchange){
		pointsExchangeService.update(pointsExchange);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:pointsexchange:delete")
	public R delete(@RequestBody Long[] pointsExchangeIds){
		pointsExchangeService.deleteBatch(pointsExchangeIds);
		
		return R.ok();
	}
	
}
