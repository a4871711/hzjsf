package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysStoreCoachEntity;
import com.dlc.modules.sys.service.SysStoreCoachService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 场馆教练
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-04-01 15:52:42
 */
@RestController
@RequestMapping("sys/storecoach")
public class SysStoresCoachController {
    @Autowired
    private SysStoreCoachService sysStoreCoachService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:storecoach:list")
    public R list(@RequestParam Map<String, Object> params) {
        //Long storeId=ShiroUtils.getUserEntity().getStoreId();
        //if (storeId!=null){
            params.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
        //}
        Query query = new Query(params);
        List<SysStoreCoachEntity> sysStoreCoachList = sysStoreCoachService.queryList(query);
        int total = sysStoreCoachService.queryTotal(query);
        PageUtils page = new PageUtils(sysStoreCoachList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{scId}")
    @RequiresPermissions("sys:storecoach:infos")
    public R info(@PathVariable("scId") Long scId) {
        SysStoreCoachEntity storeCoach = sysStoreCoachService.queryObject(scId);

        return R.ok().put("storeCoach", storeCoach);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:storecoach:save")
    public R save(@RequestBody SysStoreCoachEntity sysStoreCoach) {
        //Long storeId=ShiroUtils.getUserEntity().getStoreId();
        //if (storeId!=null){
            sysStoreCoach.setStoreId(ShiroUtils.getUserEntity().getStoreIds());
        //}
        sysStoreCoach.setCreatedDate(new Date());
        int result = sysStoreCoachService.save(sysStoreCoach);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error("未知意外，稍后重试");
        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:storecoach:updates")
    public R update(@RequestBody SysStoreCoachEntity sysStoreCoach) {
        ValidatorUtils.validateEntity(sysStoreCoach);
        int result = sysStoreCoachService.update(sysStoreCoach);//全部更新
        if (result > 0) {
            return R.ok();
        } else {
            return R.error("未知意外，稍后重试");
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:storecoach:delete")
    public R delete(@RequestBody Long[] scIds) {
        int result = sysStoreCoachService.deleteBatch(scIds);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error("未知意外，稍后重试");
        }
    }

}
