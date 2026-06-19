package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.SysInformAtionEntity;
import com.dlc.modules.sys.service.SysInformAtionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/Inform")
public class SysInformAtionController {
    @Autowired
    private SysInformAtionService sysInformAtionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:Inform:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysInformAtionEntity> sysInformAtionEntities = sysInformAtionService.queryList(query);
        int total = sysInformAtionService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(sysInformAtionEntities,total,query.getLimit(),query.getPage());
        return R.ok().put("pages", pageUtils);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{activityTrainId}")
    @RequiresPermissions("sys:Inform:info")
    public R info(@PathVariable("activityTrainId") Long activityTrainId) {
        SysInformAtionEntity sysInformAtionEntity = sysInformAtionService.queryObject(activityTrainId);
        return R.ok().put("sysInformAtionEntity", sysInformAtionEntity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:Inform:save")
    public R save(@RequestBody SysInformAtionEntity sysInformAtionEntity) {
        sysInformAtionEntity.setCreatedDate(new Date());
        //非空验证
        ValidatorUtils.validateEntity(sysInformAtionEntity);
        sysInformAtionService.save(sysInformAtionEntity);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:Inform:update")
    public R update(@RequestBody SysInformAtionEntity sysInformAtionEntity) {
        ValidatorUtils.validateEntity(sysInformAtionEntity);
        sysInformAtionService.update(sysInformAtionEntity);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:Inform:delete")
    public R delete(@RequestBody Long[] activityTrainIds) {
        sysInformAtionService.deleteBatch(activityTrainIds);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/imgList")
    @RequiresPermissions("sys:Inform:list")
    public R imgList(@RequestParam Map<String, Object> params) {
        params.put("imgType","img");
        Query query = new Query(params);
        List<SysInformAtionEntity> sysInformAtionEntities = sysInformAtionService.queryList(query);
        int total = sysInformAtionService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(sysInformAtionEntities,total,query.getLimit(),query.getPage());
        return R.ok().put("pages", pageUtils);
    }
}
