package com.dlc.modules.sys.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysPrivateReportService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 私教收入报表(第23步 §19)。路径 /sys/privateReport。纯统计查询,无写操作。
 * 权限统一 sys:privateReport:list;beginDate/endDate 必填;门店隔离 storeIds。
 * 口径见 mapper/sys/PtPrivateReportDao.xml(先按 appointment 行算单次收入再 SUM,成本命中课时费缺配=0 进异常分流)。
 * 跨店结算规则本期不落账,报表不按其拆分门店归属(§17.1/§17.3)。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/privateReport")
public class SysPrivateReportController extends AbstractController {

    @Autowired
    private SysPrivateReportService sysPrivateReportService;

    /** 顶部 4 卡片汇总。 */
    @RequestMapping("/summary")
    @RequiresPermissions("sys:privateReport:list")
    public R summary(@RequestParam Map<String, Object> params) {
        R check = prepare(params);
        if (check != null) { return check; }
        return R.ok().put("summary", sysPrivateReportService.summary(params));
    }

    /** 门店分组报表。 */
    @RequestMapping("/storeReport")
    @RequiresPermissions("sys:privateReport:list")
    public R storeReport(@RequestParam Map<String, Object> params) {
        R check = prepare(params);
        if (check != null) { return check; }
        return R.ok().put("page", sysPrivateReportService.storeReport(params));
    }

    /** 教练分组报表。 */
    @RequestMapping("/coachReport")
    @RequiresPermissions("sys:privateReport:list")
    public R coachReport(@RequestParam Map<String, Object> params) {
        R check = prepare(params);
        if (check != null) { return check; }
        return R.ok().put("page", sysPrivateReportService.coachReport(params));
    }

    /** 课程分组报表。 */
    @RequestMapping("/courseReport")
    @RequiresPermissions("sys:privateReport:list")
    public R courseReport(@RequestParam Map<String, Object> params) {
        R check = prepare(params);
        if (check != null) { return check; }
        return R.ok().put("page", sysPrivateReportService.courseReport(params));
    }

    /** 明细报表（一行=一次已完成核销）。 */
    @RequestMapping("/detailReport")
    @RequiresPermissions("sys:privateReport:list")
    public R detailReport(@RequestParam Map<String, Object> params) {
        R check = prepare(params);
        if (check != null) { return check; }
        return R.ok().put("page", sysPrivateReportService.detailReport(params));
    }

    /** 异常数据列表（成本缺配/缺门店/缺教练/课时非法），不进正式汇总。 */
    @RequestMapping("/abnormalList")
    @RequiresPermissions("sys:privateReport:list")
    public R abnormalList(@RequestParam Map<String, Object> params) {
        R check = prepare(params);
        if (check != null) { return check; }
        return R.ok().put("page", sysPrivateReportService.abnormalList(params));
    }

    /**
     * 统一前置：校验 beginDate/endDate 必填 + 注入门店隔离 storeIds（超管为空则不过滤）。
     * 返回非 null 表示校验失败，直接回该 R。
     */
    private R prepare(Map<String, Object> params) {
        Object begin = params.get("beginDate");
        Object end = params.get("endDate");
        if (begin == null || StringUtils.isBlank(begin.toString())
                || end == null || StringUtils.isBlank(end.toString())) {
            return R.error("请选择统计日期");
        }
        // 门店数据隔离：非超管按所属门店过滤（超管 storeIds 为空则不过滤）
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        return null;
    }
}
