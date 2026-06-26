package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SizeEntity;
import com.dlc.modules.sys.service.SizeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lingkangming
 */
@RestController
@RequestMapping("/sys/size")
public class SizeController {
	@Autowired
	private SizeService sizeService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:size:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<SizeEntity> sizeList = sizeService.queryList(query);
		int total = sizeService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(sizeList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:size:info")
	public R info(@PathVariable("id") Long id){
		SizeEntity Size = sizeService.queryObject(id);
		return R.ok().put("Size", Size);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:size:save")
	public R save(@RequestBody SizeEntity size){
		Map<String,Object> map = new HashMap<>();
		map.put("size",size.getSize());
		if (!sizeService.getExistsSize(map).isEmpty()){
			return R.error("尺寸为"+size.getSize()+"已经存在");
		}
		sizeService.save(size);
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:color:update")
	public R update(@RequestBody SizeEntity size){
		if(!size.getSize().equals(sizeService.queryObject(size.getSizeId()).getSize())){
			Map<String,Object> map = new HashMap<>();
			map.put("size",size.getSize());
			if (!sizeService.getExistsSize(map).isEmpty()){
				return R.error("颜色"+size.getSize()+"已经存在");
			}
		}
		sizeService.update(size);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:size:delete")
	public R delete(@RequestBody Long[] ids){
		sizeService.deleteBatch(ids);
		return R.ok();
	}

	/**
	 * 绑定尺寸
	 * @return
	 */
	@RequestMapping("/selectSizeDetail")
	public R selectSizeDetail(){
		List<SizeEntity> list = sizeService.selectSizeDetail();
		return R.ok().put("sizeDetail", list);
	}
}
