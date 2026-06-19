package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysShareEntity;
import com.dlc.modules.sys.service.SysShareService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.R;



/**
 * 分享表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-13 17:53:50
 */
@RestController
@RequestMapping("sys/share")
public class SysShareController {
    @Autowired
    private SysShareService sysShareService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:share:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysShareEntity> sysShareList = sysShareService.queryList(query);
        int total = sysShareService.queryTotal(query);
        PageUtils page = new PageUtils(sysShareList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{shareId}")
    @RequiresPermissions("sys:share:info")
    public R info(@PathVariable("shareId") Long shareId){
        SysShareEntity share = sysShareService.queryObject(shareId);

        return R.ok().put("share", share);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:share:save")
    public R save(@RequestBody SysShareEntity sysShare){
        sysShare.setCreatedDate(new Date());
        sysShareService.save(sysShare);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:share:update")
    public R update(@RequestBody SysShareEntity sysShare){
        ValidatorUtils.validateEntity(sysShare);
        sysShareService.update(sysShare);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:share:delete")
    public R delete(@RequestBody Long[] shareIds){
        sysShareService.deleteBatch(shareIds);

        return R.ok();
    }

}
