package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysPrivateOrderService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教购买记录(pt_private_order,第15步)。路径 /sys/privateOrder。
 * 后台不手工建/删订单:无 save/update/delete,仅列表/详情/退款。
 * 门店隔离:storeIds 为空(超管)不过滤;越权详情/退款一律按 404 处理,不暴露他店单据存在性。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/privateOrder")
public class SysPrivateOrderController extends AbstractController {

    @Autowired
    private SysPrivateOrderService sysPrivateOrderService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:privateOrder:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据隔离:非超管按所属门店过滤(超管 storeIds 为空则不过滤)
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPrivateOrderService.queryList(query);
        int total = sysPrivateOrderService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:privateOrder:info")
    public R info(@PathVariable("id") Long id) {
        Map<String, Object> entity = sysPrivateOrderService.queryDetail(id,
                ShiroUtils.getUserEntity().getStoreIds());
        if (entity == null) {
            // 不存在或不在管辖门店范围:统一 404,不区分两种情况
            return R.error(404, "订单不存在");
        }
        return R.ok().put("entity", entity);
    }

    /**
     * 退款:{orderId, refundAmount, refundLessons?, remark?}。
     * refundLessons 不传=按权益剩余课时全冲,传0=只退钱不冲课时;
     * 校验/渠道分支/负向流水在 api 侧 PrivateOrderService.refund 单事务内收口。
     */
    @RequestMapping("/refund")
    @RequiresPermissions("sys:privateOrder:refund")
    public R refund(@RequestBody Map<String, Object> params) {
        Object orderIdObj = params.get("orderId");
        Object amountObj = params.get("refundAmount");
        if (orderIdObj == null || amountObj == null || amountObj.toString().trim().isEmpty()) {
            return R.error("缺少参数:orderId/refundAmount");
        }
        Long orderId = Long.valueOf(orderIdObj.toString());
        BigDecimal refundAmount = new BigDecimal(amountObj.toString());
        Integer refundLessons = params.get("refundLessons") == null
                || params.get("refundLessons").toString().trim().isEmpty()
                ? null : Integer.valueOf(params.get("refundLessons").toString());
        String remark = params.get("remark") == null ? null : params.get("remark").toString();
        // 越权校验:订单必须在管辖门店范围内,否则按不存在处理
        if (!sysPrivateOrderService.existsInScope(orderId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "订单不存在");
        }
        sysPrivateOrderService.refund(orderId, refundAmount, refundLessons, remark, getUserId());
        return R.ok();
    }
}
