package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.MkGroupBuyActivityEntity;
import com.dlc.modules.sys.service.SysMarketingGroupBuyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 拼团活动管理（营销域）。路径 /sys/mkGroupBuy，perms 与菜单脚本 sys_menu_marketing.sql 逐字一致。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/mkGroupBuy")
public class SysMarketingGroupBuyController extends AbstractController {

    @Autowired
    private SysMarketingGroupBuyService sysMarketingGroupBuyService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:mkGroupBuy:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<MkGroupBuyActivityEntity> list = sysMarketingGroupBuyService.queryList(query);
        int total = sysMarketingGroupBuyService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:mkGroupBuy:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("activity", sysMarketingGroupBuyService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:mkGroupBuy:save")
    public R save(@RequestBody MkGroupBuyActivityEntity activity) {
        activity.setCreatedBy(getUserId());
        activity.setUpdatedBy(getUserId());
        sysMarketingGroupBuyService.save(activity);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:mkGroupBuy:update")
    public R update(@RequestBody MkGroupBuyActivityEntity activity) {
        activity.setUpdatedBy(getUserId());
        sysMarketingGroupBuyService.update(activity);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:mkGroupBuy:delete")
    public R delete(@RequestBody Long[] ids) {
        sysMarketingGroupBuyService.deleteBatch(ids);
        return R.ok();
    }

    /** 上下架切换（复用 update 权限串）：{id, status(1上架/0下架)}；下架后不可发起新团 */
    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:mkGroupBuy:update")
    public R changeStatus(@RequestBody Map<String, Object> params) {
        Long id = toLong(params.get("id"));
        Integer status = toInt(params.get("status"));
        sysMarketingGroupBuyService.changeStatus(id, status, getUserId());
        return R.ok();
    }

    private Long toLong(Object v) {
        if (v == null || "".equals(String.valueOf(v).trim())) {
            return null;
        }
        return Long.valueOf(String.valueOf(v).trim());
    }

    private Integer toInt(Object v) {
        if (v == null || "".equals(String.valueOf(v).trim())) {
            return null;
        }
        return Integer.valueOf(String.valueOf(v).trim());
    }
}
