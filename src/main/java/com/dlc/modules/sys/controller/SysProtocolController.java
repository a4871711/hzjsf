package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.Protocol;
import com.dlc.modules.sys.service.SysProtocolService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sys/protocol")
public class SysProtocolController {
    @Autowired
    private SysProtocolService sysProtocolService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:protocol:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<Protocol> sysProtocolList = sysProtocolService.queryList(query);
        int total = sysProtocolService.queryTotal(query);
        PageUtils page = new PageUtils(sysProtocolList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }
    /**
     * 详情
     */
    @RequestMapping("/info/{pId}")
    @RequiresPermissions("sys:protocol:info")
    public R info(@PathVariable("pId") Long pId){
        Protocol protocol = sysProtocolService.queryObject(pId);
        return R.ok().put("protocol", protocol);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:protocol:save")
    public R save(@RequestBody Protocol protocol){
        //是否已存在
        int res = sysProtocolService.queryIfExist(protocol.getType());
        if (res > 0) {
            return R.error("该类型已存在");
        }
        int r = sysProtocolService.save(protocol);
        if(r > 0){
            return R.ok();
        }
        return R.error();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:protocol:update")
    public R update(@RequestBody Protocol protocol){
        ValidatorUtils.validateEntity(protocol);
        int res = sysProtocolService.update(protocol);//全部更新
        if(res > 0){
            return R.ok();
        }
        return R.error();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:protocol:delete")
    public R delete(@RequestBody Long[] pIds){
        int res = sysProtocolService.deleteBatch(pIds);
        if(res > 0){
            return R.ok();
        }
        return R.error();
    }
}
