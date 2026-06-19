package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.ColorEntity;
import com.dlc.modules.sys.service.ColorService;
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
@RequestMapping("/sys/color")
public class ColorController {
	@Autowired
	private ColorService colorService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:color:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<ColorEntity> sizeList = colorService.queryList(query);
		int total = colorService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(sizeList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:color:info")
	public R info(@PathVariable("id") Long id){
		ColorEntity Color = colorService.queryObject(id);
		return R.ok().put("Color", Color);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:color:save")
	public R save(@RequestBody ColorEntity color){
		Map<String,Object> map = new HashMap<>();
		map.put("color",color.getColor());
		if (!colorService.getExistsColor(map).isEmpty()){
			return R.error("颜色为"+color.getColor()+"已经存在");
		}
		colorService.save(color);
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:color:update")
	public R update(@RequestBody ColorEntity color){
		if(!color.getColor().equals(colorService.queryObject(color.getColorId()).getColor())){
			Map<String,Object> map = new HashMap<>();
			map.put("color",color.getColor());
			if (!colorService.getExistsColor(map).isEmpty()){
				return R.error("颜色"+color.getColor()+"已经存在");
			}
		}
		colorService.update(color);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:color:delete")
	public R delete(@RequestBody Long[] ids){
		colorService.deleteBatch(ids);
		return R.ok();
	}

	/**
	 * 绑定颜色
	 * @return
	 */
	@RequestMapping("/selectColorDetail")
	public R selectColorDetail(){
		List<ColorEntity> list = colorService.selectColorDetail();
		return R.ok().put("colorDetail", list);
	}
}
