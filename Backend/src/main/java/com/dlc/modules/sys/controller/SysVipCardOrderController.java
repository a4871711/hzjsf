package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysVipCardOrderService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡购买记录(vip_benefit)后台只读查询。
 * 权益卡的购买/激活在移动端 api 侧(VipBenefitService),后台仅提供列表给 admin 的 vipCardOrder.vue 用。
 * 一行 vip_benefit = 一张被买下的权益卡实例(无独立订单表);列表同时带出购买人(origin_user_id)
 * 与当前持有人(user_id),转让过的两者不同。列表只显示已支付记录,排除 status=9 待支付占位单
 * (占位单仍入库供支付流程回填,只是不在购买记录里展示)。
 *
 * @date 2026-07-03
 */
@RestController
@RequestMapping("sys/vipCardOrder")
public class SysVipCardOrderController {

    @Autowired
    private SysVipCardOrderService sysVipCardOrderService;

    /**
     * 购买记录列表。可按 phone(购买人/持有人任一) / status / startDate / endDate 筛选,
     * storeIds 做门店数据权限。
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:vipCardOrder:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据权限:注入当前登录管理员可见门店(超级管理员为空则不过滤),与 SysVipTransferController 一致
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysVipCardOrderService.queryList(query);
        int total = sysVipCardOrderService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        // 与 VIP 模块 sibling(SysCardPauseController)及前端 r-table 约定一致,key 用 "pages"
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 导出购买记录(xls)。筛选条件与列表一致(自带排除 9待支付),不分页导出全部;
     * 购买人/持有人拼成"昵称 手机号"文本、状态由 SQL 转中文(见 queryExportList,状态字段命名
     * statusText 避开 SysUserController.export 对 status 字段的硬编码转义),复用 SysUserController.export 输出。
     */
    @RequestMapping("/export")
    @RequiresPermissions("sys:vipCardOrder:list")
    public R export(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        List<Map<String, Object>> list = sysVipCardOrderService.queryExportList(params);
        String fileName = "权益卡购买记录.xls";
        String[] titles = {"ID", "购买人", "当前持有人", "权益卡", "门店", "购买售价", "购买时间", "生效", "到期", "转让次数", "状态"};
        String[] columns = {"vipBenefitId", "buyerText", "holderText", "cardName", "storeName", "originPrice", "createdDate", "startTime", "expireTime", "transferCount", "statusText"};
        SysUserController.export(response, list, fileName, titles, columns);
        return R.ok("正在导出数据...");
    }
}
