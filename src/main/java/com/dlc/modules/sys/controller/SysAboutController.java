package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.SysAboutEntity;
import com.dlc.modules.sys.service.SysAboutService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/about")
public class SysAboutController {
    @Autowired
    private SysAboutService sysAboutService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:about:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysAboutEntity> entityList = sysAboutService.queryList(query);
        int total = sysAboutService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(entityList,total,query.getLimit(),query.getPage());
        return R.ok().put("pages", pageUtils);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:about:info")
    public R info(@PathVariable("id") Long id) {
        SysAboutEntity entity = sysAboutService.queryObject(id);
        return R.ok().put("entity", entity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:about:save")
    public R save(@RequestBody SysAboutEntity entity) {
        entity.setCreateTime(new Date());
        //非空验证
        ValidatorUtils.validateEntity(entity);

        sysAboutService.save(entity);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:about:update")
    public R update(@RequestBody SysAboutEntity entity) {
        ValidatorUtils.validateEntity(entity);
        sysAboutService.update(entity);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:about:delete")
    public R delete(@RequestBody Long[] ids) {
        sysAboutService.deleteBatch(ids);
        return R.ok();
    }
}
