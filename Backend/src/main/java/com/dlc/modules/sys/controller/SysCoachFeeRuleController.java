package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import com.dlc.modules.sys.entity.PtProductEntity;
import com.dlc.modules.sys.service.SysCoachFeeRuleService;
import com.dlc.modules.sys.service.SysPtCoachService;
import com.dlc.modules.sys.service.SysPtProductService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教练分成规则（按课时提成/整单提成）。路径 /sys/commission。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/commission")
public class SysCoachFeeRuleController extends AbstractController {

    @Autowired
    private SysCoachFeeRuleService sysCoachFeeRuleService;
    @Autowired
    private SysPtCoachService sysPtCoachService;
    @Autowired
    private SysPtProductService sysPtProductService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:commission:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        Query query = new Query(params);
        List<PtCoachFeeRuleEntity> list = sysCoachFeeRuleService.queryList(query);
        int total = sysCoachFeeRuleService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 分成页专用下拉，避免依赖“教练排班”和“私教商品”菜单权限。
     * 两类数据都按当前账号的门店地址范围过滤。
     */
    @RequestMapping("/options")
    @RequiresPermissions("sys:commission:list")
    public R options() {
        Map<String, Object> params = new HashMap<>();
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        List<PtProductEntity> products = sysPtProductService.queryList(params);
        if (products != null) {
            // 普通提成页面不展示包月商品，包月商品由教练资料中的专用配置维护。
            products.removeIf(product -> Long.valueOf(3L).equals(product.getProductTypeId()));
        }
        return R.ok()
                .put("coaches", sysPtCoachService.queryList(params))
                .put("products", products);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:commission:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("feeRule", sysCoachFeeRuleService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:commission:save")
    public R save(@RequestBody PtCoachFeeRuleEntity feeRule) {
        feeRule.setCreatedBy(getUserId());
        sysCoachFeeRuleService.save(feeRule);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:commission:update")
    public R update(@RequestBody PtCoachFeeRuleEntity feeRule) {
        feeRule.setUpdatedBy(getUserId());
        sysCoachFeeRuleService.update(feeRule);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:commission:delete")
    public R delete(@RequestBody Long[] ids) {
        sysCoachFeeRuleService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:commission:changeStatus")
    public R changeStatus(@RequestBody PtCoachFeeRuleEntity feeRule) {
        sysCoachFeeRuleService.changeStatus(feeRule.getId(), feeRule.getStatus());
        return R.ok();
    }
}
