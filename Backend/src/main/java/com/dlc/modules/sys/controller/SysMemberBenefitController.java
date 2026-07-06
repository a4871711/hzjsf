package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysMemberBenefitService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益(pt_member_private_benefit,第15步)。路径 /sys/memberBenefit。
 * 纯查看页:无 save/update/delete、无特殊动作;课时账本只能由下单/预约/退款链路驱动。
 * 门店隔离:storeIds 为空(超管)不过滤;越权详情返 404。
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
        // 门店数据隔离:非超管按所属门店过滤(超管 storeIds 为空则不过滤)
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
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
                ShiroUtils.getUserEntity().getStoreIds());
        if (entity == null) {
            // 不存在或不在管辖门店范围:统一 404
            return R.error(404, "权益不存在");
        }
        return R.ok().put("entity", entity);
    }
}
