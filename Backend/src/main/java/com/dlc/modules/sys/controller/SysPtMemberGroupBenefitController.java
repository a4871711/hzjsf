package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtMemberGroupBenefitEntity;
import com.dlc.modules.sys.service.SysPtMemberGroupBenefitService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员附赠团课权益（只读）。路径 /sys/ptMemberGroupBenefit，perms 全小写对齐总则 §0.5。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/ptMemberGroupBenefit")
public class SysPtMemberGroupBenefitController extends AbstractController {

    @Autowired
    private SysPtMemberGroupBenefitService sysPtMemberGroupBenefitService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptmembergroupbenefit:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<PtMemberGroupBenefitEntity> list = sysPtMemberGroupBenefitService.queryList(query);
        int total = sysPtMemberGroupBenefitService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptmembergroupbenefit:info")
    public R info(@PathVariable("id") Long id) {
        PtMemberGroupBenefitEntity benefit = sysPtMemberGroupBenefitService.queryObject(id);
        return R.ok().put("benefit", benefit)
                .put("flowList", sysPtMemberGroupBenefitService.queryFlow(id));
    }
}
