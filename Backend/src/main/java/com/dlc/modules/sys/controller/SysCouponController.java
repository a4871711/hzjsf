package com.dlc.modules.sys.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysCouponEntity;
import com.dlc.modules.sys.service.SysCouponService;
import com.dlc.modules.sys.shiro.ShiroUtils;

@RestController
@RequestMapping("/sys/sysCoupon")
public class SysCouponController {
    @Autowired
    private SysCouponService couponService;
    
    /**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:sysCoupon:list")
	public R list(@RequestBody SysCouponEntity params){
		//查询列表数据
		params.setOffset((params.getPage() - 1) * params.getPage());
		params.setStoreAddrIds(ShiroUtils.getUserEntity().getStoreAddrIds());
		List<SysCouponEntity> list = couponService.selectSysCouponList(params);
		int total = couponService.selectSysCouponCount(params);
		PageUtils pageUtil = new PageUtils(list, total, params.getLimit(), params.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:sysCoupon:info")
	public R info(@PathVariable("id") Long id){
		SysCouponEntity data = couponService.selectSysCouponById(id);
		
		return R.ok().put("data", data);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("sys:sysCoupon:save")
	public R save(@RequestBody SysCouponEntity params){
		
		int sum = couponService.insertSysCoupon(params);
		if(sum <= 0){
			return R.error("操作失败");
		}
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("sys:sysCoupon:update")
	public R update(@RequestBody SysCouponEntity coupon){
		couponService.updateSysCoupon(coupon);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("sys:sysCoupon:delete")
	public R delete(@RequestBody Long[] ids){
		couponService.deleteSysCouponByIds(ids);

		return R.ok();
	}
}
