package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.entity.PtCoachScheduleEntity;
import com.dlc.modules.sys.service.SysCoachScheduleService;
import com.dlc.modules.sys.service.SysPtCoachService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教练排班。路径 /sys/schedule。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/schedule")
public class SysCoachScheduleController extends AbstractController {

    @Autowired
    private SysCoachScheduleService sysCoachScheduleService;
    @Autowired
    private SysPtCoachService sysPtCoachService;

    /** 左侧教练列表（含是否有排班筛选由前端按 list 结果判断） */
    @RequestMapping("/coachList")
    @RequiresPermissions("sys:schedule:coachList")
    public R coachList(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<PtCoachEntity> list = sysPtCoachService.queryList(query);
        int total = sysPtCoachService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /** 某教练的排班列表 */
    @RequestMapping("/list")
    @RequiresPermissions("sys:schedule:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<PtCoachScheduleEntity> list = sysCoachScheduleService.queryList(query);
        int total = sysCoachScheduleService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:schedule:save")
    public R save(@RequestBody PtCoachScheduleEntity schedule) {
        schedule.setCreatedBy(getUserId());
        sysCoachScheduleService.save(schedule);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:schedule:update")
    public R update(@RequestBody PtCoachScheduleEntity schedule) {
        schedule.setUpdatedBy(getUserId());
        sysCoachScheduleService.update(schedule);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:schedule:delete")
    public R delete(@RequestBody Long[] ids) {
        sysCoachScheduleService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/changeEnabled")
    @RequiresPermissions("sys:schedule:changeEnabled")
    public R changeEnabled(@RequestBody PtCoachScheduleEntity schedule) {
        sysCoachScheduleService.changeEnabled(schedule.getId(), schedule.getIsEnabled());
        return R.ok();
    }
}
