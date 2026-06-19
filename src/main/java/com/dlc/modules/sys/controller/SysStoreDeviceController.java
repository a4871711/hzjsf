package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysStoreDeviceEntity;
import com.dlc.modules.sys.service.SysStoreDeviceService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.R;



/**
 * 门店设备表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-07 10:46:29
 */
@RestController
@RequestMapping("sys/storedevice")
public class SysStoreDeviceController {
    @Autowired
    private SysStoreDeviceService sysStoreDeviceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:storedevice:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<SysStoreDeviceEntity> sysStoreDeviceList = sysStoreDeviceService.queryList(query);
        int total = sysStoreDeviceService.queryTotal(query);
        PageUtils page = new PageUtils(sysStoreDeviceList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{sdId}")
    @RequiresPermissions("sys:storedevice:info")
    public R info(@PathVariable("sdId") Long sdId){
        SysStoreDeviceEntity storeDevice = sysStoreDeviceService.queryObject(sdId);

        return R.ok().put("storeDevice", storeDevice);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:storedevice:save")
    public R save(@RequestBody SysStoreDeviceEntity sysStoreDevice){
        if(sysStoreDevice == null || StringUtils.isBlank(sysStoreDevice.getDeviceNo())){
            return R.error("信息缺失");
        }
        sysStoreDevice.setCreateTime(new Date());
        //查询设备编号是不是已存在
        int res = sysStoreDeviceService.queryExistSameDevice(sysStoreDevice.getDeviceNo());
        if( res > 0 ){
           return R.error("设备编号："+sysStoreDevice.getDeviceNo()+" 已存在");
        }
        sysStoreDeviceService.save(sysStoreDevice);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:storedevice:update")
    public R update(@RequestBody SysStoreDeviceEntity sysStoreDevice){
        if(sysStoreDevice == null || StringUtils.isBlank(sysStoreDevice.getDeviceNo())){
            return R.error("信息缺失");
        }
        ValidatorUtils.validateEntity(sysStoreDevice);
        //查询设备编号是不是已存在
        int res = sysStoreDeviceService.querySameDevice(sysStoreDevice);
        if( res > 0 ){
            return R.error("设备编号："+sysStoreDevice.getDeviceNo()+" 已存在");
        }
        sysStoreDeviceService.update(sysStoreDevice);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:storedevice:delete")
    public R delete(@RequestBody Long[] sdIds){
        sysStoreDeviceService.deleteBatch(sdIds);

        return R.ok();
    }

}
