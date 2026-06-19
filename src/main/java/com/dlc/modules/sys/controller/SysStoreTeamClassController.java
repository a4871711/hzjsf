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
@RequestMapping("/sys/storeTeamClass")
public class SysStoreTeamClassController {
	@Autowired
	private SysStoreService sysStoreService;

	/**
	 * 团体课列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:storeTeamClass:list")
	public R teamList(@RequestParam Map<String, Object> params){
		//Long storeId = ShiroUtils.getUserEntity().getStoreId();
		//if(null != storeId){
			params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		//}
		//查询列表数据
		if(StringUtils.isBlank((String) params.get("classStatus"))){
			//第一次默认查询时 状态为0待上课
			params.put("classStatus",0);
		}
        if(StringUtils.isBlank((String) params.get("teamClassId"))){
            params.put("teamClassId",null);
        }
        if(StringUtils.isBlank((String) params.get("teamClassName"))){
            params.put("teamClassName",null);
        }
		Query query = new Query(params);
		PageUtils pageUtil = sysStoreService.queryTeamClassList(query);
		return R.ok().put("page", pageUtil);
	}
    /**
     * 查询学员列表（确认课程进行）
     */
    @RequestMapping("/info")
    @RequiresPermissions("sys:storeTeamClass:info")
    public R info(@RequestParam Map<String, Object> params){
        //params.put("classId", id);
		PageUtils pageUtil = sysStoreService.queryStuInfo(params);
        return R.ok().put("stuDetailPage", pageUtil);
    }

	/**
	 * 修改（确认课程完成）
	 */
	@RequestMapping("/updateEnd")
	@RequiresPermissions("sys:storeTeamClass:updateEnd")
	public R updateEnd(@RequestBody Long[] stuTeamClassId){
		if(stuTeamClassId == null || stuTeamClassId.length == 0){
			return R.error("参数不为空");
		}
		return sysStoreService.updateClass(stuTeamClassId);
	}

	/**
	 * 修改（确认课程完成）
	 */
	@RequestMapping("/signInStu")
	@RequiresPermissions("sys:storeTeamClass:updateEnd")
	public R signInStu(@RequestBody Long[] stuTeamClassIds){
		if(stuTeamClassIds == null || stuTeamClassIds.length == 0){
			return R.error("参数不为空");
		}
		return sysStoreService.signInTeamClass(stuTeamClassIds);
	}
	
}
