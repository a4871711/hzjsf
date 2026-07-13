package com.dlc.modules.sys.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysVipMemberService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 权益会员(vip_benefit)后台管理,对应 admin 的 modules/sys/vipMember.html(会员管理→权益会员)。
 * 列表默认排除 9待支付(未付款不算权益会员,完整购买流水看「权益卡购买记录」);
 * 行操作:停用/启用、备注、更新有效期、更换门店、注销(status→4 终态)。
 *
 * @date 2026-07-12
 */
@RestController
@RequestMapping("sys/vipMember")
public class SysVipMemberController {

    @Autowired
    private SysVipMemberService sysVipMemberService;

    /**
     * 权益会员列表。可按 phone(持有人) / status / startDate / endDate(购买时间) 筛选,
     * storeIds 做门店数据权限。
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:vipMember:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据权限:注入当前登录管理员可见门店(超级管理员为空则不过滤),与 SysVipCardOrderController 一致
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysVipMemberService.queryList(query);
        int total = sysVipMemberService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        // 与 VIP 模块 sibling(SysVipCardOrderController)及前端 r-table 约定一致,key 用 "pages"
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 导出权益会员列表(xls)。筛选条件与列表一致(自带排除 9待支付),不分页导出全部;
     * 状态/可用门店由 SQL 直接转中文文本(见 queryExportList),复用 SysUserController.export 输出。
     */
    @RequestMapping("/export")
    @RequiresPermissions("sys:vipMember:list")
    public R export(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        List<Map<String, Object>> list = sysVipMemberService.queryExportList(params);
        String fileName = "权益会员列表.xls";
        String[] titles = {"ID", "昵称", "开通手机号码", "开通门店", "可用门店", "权益名称", "权益状态", "到期时间", "购买时间", "备注"};
        String[] columns = {"vipBenefitId", "nickname", "phone", "storeName", "availableStores", "cardName", "statusText", "expireTime", "createdDate", "remark"};
        SysUserController.export(response, list, fileName, titles, columns);
        return R.ok("正在导出数据...");
    }

    /** 停用/启用:disable=1 停用(0正常→2已冻结),disable=0 启用(2→0) */
    @RequestMapping("/freeze")
    @RequiresPermissions("sys:vipMember:update")
    public R freeze(@RequestBody Map<String, Object> body) {
        Long vipBenefitId = getLong(body, "vipBenefitId");
        if (vipBenefitId == null || body.get("disable") == null) {
            return R.error("缺少参数");
        }
        boolean disable = "1".equals(String.valueOf(body.get("disable")));
        try {
            sysVipMemberService.freeze(vipBenefitId, disable);
            return R.ok();
        } catch (RRException e) {
            return R.error(e.getMsg());
        }
    }

    /** 备注:remark 允许传空串清空 */
    @RequestMapping("/remark")
    @RequiresPermissions("sys:vipMember:update")
    public R remark(@RequestBody Map<String, Object> body) {
        Long vipBenefitId = getLong(body, "vipBenefitId");
        if (vipBenefitId == null) {
            return R.error("缺少参数");
        }
        String remark = body.get("remark") == null ? null : body.get("remark").toString();
        try {
            sysVipMemberService.updateRemark(vipBenefitId, remark);
            return R.ok();
        } catch (RRException e) {
            return R.error(e.getMsg());
        }
    }

    /** 更新有效期:expireTime 格式 yyyy-MM-dd HH:mm:ss */
    @RequestMapping("/updateValidity")
    @RequiresPermissions("sys:vipMember:update")
    public R updateValidity(@RequestBody Map<String, Object> body) {
        Long vipBenefitId = getLong(body, "vipBenefitId");
        String expireTime = body.get("expireTime") == null ? "" : body.get("expireTime").toString();
        if (vipBenefitId == null || expireTime.isEmpty()) {
            return R.error("缺少参数");
        }
        try {
            sysVipMemberService.updateValidity(vipBenefitId, expireTime);
            return R.ok();
        } catch (RRException e) {
            return R.error(e.getMsg());
        }
    }

    /** 更换开通门店:storeAddrId 为 store_address 主键,store_id 联动更新 */
    @RequestMapping("/changeStore")
    @RequiresPermissions("sys:vipMember:update")
    public R changeStore(@RequestBody Map<String, Object> body) {
        Long vipBenefitId = getLong(body, "vipBenefitId");
        Long storeAddrId = getLong(body, "storeAddrId");
        if (vipBenefitId == null || storeAddrId == null) {
            return R.error("缺少参数");
        }
        try {
            sysVipMemberService.changeStore(vipBenefitId, storeAddrId);
            return R.ok();
        } catch (RRException e) {
            return R.error(e.getMsg());
        }
    }

    /** 注销:0正常/2已冻结/3已过期 → 4已注销(终态) */
    @RequestMapping("/cancel")
    @RequiresPermissions("sys:vipMember:update")
    public R cancel(@RequestBody Map<String, Object> body) {
        Long vipBenefitId = getLong(body, "vipBenefitId");
        if (vipBenefitId == null) {
            return R.error("缺少参数");
        }
        try {
            sysVipMemberService.cancel(vipBenefitId);
            return R.ok();
        } catch (RRException e) {
            return R.error(e.getMsg());
        }
    }

    private Long getLong(Map<String, Object> body, String key) {
        Object v = body.get(key);
        if (v == null || String.valueOf(v).isEmpty()) {
            return null;
        }
        return Long.valueOf(String.valueOf(v));
    }
}
