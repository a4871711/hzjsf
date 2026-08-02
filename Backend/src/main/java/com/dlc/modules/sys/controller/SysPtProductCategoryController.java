package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtProductCategoryEntity;
import com.dlc.modules.sys.service.SysPtProductCategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 私教商品分类管理。
 */
@RestController
@RequestMapping("/sys/ptProductCategory")
public class SysPtProductCategoryController extends AbstractController {

    @Autowired
    private SysPtProductCategoryService sysPtProductCategoryService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptproductcategory:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<PtProductCategoryEntity> list = sysPtProductCategoryService.queryList(query);
        int total = sysPtProductCategoryService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptproductcategory:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("category", sysPtProductCategoryService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:ptproductcategory:save")
    public R save(@RequestBody PtProductCategoryEntity category) {
        category.setCreatedBy(getUserId());
        category.setUpdatedBy(getUserId());
        sysPtProductCategoryService.save(category);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:ptproductcategory:update")
    public R update(@RequestBody PtProductCategoryEntity category) {
        category.setUpdatedBy(getUserId());
        sysPtProductCategoryService.update(category);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:ptproductcategory:delete")
    public R delete(@RequestBody Long[] ids) {
        sysPtProductCategoryService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/updateStatus")
    @RequiresPermissions("sys:ptproductcategory:updateStatus")
    public R updateStatus(@RequestBody PtProductCategoryEntity category) {
        sysPtProductCategoryService.updateStatus(category.getId(), category.getStatus());
        return R.ok();
    }

    /** 启用分类下拉项（供商品表单使用，无需单独按钮权限） */
    @RequestMapping("/options")
    public R options() {
        return R.ok().put("list", sysPtProductCategoryService.queryOptions());
    }
}
