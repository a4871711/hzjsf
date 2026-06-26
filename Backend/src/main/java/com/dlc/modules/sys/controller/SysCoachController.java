package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysCoachEntity;
import com.dlc.modules.sys.service.SysCoachService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 教练控制类
 */
@RestController
@RequestMapping("/sys/coach")
public class SysCoachController {
	@Autowired
	private SysCoachService sysCoachService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:coach:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<SysCoachEntity> coachList = sysCoachService.queryList(query);
		int total = sysCoachService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(coachList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil).put("coachGrade", sysCoachService.getCoachGradeList());
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:coach:info")
	public R info(@PathVariable("id") Long id){
		SysCoachEntity coach = sysCoachService.queryObject(id);
		return R.ok().put("coach", coach);
	}
	
	/**
	 * 审核成功
	 */
	@RequestMapping("/updateSuccess")
	@RequiresPermissions("sys:coach:updateSuccess")
	public R updateSuccess(@RequestBody SysCoachEntity coach){
		if(coach.getApproveStatus() != 0){
			return R.error("请选择待审核的数据操作");
		}
		coach.setApproveStatus(1);
		coach.setApproveTime(new Date());
		sysCoachService.updateSuccess(coach);

		return R.ok();
	}

	/**
	 * 审核失败
	 */
	@RequestMapping("/updateFailure")
	@RequiresPermissions("sys:coach:updateFailure")
	public R updateFailure(@RequestBody SysCoachEntity coach){
		if(coach.getApproveStatus() != 0){
			return R.error("请选择待审核的数据操作");
		}
		coach.setApproveStatus(2);
		coach.setApproveTime(new Date());
		sysCoachService.updateFailure(coach);

		return R.ok();
	}
	/**
	 * 审核失败
	 */
	@RequestMapping("/coachUpdate")
	public R coachUpdate(@RequestBody SysCoachEntity coach){
		if(coach == null){
			return R.error("请选择正确的数据操作");
		}
		sysCoachService.coachUpdate(coach);

		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:coach:delete")
	public R delete(@RequestBody Long[] ids){
		sysCoachService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
