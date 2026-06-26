package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.service.SysStoreService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lingkangming
 */
@RestController
@RequestMapping("/sys/storeCoach")
public class SysStoreCoachController {
	@Autowired
	private SysStoreService sysStoreService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:storeCoach:list")
	public R list(@RequestParam Map<String, Object> params){
		params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		//查询列表数据
		Query query = new Query(params);
		PageUtils pageUtil = sysStoreService.queryCoachList(query);
		return R.ok().put("page", pageUtil);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:storeCoach:info")
	public R info(@PathVariable("id") Long id){
		SysStoreEntity store = sysStoreService.queryObject(id);
		
		return R.ok().put("store", store);
	}
	
	/**
	 * 修改（确认课程）
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@RequiresPermissions("sys:storeCoach:update")
	public R update(@RequestParam Map<String, Object> params){
		if(StringUtils.isBlank((String) params.get("cpsId")) || StringUtils.isBlank((String) params.get("grade"))){
		    R.reError("参数不为空");
        }
        sysStoreService.updateGrade(params);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:storeCoach:delete")
	public R delete(@RequestBody Long ids){
		sysStoreService.delete(ids);

		return R.ok();
	}

	
}
