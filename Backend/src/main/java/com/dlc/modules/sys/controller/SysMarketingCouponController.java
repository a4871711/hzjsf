package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.MkCouponEntity;
import com.dlc.modules.sys.service.SysMarketingCouponService;
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
 * 优惠券管理（营销域）。路径 /sys/mkCoupon，perms 与菜单脚本 sys_menu_marketing.sql 逐字一致。
 * 状态枚举：1上架/0下架（勿照抄旧 sys_coupon 的 2=下架）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/mkCoupon")
public class SysMarketingCouponController extends AbstractController {

    @Autowired
    private SysMarketingCouponService sysMarketingCouponService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:mkCoupon:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<MkCouponEntity> list = sysMarketingCouponService.queryList(query);
        int total = sysMarketingCouponService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:mkCoupon:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("coupon", sysMarketingCouponService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:mkCoupon:save")
    public R save(@RequestBody MkCouponEntity coupon) {
        coupon.setCreatedBy(getUserId());
        coupon.setUpdatedBy(getUserId());
        sysMarketingCouponService.save(coupon);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:mkCoupon:update")
    public R update(@RequestBody MkCouponEntity coupon) {
        coupon.setUpdatedBy(getUserId());
        sysMarketingCouponService.update(coupon);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:mkCoupon:delete")
    public R delete(@RequestBody Long[] ids) {
        sysMarketingCouponService.deleteBatch(ids);
        return R.ok();
    }

    /** 上下架切换（复用 update 权限串）：{id, status(1上架/0下架)} */
    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:mkCoupon:update")
    public R changeStatus(@RequestBody Map<String, Object> params) {
        Long id = toLong(params.get("id"));
        Integer status = toInt(params.get("status"));
        sysMarketingCouponService.changeStatus(id, status, getUserId());
        return R.ok();
    }

    /** 后台发券：{couponId, memberIds[]}；仅上架券可发，允许重复发放 */
    @RequestMapping("/grant")
    @RequiresPermissions("sys:mkCoupon:grant")
    public R grant(@RequestBody Map<String, Object> params) {
        Long couponId = toLong(params.get("couponId"));
        List<Long> memberIds = toLongList(params.get("memberIds"));
        int grantCount = sysMarketingCouponService.grant(couponId, memberIds);
        int failCount = (memberIds == null ? 0 : memberIds.size()) - grantCount;
        return R.ok().put("grantCount", grantCount).put("failCount", failCount);
    }

    private Long toLong(Object v) {
        if (v == null || "".equals(String.valueOf(v).trim())) {
            return null;
        }
        return Long.valueOf(String.valueOf(v).trim());
    }

    private Integer toInt(Object v) {
        if (v == null || "".equals(String.valueOf(v).trim())) {
            return null;
        }
        return Integer.valueOf(String.valueOf(v).trim());
    }

    private List<Long> toLongList(Object v) {
        if (!(v instanceof List)) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (Object item : (List<?>) v) {
            Long id = toLong(item);
            if (id != null) {
                result.add(id);
            }
        }
        return result;
    }
}
