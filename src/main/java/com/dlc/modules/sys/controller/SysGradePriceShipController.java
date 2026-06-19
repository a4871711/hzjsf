package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.SysGradePriceShipEntity;
import com.dlc.modules.sys.service.SysGradePriceShipService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/leve")
public class SysGradePriceShipController {
    @Autowired
    private SysGradePriceShipService sysLevelPriceShipService;
    /**
     * 兴趣标签
     * @return
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:datamap:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysGradePriceShipEntity> entities= sysLevelPriceShipService.queryList(query);
        int total = sysLevelPriceShipService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(entities,total,query.getLimit(),query.getPage());
        return R.ok().put("pages",pageUtils);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{dataMapId}")
    @RequiresPermissions("sys:datamap:info")
    public R info(@PathVariable("dataMapId") Long dataMapId) {
        SysGradePriceShipEntity entity = sysLevelPriceShipService.queryObject(dataMapId);
        return R.ok().put("entity", entity);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:datamap:save")
    public R save(@RequestBody SysGradePriceShipEntity entity) {
        //非空验证
        ValidatorUtils.validateEntity(entity);
        sysLevelPriceShipService.save(entity);
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:datamap:update")
    public R update(@RequestBody SysGradePriceShipEntity entity) {
        //非空验证
        ValidatorUtils.validateEntity(entity);
        sysLevelPriceShipService.update(entity);//全部更新
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:datamap:delete")
    public R delete(@RequestBody Long[] fitCardIds) {
        sysLevelPriceShipService.deleteBatch(fitCardIds);
        return R.ok();
    }
}
