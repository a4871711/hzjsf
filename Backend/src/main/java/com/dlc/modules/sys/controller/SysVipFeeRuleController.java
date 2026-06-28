package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.VipFeeRuleEntity;
import com.dlc.modules.sys.service.SysVipFeeRuleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * VIP 转让费用规则(按转让次数分档定额)
 */
@RestController
@RequestMapping("sys/vipFeeRule")
public class SysVipFeeRuleController {
    @Autowired
    private SysVipFeeRuleService sysVipFeeRuleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:vipFeeRule:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<VipFeeRuleEntity> list = sysVipFeeRuleService.queryList(query);
        int total = sysVipFeeRuleService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{feeRuleId}")
    @RequiresPermissions("sys:vipFeeRule:info")
    public R info(@PathVariable("feeRuleId") Long feeRuleId) {
        VipFeeRuleEntity vipFeeRule = sysVipFeeRuleService.queryObject(feeRuleId);
        return R.ok().put("vipFeeRule", vipFeeRule);
    }

    /**
     * 保存(service 内校验 tiersJson 合法性,非法抛 ERROR_VIP_FEE_RULE_FORMAT)
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:vipFeeRule:save")
    public R save(@RequestBody VipFeeRuleEntity vipFeeRule) {
        vipFeeRule.setCreatedDate(new Date());
        sysVipFeeRuleService.save(vipFeeRule);
        return R.ok();
    }

    /**
     * 修改(全量更新;同样校验 tiersJson。改规则只影响后续新发起的转让,不回溯已发起单)
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:vipFeeRule:update")
    public R update(@RequestBody VipFeeRuleEntity vipFeeRule) {
        sysVipFeeRuleService.update(vipFeeRule);
        return R.ok();
    }

    /**
     * 删除(删除前校验是否被 vip_benefit_card.fee_rule_id 引用,被引用则拒删)
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:vipFeeRule:delete")
    public R delete(@RequestBody Long[] feeRuleIds) {
        if (feeRuleIds != null) {
            for (Long feeRuleId : feeRuleIds) {
                if (sysVipFeeRuleService.countCardRefByRuleId(feeRuleId) > 0) {
                    return R.error("该费用规则已被权益卡引用，不能删除");
                }
            }
        }
        sysVipFeeRuleService.deleteBatch(feeRuleIds);
        return R.ok();
    }
}
