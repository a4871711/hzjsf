package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysStoreCoachEvaluateEntity;
import com.dlc.modules.sys.service.SysStoreCoachEvaluateService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 门店教练评价表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-04-02 09:09:30
 */
@RestController
@RequestMapping("sys/storecoachevaluate")
public class SysStoreCoachEvaluateController {
    @Autowired
    private SysStoreCoachEvaluateService sysStoreCoachEvaluateService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("sys:storecoachevaluate:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysStoreCoachEvaluateEntity> sysStoreCoachEvaluateList = sysStoreCoachEvaluateService.queryList(query);
        int total = sysStoreCoachEvaluateService.queryTotal(query);
        PageUtils page = new PageUtils(sysStoreCoachEvaluateList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{evaluateId}")
    @RequiresPermissions("sys:storecoachevaluate:info")
    public R info(@PathVariable("evaluateId") Long evaluateId) {
        SysStoreCoachEvaluateEntity storeCoachEvaluate = sysStoreCoachEvaluateService.queryObject(evaluateId);

        return R.ok().put("storeCoachEvaluate", storeCoachEvaluate);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:storecoachevaluate:save")
    public R save(@RequestBody SysStoreCoachEvaluateEntity sysStoreCoachEvaluate) {
        int result = sysStoreCoachEvaluateService.save(sysStoreCoachEvaluate);
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
    @RequiresPermissions("sys:storecoachevaluate:update")
    public R update(@RequestBody SysStoreCoachEvaluateEntity sysStoreCoachEvaluate) {
        ValidatorUtils.validateEntity(sysStoreCoachEvaluate);
        int result = sysStoreCoachEvaluateService.update(sysStoreCoachEvaluate);//全部更新
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
    @RequiresPermissions("sys:storecoachevaluate:delete")
    public R delete(@RequestBody Long[] evaluateIds) {
        int result = sysStoreCoachEvaluateService.deleteBatch(evaluateIds);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error("未知意外，稍后重试");
        }
    }

}
