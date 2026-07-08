package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.VipPauseRuleEntity;
import com.dlc.modules.sys.service.SysVipPauseRuleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * VIP 停卡规则(按停卡天数分档定价)
 */
@RestController
@RequestMapping("sys/vipPauseRule")
public class SysVipPauseRuleController {
    @Autowired
    private SysVipPauseRuleService sysVipPauseRuleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:vipPauseRule:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<VipPauseRuleEntity> list = sysVipPauseRuleService.queryList(query);
        int total = sysVipPauseRuleService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{pauseRuleId}")
    @RequiresPermissions("sys:vipPauseRule:info")
    public R info(@PathVariable("pauseRuleId") Long pauseRuleId) {
        VipPauseRuleEntity vipPauseRule = sysVipPauseRuleService.queryObject(pauseRuleId);
        return R.ok().put("vipPauseRule", vipPauseRule);
    }

    /**
     * 保存(service 内校验 tiersJson 合法性,非法抛 ERROR_VIP_FEE_RULE_FORMAT)
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:vipPauseRule:save")
    public R save(@RequestBody VipPauseRuleEntity vipPauseRule) {
        vipPauseRule.setCreatedDate(new Date());
        sysVipPauseRuleService.save(vipPauseRule);
        return R.ok();
    }

    /**
     * 修改(全量更新;同样校验 tiersJson。改规则只影响后续新发起的停卡,不回溯已发起单)
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:vipPauseRule:update")
    public R update(@RequestBody VipPauseRuleEntity vipPauseRule) {
        sysVipPauseRuleService.update(vipPauseRule);
        return R.ok();
    }

    /**
     * 删除(删除前校验是否被 vip_benefit_card.pause_rule_id 引用,被引用则拒删)
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:vipPauseRule:delete")
    public R delete(@RequestBody Long[] pauseRuleIds) {
        if (pauseRuleIds != null) {
            for (Long pauseRuleId : pauseRuleIds) {
                if (sysVipPauseRuleService.countCardRefByRuleId(pauseRuleId) > 0) {
                    return R.error("该停卡规则已被权益卡引用，不能删除");
                }
            }
        }
        sysVipPauseRuleService.deleteBatch(pauseRuleIds);
        return R.ok();
    }
}
