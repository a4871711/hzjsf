package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysCardBenefitService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 会员卡权益聚合视图（营销域，占位）。路径 /sys/cardBenefit，仅 list。
 *
 * 【后续迭代】不新建任何 card_benefit* 表：聚合 VIP 权益卡（vip_benefit_card）/
 * 私教商品附赠团课（pt_product_group_benefit*）/ 优惠券（mk_coupon）三类权益按会员卡维度展示，
 * 编辑入口跳转对应域配置页。本期返回空列表，前端页面标注「后续」。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/cardBenefit")
public class SysCardBenefitController extends AbstractController {

    @Autowired
    private SysCardBenefitService sysCardBenefitService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:cardBenefit:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> list = sysCardBenefitService.queryList(query);
        int total = sysCardBenefitService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
