package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.MkMemberCouponEntity;
import com.dlc.modules.sys.service.SysMarketingMemberCouponService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 会员领券记录（营销域，只读）。路径 /sys/mkMemberCoupon，仅 list/info——
 * 领券记录由后台发券/会员端领券生成，不在后台手改，无 save/update/delete。
 * use_status：0未使用 1已使用 2已过期 3使用中（下单占用态）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/mkMemberCoupon")
public class SysMarketingMemberCouponController extends AbstractController {

    @Autowired
    private SysMarketingMemberCouponService sysMarketingMemberCouponService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:mkMemberCoupon:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<MkMemberCouponEntity> list = sysMarketingMemberCouponService.queryList(query);
        int total = sysMarketingMemberCouponService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:mkMemberCoupon:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("memberCoupon", sysMarketingMemberCouponService.queryObject(id));
    }
}
