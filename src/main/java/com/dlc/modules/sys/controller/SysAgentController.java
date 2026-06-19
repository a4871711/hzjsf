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
import com.dlc.modules.sys.entity.SysAgentEntity;
import com.dlc.modules.sys.service.SysAgentService;

@RestController
@RequestMapping("/sys/agent")
public class SysAgentController {
    @Autowired
    private SysAgentService agentService;
    
    /**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:store:list")
	public R list(@RequestBody SysAgentEntity params){
		//查询列表数据
		params.setOffset((params.getPage() - 1) * params.getPage());
		List<SysAgentEntity> list = agentService.selectAgentList(params);
		int total = agentService.selectAgentCount(params);
		PageUtils pageUtil = new PageUtils(list, total, params.getLimit(), params.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:store:info")
	public R info(@PathVariable("id") Long id){
		SysAgentEntity data = agentService.selectAgentById(id);
		
		return R.ok().put("data", data);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("sys:agent:save")
	public R save(@RequestBody SysAgentEntity params){
		
		int sum = agentService.insertAgent(params);
		if(sum <= 0){
			return R.error("操作失败");
		}
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("sys:agent:update")
	public R update(@RequestBody SysAgentEntity agent){
		agentService.updateAgent(agent);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("sys:agent:delete")
	public R delete(@RequestBody Long[] ids){
		agentService.deleteAgentByIds(ids);

		return R.ok();
	}
}
