package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import com.dlc.modules.sys.service.SysCoachFeeRuleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教练分成规则（课时费/销售提成）。路径 /sys/commission。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/commission")
public class SysCoachFeeRuleController extends AbstractController {

    @Autowired
    private SysCoachFeeRuleService sysCoachFeeRuleService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:commission:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<PtCoachFeeRuleEntity> list = sysCoachFeeRuleService.queryList(query);
        int total = sysCoachFeeRuleService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
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
