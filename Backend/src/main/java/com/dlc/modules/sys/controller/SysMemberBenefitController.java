package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysMemberBenefitService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 会员私教权益(pt_member_private_benefit,第15步)。路径 /sys/memberBenefit。
 * 课时账本只能由下单/预约/退款链路驱动;后台动作只允许批量调整权益到期日和未来归属门店。
 * 门店隔离:storeAddrIds 为空(超管)不过滤;越权详情返 404。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/memberBenefit")
public class SysMemberBenefitController extends AbstractController {

    @Autowired
    private SysMemberBenefitService sysMemberBenefitService;

    /** 列表 + 顶部统计卡(stat 随 list 一并返回,统计不吃 status 筛选、按状态 CASE 分桶) */
    @RequestMapping("/list")
    @RequiresPermissions("sys:memberBenefit:list")
    public R list(@RequestParam Map<String, Object> params) {
        // pt_member_private_benefit.store_id 保存的是 store_address.storeAddrId。
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysMemberBenefitService.queryList(query);
        int total = sysMemberBenefitService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil).put("stat", sysMemberBenefitService.queryStat(query));
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:memberBenefit:info")
    public R info(@PathVariable("id") Long id) {
        Map<String, Object> entity = sysMemberBenefitService.queryDetail(id,
                ShiroUtils.getUserEntity().getStoreAddrIds());
        if (entity == null) {
            // 不存在或不在管辖门店范围:统一 404
            return R.error(404, "权益不存在");
        }
        return R.ok().put("entity", entity);
    }

    @RequestMapping("/batchAdjustExpireDate")
    public R batchAdjustExpireDate(@RequestBody Map<String, Object> params) {
        List<Long> benefitIds = parseLongList(params.get("benefitIds"));
        String operation = parseRequiredString(params.get("operation"));
        Integer days = parsePositiveInteger(params.get("days"));
        if (benefitIds == null || operation == null || days == null) {
            return R.error("参数不合法:benefitIds/operation/days");
        }
        sysMemberBenefitService.batchAdjustExpireDate(benefitIds, operation, days,
                ShiroUtils.getUserEntity().getStoreAddrIds());
        return R.ok();
    }

    @RequestMapping("/batchChangeStore")
    public R batchChangeStore(@RequestBody Map<String, Object> params) {
        List<Long> benefitIds = parseLongList(params.get("benefitIds"));
        Long targetStoreAddrId = parseLong(params.get("storeAddrId"));
        if (benefitIds == null || targetStoreAddrId == null) {
            return R.error("参数不合法:benefitIds/storeAddrId");
        }
        sysMemberBenefitService.batchChangeStore(benefitIds, targetStoreAddrId,
                ShiroUtils.getUserEntity().getStoreAddrIds());
        return R.ok();
    }

    private List<Long> parseLongList(Object value) {
        if (!(value instanceof List) || ((List) value).isEmpty()) {
            return null;
        }
        List<Long> result = new ArrayList<Long>();
        for (Object item : (List) value) {
            Long parsed = parseLong(item);
            if (parsed == null) {
                return null;
            }
            result.add(parsed);
        }
        return result;
    }

    private Long parseLong(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parsePositiveInteger(Object value) {
        Long parsed = parseLong(value);
        if (parsed == null || parsed <= 0 || parsed > Integer.MAX_VALUE) {
            return null;
        }
        return parsed.intValue();
    }

    private String parseRequiredString(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        return value.toString().trim();
    }
}
