package com.dlc.modules.sys.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;
import com.dlc.modules.sys.service.SysTeamClassService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *	lingkangming
 *  团体课
 */
@RestController
@RequestMapping("/sys/teamClass")
public class SysTeamClassController {
	@Autowired
	private SysTeamClassService sysTeamClassService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:teamClass:list")
	public R list(@RequestParam Map<String, Object> params){
        //Long storeId = ShiroUtils.getUserEntity().getStoreId();
        //if(null != storeId){
            params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        //}
		//查询列表数据
        Query query = new Query(params);
		List<TeamClassEntity> teamClassList = sysTeamClassService.queryList(query);
		int total = sysTeamClassService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(teamClassList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:teamClass:info")
	public R info(@PathVariable("id") Long id){
		TeamClassEntity teamClass = sysTeamClassService.queryObject(id);
		return R.ok().put("teamClass", teamClass);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:teamClass:save")
	public R save(@RequestBody TeamClassEntity teamClass){
		String img = teamClass.getFirstImgUrl();
		if(img != ""){
			String s = img;
			teamClass.setFirstImgUrl(s);
		}else{
			return R.error("请选择一张封面图");
		}
		teamClass.setCreatedDate(new Date());
		sysTeamClassService.save(teamClass);
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:teamClass:update")
	public R update(@RequestBody TeamClassEntity teamClass){
		String img = teamClass.getFirstImgUrl();
		if(img.indexOf(ConfigConstant.PRO_VER_URL)==-1){
			String s = img;
			teamClass.setFirstImgUrl(s);
		}
		Integer type = teamClass.getTeamClassType();
		if(type == 0){
			BigDecimal money = new BigDecimal(0);
			teamClass.setClassPrice(money);
		}
		sysTeamClassService.update(teamClass);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:teamClass:delete")
	public R delete(@RequestBody Long[] ids){
		sysTeamClassService.deleteBatch(ids);
		return R.ok();
	}

	/**
	 * 绑定门店数据在团体课里面
	 * @return
	 */
	@RequestMapping("/selectStoreName")
	public R selectStoreName(){
        String storeIds = ShiroUtils.getUserEntity().getStoreIds();
		List<SysStoreEntity> list = sysTeamClassService.selectStoreName(storeIds);
		return R.ok().put("storeNameDetail", list);
	}
}
