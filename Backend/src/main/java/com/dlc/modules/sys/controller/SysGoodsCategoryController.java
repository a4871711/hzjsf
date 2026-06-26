package com.dlc.modules.sys.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysGoodsCategoryEntity;
import com.dlc.modules.sys.service.SysGoodsCategoryService;
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
@RequestMapping("/sys/goodsCategory")
public class SysGoodsCategoryController {
	@Autowired
	private SysGoodsCategoryService sysGoodsCategoryService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:goodsCategory:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<SysGoodsCategoryEntity> goodsCategoryList = sysGoodsCategoryService.queryList(query);
		int total = sysGoodsCategoryService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(goodsCategoryList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:goodsCategory:info")
	public R info(@PathVariable("id") Long id){
		SysGoodsCategoryEntity goodsCategory = sysGoodsCategoryService.queryObject(id);
		
		return R.ok().put("goodsCategory", goodsCategory);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:goodsCategory:save")
	public R save(@RequestBody SysGoodsCategoryEntity goodsCategory){
		goodsCategory.setCreatedDate(new Date());
		sysGoodsCategoryService.save(goodsCategory);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:goodsCategory:update")
	public R update(@RequestBody SysGoodsCategoryEntity goodsCategory){
		String temp = goodsCategory.getCategoryImg();
		if(temp == ""){
			return R.error("请选择一级分类主图");
		}
		if(temp.indexOf(ConfigConstant.PRO_VER_URL)==-1){
			String img = temp;
			goodsCategory.setCategoryImg(img);
		}
		sysGoodsCategoryService.update(goodsCategory);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:goodsCategory:delete")
	public R delete(@RequestBody Long[] ids){
		sysGoodsCategoryService.deleteBatch(ids);
		
		return R.ok();
	}

	/**
	 * 绑定一级分类在二级分类下拉框下面
	 * @return
	 */
	@RequestMapping("/selectGoodsCategory")
	public R selectGoodsCategory(){
		List<SysGoodsCategoryEntity> list = sysGoodsCategoryService.queryGoodsCategoryList();
		return R.ok().put("goodsCategory", list);
	}
	
}
