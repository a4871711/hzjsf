package com.dlc.modules.sys.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.GoodsEntity;
import com.dlc.modules.sys.entity.GoodsOrderEntity;
import com.dlc.modules.sys.service.GoodsService;
import com.dlc.modules.sys.service.SysGoodsOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 商品控制类
 */
@RestController("SysGoodsOrderController")
@RequestMapping("/sys/goodsOrder")
public class SysGoodsOrderController {
	@Autowired
	private SysGoodsOrderService goodsOrderService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:goodsOrder:list")
	public R list(@RequestParam Map<String, Object> params){
		if(StringUtils.isBlank((String) params.get("status"))){
			params.put("status", 1);//默认查询待发货
		}
		//查询列表数据
		Query query = new Query(params);

		List<GoodsOrderEntity> goodsOrderList = goodsOrderService.queryList(query);
		int total = goodsOrderService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(goodsOrderList, total, query.getLimit(), query.getPage());

		return R.ok().put("page", pageUtil);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:goodsOrder:info")
	public R info(@PathVariable("id") Long goodsOrderId){
		//GoodsOrderEntity goodsOrder = goodsOrderService.queryObject(orderNo);
        List<Map<String, Object>> queryList = goodsOrderService.queryDetailList(goodsOrderId);
		return R.ok().put("orderDetail", queryList);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:goodsOrder:save")
	public R save(@RequestBody GoodsOrderEntity goodsOrder){
		goodsOrderService.save(goodsOrder);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:goodsOrder:update")
	public R update(@RequestBody GoodsOrderEntity goodsOrder){
		if(goodsOrder != null){
		    goodsOrder.setDeliveryTime(new Date());   //发货时间
        }
		goodsOrderService.update(goodsOrder);
        return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:goodsOrder:delete")
	public R delete(@RequestBody Long[] goodsOrderIds){
		goodsOrderService.deleteBatch(goodsOrderIds);

		return R.ok();
	}
	
}
