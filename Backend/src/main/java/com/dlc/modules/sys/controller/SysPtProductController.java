package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtProductEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;
import com.dlc.modules.sys.service.SysPtProductService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 私教商品管理。路径 /sys/ptProduct，perms 全小写 sys:ptproduct:*（对齐总则 §0.5）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/ptProduct")
public class SysPtProductController extends AbstractController {

    @Autowired
    private SysPtProductService sysPtProductService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptproduct:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<PtProductEntity> list = sysPtProductService.queryList(query);
        int total = sysPtProductService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptproduct:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("product", sysPtProductService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:ptproduct:save")
    public R save(@RequestBody PtProductEntity product) {
        product.setCreatedBy(getUserId());
        product.setUpdatedBy(getUserId());
        sysPtProductService.save(product);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:ptproduct:update")
    public R update(@RequestBody PtProductEntity product) {
        product.setUpdatedBy(getUserId());
        sysPtProductService.update(product);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:ptproduct:delete")
    public R delete(@RequestBody Long[] ids) {
        sysPtProductService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/onCard")
    @RequiresPermissions("sys:ptproduct:update")
    public R onCard(@RequestBody Long[] ids) {
        sysPtProductService.onCard(ids);
        return R.ok();
    }

    @RequestMapping("/offCard")
    @RequiresPermissions("sys:ptproduct:update")
    public R offCard(@RequestBody Long[] ids) {
        sysPtProductService.offCard(ids);
        return R.ok();
    }

    @RequestMapping("/copy/{id}")
    @RequiresPermissions("sys:ptproduct:save")
    public R copy(@PathVariable("id") Long id) {
        sysPtProductService.copy(id);
        return R.ok();
    }

    /** 指定团课商品下拉项（供附赠团课权益配置使用，无需单独按钮权限） */
    @RequestMapping("/groupClassOptions")
    public R groupClassOptions() {
        List<TeamClassEntity> list = sysPtProductService.groupClassOptions();
        return R.ok().put("list", list);
    }
}
