package com.dlc.modules.sys.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysDeviceEntity;
import com.dlc.modules.sys.service.SysDeviceService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;



/**
 * 我的设备
 *
 * @author wangsheng
 * @email 
 * @date 2018-09-13 10:16:12
 */
@RestController
@RequestMapping("sys/device")
public class SysDeviceController {
    @Autowired
    private SysDeviceService sysDeviceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:device:list")
    public R list(@RequestParam Map<String, Object> params){
    	params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<SysDeviceEntity> sysDeviceList = sysDeviceService.queryList(query);
        int total = sysDeviceService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(sysDeviceList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtils);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{deviceid}")
    @RequiresPermissions("sys:device:info")
    public R info(@PathVariable("deviceid") Long deviceid){
        SysDeviceEntity device = sysDeviceService.queryObject(deviceid);

        return R.ok().put("device", device);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:device:save")
    public R save(@RequestBody SysDeviceEntity device){
        if(device == null){return R.error("请求无效");}
        device.setCreateTime(new Date());
        sysDeviceService.save(device);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:device:update")
    public R update(@RequestBody SysDeviceEntity device){
        if(device == null){return R.error("请求无效");}
        //查看该手环是否已存在绑定
        int flag = sysDeviceService.queryIfExistDeciceNo(device);
        String prompt = "操作成功";
        if(flag > 0){
            //return R.error("该手环已存在");  解绑
            sysDeviceService.updateDeviceInit(device.getDeviceNo());
            prompt = "已将旧绑定记录解除，并重新绑定给当前用户";
        }
        int res = sysDeviceService.update(device);//全部更新
        if(res <= 0){return R.error("失败");}
        return R.ok(prompt);
    }
    /**
     * 禁用设备
     */
    @RequestMapping("/forbidden")
    @RequiresPermissions("sys:device:update")
    public R forbidden(@RequestBody Long[] deviceIds){
        int res = sysDeviceService.batchForbidden(deviceIds);//全部更新
        if(res <= 0){
            return R.error("失败");
        }
        return R.ok();
    }
    /**
     * 解除禁用设备
     */
    @RequestMapping("/unForbidden")
    @RequiresPermissions("sys:device:update")
    public R unForbidden(@RequestBody Long[] deviceIds){
        int res = sysDeviceService.batchUnForbidden(deviceIds);//全部更新
        if(res <= 0){
            return R.error("失败");
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:device:delete")
    public R delete(@RequestBody Long[] deviceIds){
        sysDeviceService.deleteBatch(deviceIds);

        return R.ok();
    }

}
