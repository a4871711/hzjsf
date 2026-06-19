package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.api.entity.ActivityType;
import com.dlc.modules.sys.entity.SysActivityTrainEntity;
import com.dlc.modules.sys.service.SysActivityTrainService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/activ")
public class SysActivityTrainController {
    @Autowired
    private SysActivityTrainService activityTrainService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:activ:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysActivityTrainEntity> activityTrainEntities = activityTrainService.queryList(query);
        int total = activityTrainService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(activityTrainEntities,total,query.getLimit(),query.getPage());
        return R.ok().put("pages", pageUtils).put("activTypeList", activityTrainService.selectAllActivType());
    }
    /**
     * 列表type
     */
    @RequestMapping("/getActivTypeList")
    @RequiresPermissions("sys:activ:list")
    public R getActivEngineList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<ActivityType> list = activityTrainService.queryActivList(query);
        int total = activityTrainService.queryActivTotal(query);
        PageUtils pageUtils=new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.ok().put("activPage", pageUtils).put("activTypeList", activityTrainService.selectAllActivType());
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{activityTrainId}")
    @RequiresPermissions("sys:activ:info")
    public R info(@PathVariable("activityTrainId") Long activityTrainId) {
        SysActivityTrainEntity activityTrainEntity = activityTrainService.queryObject(activityTrainId);
        return R.ok().put("activityTrainEntity", activityTrainEntity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:activ:save")
    public R save(@RequestBody SysActivityTrainEntity activityTrainEntity) {
        activityTrainEntity.setCreatedDate(new Date());
        //非空验证
        ValidatorUtils.validateEntity(activityTrainEntity);
        activityTrainService.save(activityTrainEntity);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:activ:update")
    public R update(@RequestBody SysActivityTrainEntity activityTrainEntity) {
        ValidatorUtils.validateEntity(activityTrainEntity);
        activityTrainService.update(activityTrainEntity);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:activ:delete")
    public R delete(@RequestBody Long[] activityTrainIds) {
        activityTrainService.deleteBatch(activityTrainIds);
        return R.ok();
    }
    /**
     * 删除type
     */
    @RequestMapping("/deleteAt")
    @RequiresPermissions("sys:activ:delete")
    public R deleteAt(@RequestBody Long atId) {
        activityTrainService.deleteActiveType(atId);
        return R.ok();
    }
    /**
     * 保存type
     */
    @RequestMapping("/saveActivType")
    @RequiresPermissions("sys:activ:save")
    public R saveActivType(@RequestBody ActivityType activityType) {
        activityType.setCreatedDate(new Date());
        //非空验证
        ValidatorUtils.validateEntity(activityType);
        activityTrainService.saveActivityType(activityType);
        return R.ok();
    }

    /**
     * 修改type
     */
    @RequestMapping("/updateActivType")
    @RequiresPermissions("sys:activ:update")
    public R updateActivType(@RequestBody ActivityType activityType) {
        activityType.setCreatedDate(new Date());
        ValidatorUtils.validateEntity(activityType);
        activityTrainService.updateActivityType(activityType);//全部更新
        return R.ok();
    }
}
