package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachLevelEntity;
import com.dlc.modules.sys.service.SysCoachLevelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教教练等级管理
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/coachLevel")
public class SysCoachLevelController extends AbstractController {

    @Autowired
    private SysCoachLevelService sysCoachLevelService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:coachLevel:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<PtCoachLevelEntity> list = sysCoachLevelService.queryList(query);
        int total = sysCoachLevelService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:coachLevel:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("coachLevel", sysCoachLevelService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:coachLevel:save")
    public R save(@RequestBody PtCoachLevelEntity coachLevel) {
        coachLevel.setCreatedBy(getUserId());
        coachLevel.setUpdatedBy(getUserId());
        coachLevel.setCreatedAt(new Date());
        sysCoachLevelService.save(coachLevel);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:coachLevel:update")
    public R update(@RequestBody PtCoachLevelEntity coachLevel) {
        coachLevel.setUpdatedBy(getUserId());
        sysCoachLevelService.update(coachLevel);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:coachLevel:delete")
    public R delete(@RequestBody Long[] ids) {
        sysCoachLevelService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:coachLevel:changeStatus")
    public R changeStatus(@RequestBody PtCoachLevelEntity coachLevel) {
        sysCoachLevelService.changeStatus(coachLevel.getId(), coachLevel.getStatus());
        return R.ok();
    }

    /** 启用等级下拉项（供教练表单使用，无需单独按钮权限） */
    @RequestMapping("/options")
    public R options() {
        return R.ok().put("list", sysCoachLevelService.queryOptions());
    }
}
