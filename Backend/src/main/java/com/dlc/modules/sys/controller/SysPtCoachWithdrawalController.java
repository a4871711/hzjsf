package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachWithdrawalReviewForm;
import com.dlc.modules.sys.service.SysPtCoachWithdrawalService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** 私教教练提现审核后台接口。 */
@RestController
@RequestMapping("/sys/ptCoachWithdrawal")
public class SysPtCoachWithdrawalController extends AbstractController {

    @Autowired
    private SysPtCoachWithdrawalService sysPtCoachWithdrawalService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptCoachWithdrawal:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPtCoachWithdrawalService.queryList(query);
        int total = sysPtCoachWithdrawalService.queryTotal(query);
        return R.ok().put("page", new PageUtils(list, total, query.getLimit(), query.getPage()));
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptCoachWithdrawal:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("withdrawal", sysPtCoachWithdrawalService.queryObject(
                id, ShiroUtils.getUserEntity().getStoreAddrIds()));
    }

    @RequestMapping("/review")
    @RequiresPermissions("sys:ptCoachWithdrawal:review")
    public R review(@RequestBody PtCoachWithdrawalReviewForm form) {
        sysPtCoachWithdrawalService.review(form, getUserId(),
                ShiroUtils.getUserEntity().getStoreAddrIds());
        return R.ok();
    }
}
