package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysGoodsCategoryDetailEntity;
import com.dlc.modules.sys.entity.SysGoodsCategoryEntity;
import com.dlc.modules.sys.service.SysGoodsCategoryDetailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lingkangming
 */
@RestController
@RequestMapping("/sys/goodsCategoryDetail")
public class SysGoodsCategoryDetailController {
	@Autowired
	private SysGoodsCategoryDetailService sysGoodsCategoryDetailService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:goodsCategoryDetail:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<SysGoodsCategoryDetailEntity> goodsCategoryDetailList = sysGoodsCategoryDetailService.queryList(query);
		int total = sysGoodsCategoryDetailService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(goodsCategoryDetailList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:goodsCategoryDetail:info")
	public R info(@PathVariable("id") Long id){
		SysGoodsCategoryDetailEntity goodsCategoryDetail = sysGoodsCategoryDetailService.queryObject(id);
		
		return R.ok().put("goodsCategoryDetail", goodsCategoryDetail);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:goodsCategoryDetail:save")
	public R save(@RequestBody SysGoodsCategoryDetailEntity goodsCategoryDetail){
		goodsCategoryDetail.setCreatedDate(new Date());
		sysGoodsCategoryDetailService.save(goodsCategoryDetail);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:goodsCategoryDetail:update")
	public R update(@RequestBody SysGoodsCategoryDetailEntity goodsCategoryDetail){
		sysGoodsCategoryDetailService.update(goodsCategoryDetail);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:goodsCategoryDetail:delete")
	public R delete(@RequestBody Long[] ids){
		sysGoodsCategoryDetailService.deleteBatch(ids);
		
		return R.ok();
	}

	/**
	 * 绑定二级分类在商品下拉框
	 * @return
	 */
	@RequestMapping("/selectGoodsCategoryDetail")
	public R selectGoodsCategoryDetail(){
		List<SysGoodsCategoryDetailEntity> list = sysGoodsCategoryDetailService.queryGoodsCategoryDetailList();
		return R.ok().put("goodsCategoryDetail", list);
	}
	
}
