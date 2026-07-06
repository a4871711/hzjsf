package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.MkFlashSaleActivityEntity;
import com.dlc.modules.sys.service.SysMarketingFlashSaleService;
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
 * 限时秒杀活动管理（营销域）。路径 /sys/mkFlashSale，perms 与菜单脚本 sys_menu_marketing.sql 逐字一致。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/mkFlashSale")
public class SysMarketingFlashSaleController extends AbstractController {

    @Autowired
    private SysMarketingFlashSaleService sysMarketingFlashSaleService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:mkFlashSale:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<MkFlashSaleActivityEntity> list = sysMarketingFlashSaleService.queryList(query);
        int total = sysMarketingFlashSaleService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:mkFlashSale:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("activity", sysMarketingFlashSaleService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:mkFlashSale:save")
    public R save(@RequestBody MkFlashSaleActivityEntity activity) {
        activity.setCreatedBy(getUserId());
        activity.setUpdatedBy(getUserId());
        sysMarketingFlashSaleService.save(activity);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:mkFlashSale:update")
    public R update(@RequestBody MkFlashSaleActivityEntity activity) {
        activity.setUpdatedBy(getUserId());
        sysMarketingFlashSaleService.update(activity);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:mkFlashSale:delete")
    public R delete(@RequestBody Long[] ids) {
        sysMarketingFlashSaleService.deleteBatch(ids);
        return R.ok();
    }

    /** 上下架切换（复用 update 权限串）：{id, status(1上架/0下架)}；下架后不可继续购买 */
    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:mkFlashSale:update")
    public R changeStatus(@RequestBody Map<String, Object> params) {
        Long id = toLong(params.get("id"));
        Integer status = toInt(params.get("status"));
        sysMarketingFlashSaleService.changeStatus(id, status, getUserId());
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
