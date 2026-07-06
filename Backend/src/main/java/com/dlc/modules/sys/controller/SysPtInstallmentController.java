package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysPtInstallmentService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台 · 私教分期管理(pt_order_installment_plan / pt_order_installment_bill,第20步资金域)。
 * <p>路径 /sys/installment;权限串 sys:installment:*(与 sys_menu_wallet_installment.sql seed 的 perms 逐字对齐)。
 * 计划/账单由订单生成与回调驱动,后台只读 + 催缴,不提供 save/update/delete。</p>
 * <p>门店隔离经订单 store_id(计划挂订单),超管 storeIds 空则不过滤。</p>
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/installment")
public class SysPtInstallmentController extends AbstractController {

    @Autowired
    private SysPtInstallmentService sysPtInstallmentService;

    /**
     * 分期计划分页。联会员昵称/手机 + 关联商品名;进度"当前期/总期";门店隔离经订单 store_id。
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:installment:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPtInstallmentService.queryList(query);
        int total = sysPtInstallmentService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 计划详情 + 全部账单期明细(详情抽屉用)。
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:installment:info")
    public R info(@PathVariable("id") Long id) {
        Map<String, Object> data = sysPtInstallmentService.queryDetail(id);
        return R.ok().put("plan", data.get("plan")).put("bills", data.get("bills"));
    }

    /**
     * 催缴:仅当计划存在逾期账单可催,向会员推送站内信;不改账单状态,仅触达。
     */
    @RequestMapping("/remind")
    @RequiresPermissions("sys:installment:remind")
    public R remind(@RequestBody Map<String, Object> body) {
        Long id = toLong(body.get("id"));
        if (id == null) {
            return R.error("请指定分期计划");
        }
        return sysPtInstallmentService.remind(id) ? R.ok() : R.error("无逾期账单,无需催缴");
    }

    private Long toLong(Object v) {
        if (v == null || "".equals(v.toString().trim())) {
            return null;
        }
        return Long.valueOf(v.toString().trim());
    }
}
