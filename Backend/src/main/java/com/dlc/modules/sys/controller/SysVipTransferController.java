package com.dlc.modules.sys.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysVipTransferService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益转让审核工作台(vip_benefit_transfer)后台管理。
 * 只处理「20待审核」单:通过→40待受让人确认(写 confirm_deadline + 推送受让人),
 * 驳回→31已驳回(微信原路退服务费 + 推送转让人)。
 *
 * 分层:controller 薄(权限校验 + 取操作人 + 组参),审核的状态流转/复核/退费/推送等原子逻辑
 * 全部下沉到 api 的 {@link com.dlc.modules.api.service.VipTransferService#audit}(命中 api.service.impl
 * 事务切面,复用 checkTransferable/doRefund/站内信)。
 *
 * @date 2026-07-02
 */
@RestController
@RequestMapping("sys/vipTransfer")
public class SysVipTransferController {

    @Autowired
    private SysVipTransferService sysVipTransferService;
    @Autowired
    private com.dlc.modules.api.service.VipTransferService vipTransferService;

    /**
     * 审核工作台列表。可按 status / fromUserId / toUserId / phone 筛选,storeIds 做门店数据权限。
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:vipTransfer:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据权限:注入当前登录管理员可见门店(超级管理员为空则不过滤)
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysVipTransferService.queryList(query);
        int total = sysVipTransferService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        // 与 VIP 模块 sibling(SysVipCardController)及前端 r-table 约定一致,key 用 "pages"
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 审核:pass=1 通过 / 其余驳回。逻辑单事务落在 api 的 vipTransferService.audit。
     * 入参:transferId(必)、pass(1通过 0驳回,必)、remark(驳回原因/备注)。
     */
    @RequestMapping("/audit")
    @RequiresPermissions("sys:vipTransfer:audit")
    public R audit(@RequestBody Map<String, Object> body) {
        if (body.get("transferId") == null || body.get("pass") == null) {
            return R.error("缺少参数");
        }
        Long transferId = Long.valueOf(body.get("transferId").toString());
        // pass 兼容 Integer(1/0)与 Boolean(true/false)两种前端传法
        String passStr = String.valueOf(body.get("pass"));
        int pass = ("1".equals(passStr) || "true".equalsIgnoreCase(passStr)) ? 1 : 0;
        String remark = body.get("remark") == null ? null : body.get("remark").toString();
        Long auditUserId = ShiroUtils.getUserId();
        try {
            vipTransferService.audit(transferId, auditUserId, pass, remark);
            return R.ok();
        } catch (RRException e) {
            // 状态已变/退款失败等业务失败:整笔已回滚,回传文案供后台提示重试
            return R.error(e.getMsg());
        }
    }
}
