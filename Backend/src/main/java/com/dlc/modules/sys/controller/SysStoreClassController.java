package com.dlc.modules.sys.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysStoreAddressEntity;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.SysStoreGroupEntity;
import com.dlc.modules.sys.entity.SysUserEntity;
import com.dlc.modules.sys.service.*;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lingkangming
 */
@RestController
@RequestMapping("/sys/storeClass")
public class SysStoreClassController {
	@Autowired
	private SysStoreService sysStoreService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:storeClass:list")
	public R list(@RequestParam Map<String, Object> params){
		String storeIds = ShiroUtils.getUserEntity().getStoreIds();
		if(StringUtils.isNotBlank(storeIds)){
            //Long storeAddrId = sysStoreService.queryStoreAddrId(storeId);
		    //params.put("classPlaceId", storeAddrId);
        }
		params.put("storeIds",storeIds);
        //查询列表数据
        if(StringUtils.isBlank((String) params.get("classStatus"))){
            //第一次默认查询时 状态为0进行中
            params.put("classStatus",0);
        }
		Query query = new Query(params);
		PageUtils pageUtil = sysStoreService.queryClassList(query);
		return R.ok().put("page", pageUtil);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:store:info")
	public R info(@PathVariable("id") Long id){
		SysStoreEntity store = sysStoreService.queryObject(id);
		
		return R.ok().put("store", store);
	}
	
	/**
	 * 修改（确认课程）
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:storeClass:update")
	public R update(@RequestBody Long arrangeClassId){
		if(arrangeClassId == null){
		    R.reError("参数不为空");
        }
		return sysStoreService.confirmClass(arrangeClassId);
	}

	
}
