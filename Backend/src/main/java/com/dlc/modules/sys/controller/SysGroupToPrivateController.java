package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtGroupToPrivateLeadEntity;
import com.dlc.modules.sys.entity.PtGroupToPrivateRuleEntity;
import com.dlc.modules.sys.service.SysGroupToPrivateService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 团课转私教（pt_group_to_private_rule/_lead/_follow，第22步·运营域）。路径 /sys/groupToPrivate。
 * 规则配置（五法）+ 转化名单列表/详情 + 发券（复用营销域 grant）/跟进/标记转化。
 * 名单由 api 侧 GroupToPrivateScanTask 每日 3:20 扫描 upsert（uk_member_id，不覆盖人工态）；
 * 会员购买私教后由支付回调 autoConvertOnPaid 自动标记已转化。
 *
 * perms 与 sql/sys_menu_ops.sql 逐字对齐——菜单平铺 list/info/save/update/delete/sendCoupon/follow/markConverted 八个按钮权限：
 *   - 规则 ruleList/ruleSave/ruleUpdate/ruleDelete → list/save/update/delete；
 *   - 名单 leadList 复用 :list，leadInfo → :info；sendCoupon/follow/markConverted 各用同名 perm。
 * 门店隔离：名单表带 store_id（可空），leadList/leadInfo/发券/跟进/转化前均按 storeIds（超管空=全部）收口。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/groupToPrivate")
public class SysGroupToPrivateController extends AbstractController {

    @Autowired
    private SysGroupToPrivateService sysGroupToPrivateService;

    /* ===== 规则配置 ===== */

    @RequestMapping("/ruleList")
    @RequiresPermissions("sys:groupToPrivate:list")
    public R ruleList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        PageUtils page = sysGroupToPrivateService.queryRulePage(query);
        return R.ok().put("page", page);
    }

    @RequestMapping("/ruleInfo/{id}")
    @RequiresPermissions("sys:groupToPrivate:info")
    public R ruleInfo(@PathVariable("id") Long id) {
        PtGroupToPrivateRuleEntity rule = sysGroupToPrivateService.queryRule(id);
        if (rule == null) {
            return R.error(404, "团课转私教规则不存在");
        }
        return R.ok().put("rule", rule);
    }

    @RequestMapping("/ruleSave")
    @RequiresPermissions("sys:groupToPrivate:save")
    public R ruleSave(@RequestBody PtGroupToPrivateRuleEntity rule) {
        rule.setCreatedBy(getUserId());
        rule.setUpdatedBy(getUserId());
        sysGroupToPrivateService.saveRule(rule);
        return R.ok();
    }

    @RequestMapping("/ruleUpdate")
    @RequiresPermissions("sys:groupToPrivate:update")
    public R ruleUpdate(@RequestBody PtGroupToPrivateRuleEntity rule) {
        rule.setUpdatedBy(getUserId());
        sysGroupToPrivateService.updateRule(rule);
        return R.ok();
    }

    @RequestMapping("/ruleDelete")
    @RequiresPermissions("sys:groupToPrivate:delete")
    public R ruleDelete(@RequestBody Long[] ids) {
        sysGroupToPrivateService.deleteRule(ids);
        return R.ok();
    }

    /* ===== 转化名单与跟进 ===== */

    @RequestMapping("/leadList")
    @RequiresPermissions("sys:groupToPrivate:list")
    public R leadList(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        PageUtils page = sysGroupToPrivateService.queryLeadPage(query);
        return R.ok().put("page", page);
    }

    @RequestMapping("/leadInfo/{id}")
    @RequiresPermissions("sys:groupToPrivate:info")
    public R leadInfo(@PathVariable("id") Long id) {
        PtGroupToPrivateLeadEntity lead = sysGroupToPrivateService.queryLeadDetail(id,
                ShiroUtils.getUserEntity().getStoreIds());
        if (lead == null) {
            return R.error(404, "转化名单不存在");
        }
        return R.ok().put("lead", lead).put("followList", lead.getFollowList());
    }

    /** 发券：{leadId, couponId}；状态机防重发，调营销域 grant */
    @RequestMapping("/sendCoupon")
    @RequiresPermissions("sys:groupToPrivate:sendCoupon")
    public R sendCoupon(@RequestBody Map<String, Object> params) {
        Long leadId = parseLong(params.get("leadId"));
        Long couponId = parseLong(params.get("couponId"));
        if (leadId == null) {
            return R.error("缺少参数：leadId");
        }
        if (!sysGroupToPrivateService.existsInScope(leadId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "转化名单不存在");
        }
        sysGroupToPrivateService.sendCoupon(leadId, couponId);
        return R.ok();
    }

    /** 跟进：{leadId, followStatus, followRemark}；插流水 + 回写名单 */
    @RequestMapping("/follow")
    @RequiresPermissions("sys:groupToPrivate:follow")
    public R follow(@RequestBody Map<String, Object> params) {
        Long leadId = parseLong(params.get("leadId"));
        Integer followStatus = parseInt(params.get("followStatus"));
        String followRemark = params.get("followRemark") == null ? null : params.get("followRemark").toString();
        if (leadId == null) {
            return R.error("缺少参数：leadId");
        }
        if (!sysGroupToPrivateService.existsInScope(leadId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "转化名单不存在");
        }
        sysGroupToPrivateService.follow(leadId, followStatus, followRemark, getUserId());
        return R.ok();
    }

    /** 标记已转化：{leadId} */
    @RequestMapping("/markConverted")
    @RequiresPermissions("sys:groupToPrivate:markConverted")
    public R markConverted(@RequestBody Map<String, Object> params) {
        Long leadId = parseLong(params.get("leadId"));
        if (leadId == null) {
            return R.error("缺少参数：leadId");
        }
        if (!sysGroupToPrivateService.existsInScope(leadId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "转化名单不存在");
        }
        sysGroupToPrivateService.markConverted(leadId);
        return R.ok();
    }

    private Long parseLong(Object val) {
        if (val == null || val.toString().trim().isEmpty()) {
            return null;
        }
        return Long.valueOf(val.toString().trim());
    }

    private Integer parseInt(Object val) {
        if (val == null || val.toString().trim().isEmpty()) {
            return null;
        }
        return Integer.valueOf(val.toString().trim());
    }
}
