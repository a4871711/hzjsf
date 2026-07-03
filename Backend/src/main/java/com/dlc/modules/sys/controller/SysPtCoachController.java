package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.service.SysPtCoachService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教教练管理（pt_coach）。
 * 注意：路径 /sys/ptCoach，避免与旧教练模块 SysCoachController(/sys/coach) 冲突。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/ptCoach")
public class SysPtCoachController extends AbstractController {

    @Autowired
    private SysPtCoachService sysPtCoachService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptCoach:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据隔离：非超管按所属门店过滤（超管 storeIds 为空则不过滤）
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<PtCoachEntity> list = sysPtCoachService.queryList(query);
        int total = sysPtCoachService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptCoach:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("coach", sysPtCoachService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:ptCoach:save")
    public R save(@RequestBody PtCoachEntity coach) {
        coach.setCreatedBy(getUserId());
        coach.setUpdatedBy(getUserId());
        sysPtCoachService.save(coach);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:ptCoach:update")
    public R update(@RequestBody PtCoachEntity coach) {
        coach.setUpdatedBy(getUserId());
        sysPtCoachService.update(coach);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:ptCoach:delete")
    public R delete(@RequestBody Long[] ids) {
        sysPtCoachService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:ptCoach:changeStatus")
    public R changeStatus(@RequestBody PtCoachEntity coach) {
        sysPtCoachService.changeStatus(coach.getId(), coach.getStatus(), coach.getDisableReason());
        return R.ok();
    }

    /**
     * 教练预约只读抽屉。
     * TODO 第14步：接入 pt_private_appointment 后返回该教练的预约列表，当前先返回空。
     */
    @RequestMapping("/appointments/{id}")
    @RequiresPermissions("sys:ptCoach:appointments")
    public R appointments(@PathVariable("id") Long id) {
        return R.ok().put("list", new java.util.ArrayList<>());
    }
}
