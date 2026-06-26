package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.service.SysDataMapService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/datamap")
public class SysDataMapController {
    @Autowired
    private SysDataMapService sysDataMapService;

   /**
     * 兴趣标签
     * @return
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:datamap:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList= sysDataMapService.queryList(query);
        int total = sysDataMapService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(dataMapEntityList,total,query.getLimit(),query.getPage());
        return R.ok().put("pages",pageUtils);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{dataMapId}")
    @RequiresPermissions("sys:datamap:info")
    public R info(@PathVariable("dataMapId") Long dataMapId) {
        SysDataMapEntity dataMapEntity = sysDataMapService.queryObject(dataMapId);
        return R.ok().put("dataMapEntity", dataMapEntity);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:datamap:save")
    public R save(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysDataMapService.save(dataMapEntity);
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:datamap:update")
    public R update(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysDataMapService.update(dataMapEntity);//全部更新
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:datamap:delete")
    public R delete(@RequestBody Long[] fitCardIds) {
        sysDataMapService.deleteBatch(fitCardIds);
        return R.ok();
    }
}
