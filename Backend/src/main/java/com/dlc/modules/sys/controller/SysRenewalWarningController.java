package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtRenewalWarningRuleEntity;
import com.dlc.modules.sys.service.SysRenewalWarningService;
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
 * 续费预警管理（pt_renewal_warning_rule/_record，第22步·运营域）。路径 /sys/renewalWarning。
 * 规则配置（rule 主表 + 适用门店 rel，全删全插）+ 预警记录处理（跟进/续费/忽略）。
 * 记录由 api 侧 RenewalWarningScanTask 每日 0:50 扫描生成；提醒对象固定为教练。
 *
 * perms 与 sql/sys_menu_ops.sql 逐字对齐——菜单平铺 list/info/save/update/delete/follow 六个按钮权限：
 *   - 规则 ruleList/ruleInfo/ruleSave/ruleUpdate/ruleDelete → list/info/save/update/delete；
 *   - 记录 recordList 复用 :list；follow → :follow；markStatus（已续费/已忽略）复用 :update。
 * 门店隔离：记录表带 store_id，storeIds 为逗号分隔串（超管空=全部），记录列表/跟进/标记状态均按其收口。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/renewalWarning")
public class SysRenewalWarningController extends AbstractController {

    @Autowired
    private SysRenewalWarningService sysRenewalWarningService;

    /* ===== 规则配置 ===== */

    @RequestMapping("/ruleList")
    @RequiresPermissions("sys:renewalWarning:list")
    public R ruleList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        PageUtils page = sysRenewalWarningService.queryRulePage(query);
        return R.ok().put("page", page);
    }

    @RequestMapping("/ruleInfo/{id}")
    @RequiresPermissions("sys:renewalWarning:info")
    public R ruleInfo(@PathVariable("id") Long id) {
        PtRenewalWarningRuleEntity rule = sysRenewalWarningService.queryRule(id);
        if (rule == null) {
            return R.error(404, "续费预警规则不存在");
        }
        return R.ok().put("rule", rule);
    }

    @RequestMapping("/ruleSave")
    @RequiresPermissions("sys:renewalWarning:save")
    public R ruleSave(@RequestBody PtRenewalWarningRuleEntity rule) {
        rule.setCreatedBy(getUserId());
        rule.setUpdatedBy(getUserId());
        sysRenewalWarningService.saveRule(rule);
        return R.ok();
    }

    @RequestMapping("/ruleUpdate")
    @RequiresPermissions("sys:renewalWarning:update")
    public R ruleUpdate(@RequestBody PtRenewalWarningRuleEntity rule) {
        rule.setUpdatedBy(getUserId());
        sysRenewalWarningService.updateRule(rule);
        return R.ok();
    }

    @RequestMapping("/ruleDelete")
    @RequiresPermissions("sys:renewalWarning:delete")
    public R ruleDelete(@RequestBody Long[] ids) {
        sysRenewalWarningService.deleteRule(ids);
        return R.ok();
    }

    /* ===== 预警记录处理 ===== */

    @RequestMapping("/recordList")
    @RequiresPermissions("sys:renewalWarning:list")
    public R recordList(@RequestParam Map<String, Object> params) {
        // 门店数据隔离：非超管按所属门店过滤（超管 storeIds 为空则不过滤）
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        PageUtils page = sysRenewalWarningService.queryRecordPage(query);
        return R.ok().put("page", page);
    }

    /** 跟进：{recordId, followRemark}；置 follow_status=1 + 记备注 */
    @RequestMapping("/follow")
    @RequiresPermissions("sys:renewalWarning:follow")
    public R follow(@RequestBody Map<String, Object> params) {
        Long recordId = parseLong(params.get("recordId"));
        String followRemark = params.get("followRemark") == null ? null : params.get("followRemark").toString();
        if (recordId == null) {
            return R.error("缺少参数：recordId");
        }
        sysRenewalWarningService.follow(recordId, followRemark, ShiroUtils.getUserEntity().getStoreIds());
        return R.ok();
    }

    /** 标记状态：{recordId, followStatus(2已续费/3已忽略)}；写 closed_at 关闭记录 */
    @RequestMapping("/markStatus")
    @RequiresPermissions("sys:renewalWarning:update")
    public R markStatus(@RequestBody Map<String, Object> params) {
        Long recordId = parseLong(params.get("recordId"));
        Integer followStatus = parseInt(params.get("followStatus"));
        if (recordId == null) {
            return R.error("缺少参数：recordId");
        }
        sysRenewalWarningService.markStatus(recordId, followStatus, ShiroUtils.getUserEntity().getStoreIds());
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
