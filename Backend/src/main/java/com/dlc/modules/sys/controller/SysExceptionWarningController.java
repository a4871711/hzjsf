package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtExceptionWarningRuleEntity;
import com.dlc.modules.sys.service.SysExceptionWarningService;
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
 * 异常预警中心（pt_exception_warning_rule/_record，第22步·运营域）。路径 /sys/exceptionWarning。
 * 规则管理（五法，applicable_store_ids JSON，空=全部门店）+ 预警记录只读（§18.4 不做处理态流转）+ 顶部统计。
 * 记录由 api 侧 ExceptionWarningScanTask 每日 1:10 扫描生成。
 *
 * perms 与 sql/sys_menu_ops.sql 逐字对齐——菜单平铺 list/info/save/update/delete 五个按钮权限：
 *   - 规则 ruleList/ruleInfo/ruleSave/ruleUpdate/ruleDelete → list/info/save/update/delete；
 *   - 记录 recordList / stat 复用 :list。
 * 门店隔离：记录表 store_id 可空，recordList/stat 按 storeIds（超管空=全部）收口。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/exceptionWarning")
public class SysExceptionWarningController extends AbstractController {

    @Autowired
    private SysExceptionWarningService sysExceptionWarningService;

    /* ===== 规则五法 ===== */

    @RequestMapping("/ruleList")
    @RequiresPermissions("sys:exceptionWarning:list")
    public R ruleList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        PageUtils page = sysExceptionWarningService.queryRulePage(query);
        return R.ok().put("page", page);
    }

    @RequestMapping("/ruleInfo/{id}")
    @RequiresPermissions("sys:exceptionWarning:info")
    public R ruleInfo(@PathVariable("id") Long id) {
        PtExceptionWarningRuleEntity rule = sysExceptionWarningService.queryRule(id);
        if (rule == null) {
            return R.error(404, "异常预警规则不存在");
        }
        return R.ok().put("rule", rule);
    }

    @RequestMapping("/ruleSave")
    @RequiresPermissions("sys:exceptionWarning:save")
    public R ruleSave(@RequestBody PtExceptionWarningRuleEntity rule) {
        rule.setCreatedBy(getUserId());
        rule.setUpdatedBy(getUserId());
        sysExceptionWarningService.saveRule(rule);
        return R.ok();
    }

    @RequestMapping("/ruleUpdate")
    @RequiresPermissions("sys:exceptionWarning:update")
    public R ruleUpdate(@RequestBody PtExceptionWarningRuleEntity rule) {
        rule.setUpdatedBy(getUserId());
        sysExceptionWarningService.updateRule(rule);
        return R.ok();
    }

    @RequestMapping("/ruleDelete")
    @RequiresPermissions("sys:exceptionWarning:delete")
    public R ruleDelete(@RequestBody Long[] ids) {
        sysExceptionWarningService.deleteRule(ids);
        return R.ok();
    }

    /* ===== 预警记录（只读） ===== */

    @RequestMapping("/recordList")
    @RequiresPermissions("sys:exceptionWarning:list")
    public R recordList(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        PageUtils page = sysExceptionWarningService.queryRecordPage(query);
        return R.ok().put("page", page);
    }

    /** 顶部统计卡片：{todayNew, frequentCancel, lessonAbnormal, enabledRuleCount} */
    @RequestMapping("/stat")
    @RequiresPermissions("sys:exceptionWarning:list")
    public R stat(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        return R.ok().put("stat", sysExceptionWarningService.queryStat(params));
    }
}
