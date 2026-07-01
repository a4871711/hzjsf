package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtProductTypeEntity;
import com.dlc.modules.sys.service.SysPtProductTypeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教商品类型管理。路径 /sys/ptProductType，perms 全小写 sys:ptproducttype:*（对齐总则 §0.5）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/ptProductType")
public class SysPtProductTypeController extends AbstractController {

    @Autowired
    private SysPtProductTypeService sysPtProductTypeService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptproducttype:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<PtProductTypeEntity> list = sysPtProductTypeService.queryList(query);
        int total = sysPtProductTypeService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptproducttype:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("productType", sysPtProductTypeService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:ptproducttype:save")
    public R save(@RequestBody PtProductTypeEntity productType) {
        productType.setCreatedBy(getUserId());
        productType.setUpdatedBy(getUserId());
        sysPtProductTypeService.save(productType);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:ptproducttype:update")
    public R update(@RequestBody PtProductTypeEntity productType) {
        productType.setUpdatedBy(getUserId());
        sysPtProductTypeService.update(productType);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:ptproducttype:delete")
    public R delete(@RequestBody Long[] ids) {
        sysPtProductTypeService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/updateStatus")
    @RequiresPermissions("sys:ptproducttype:updateStatus")
    public R updateStatus(@RequestBody PtProductTypeEntity productType) {
        sysPtProductTypeService.updateStatus(productType.getId(), productType.getStatus());
        return R.ok();
    }

    /** 启用类型下拉项（供商品表单使用，无需单独按钮权限） */
    @RequestMapping("/options")
    public R options() {
        return R.ok().put("list", sysPtProductTypeService.queryOptions());
    }
}
