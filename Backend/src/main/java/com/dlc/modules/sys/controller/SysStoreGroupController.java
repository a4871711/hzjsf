package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysStoreGroupEntity;
import com.dlc.modules.sys.service.SysStoreGroupService;
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
@RequestMapping("/sys/storeGroup")
public class SysStoreGroupController {
	@Autowired
	private SysStoreGroupService sysStoreGroupService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<SysStoreGroupEntity> storeGroupList = sysStoreGroupService.queryList(query);
		int total = sysStoreGroupService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(storeGroupList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		SysStoreGroupEntity storeGroup = sysStoreGroupService.queryObject(id);
		
		return R.ok().put("storeGroup", storeGroup);
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SysStoreGroupEntity storeGroup){
		storeGroup.setCreatedDate(new Date());
		sysStoreGroupService.update(storeGroup);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		sysStoreGroupService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
