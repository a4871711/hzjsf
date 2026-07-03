package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysMemberBlacklistService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员黑名单后台管理(黑名单会员禁止发起 VIP 权益转让)
 */
@RestController
@RequestMapping("sys/memberBlacklist")
public class SysMemberBlacklistController {
    @Autowired
    private SysMemberBlacklistService sysMemberBlacklistService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:memberBlacklist:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> list = sysMemberBlacklistService.queryList(query);
        int total = sysMemberBlacklistService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 拉黑(按手机号)
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:memberBlacklist:save")
    public R save(@RequestBody Map<String, Object> body) {
        String phone = body.get("phone") == null ? null : body.get("phone").toString();
        String reason = body.get("reason") == null ? null : body.get("reason").toString();
        if (phone == null || phone.trim().isEmpty()) {
            return R.error("请输入会员手机号");
        }
        String operator = ShiroUtils.getUserEntity().getUsername();
        String err = sysMemberBlacklistService.blacklist(phone, reason, operator);
        return err == null ? R.ok() : R.error(err);
    }

    /**
     * 解除黑名单(软删)
     */
    @RequestMapping("/remove")
    @RequiresPermissions("sys:memberBlacklist:remove")
    public R remove(@RequestBody Map<String, Object> body) {
        if (body.get("id") == null) {
            return R.error("缺少参数");
        }
        Long id = Long.valueOf(body.get("id").toString());
        sysMemberBlacklistService.release(id);
        return R.ok();
    }
}
