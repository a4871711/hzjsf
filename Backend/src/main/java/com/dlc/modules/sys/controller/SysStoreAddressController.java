package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysStoreAddressEntity;
import com.dlc.modules.sys.entity.SysStoreGroupEntity;
import com.dlc.modules.sys.service.SysStoreAddressService;
import com.dlc.modules.sys.service.SysStoreGroupService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingkangming
 */
@RestController
@RequestMapping("/sys/storeAddress")
public class SysStoreAddressController {
	@Autowired
	private SysStoreAddressService sysStoreAddressService;
	@Autowired
	private SysStoreGroupService sysStoreGroupService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		query.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
		List<SysStoreAddressEntity> storeAddressList = sysStoreAddressService.queryList(query);
		int total = sysStoreAddressService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(storeAddressList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		SysStoreAddressEntity storeAddress = sysStoreAddressService.queryObject(id);
		
		return R.ok().put("storeAddress", storeAddress);
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SysStoreAddressEntity storeAddress){
		storeAddress.setStatus(1);
		storeAddress.setCreatedDate(new Date());
		int sum = sysStoreAddressService.update(storeAddress);
		if(sum>0){
			SysStoreGroupEntity storeGroupEntity = sysStoreGroupService.queryStoreAddressById(storeAddress.getStoreId());
			storeGroupEntity.setGroupAddr(storeAddress.getProvince()+storeAddress.getCity()+storeAddress.getZone()+storeAddress.getStoreAddrDetail());
			sysStoreGroupService.update(storeGroupEntity);
		}
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		sysStoreAddressService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
