package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysCardPauseService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 停卡记录(card_pause_record)后台只读查询。
 * 停卡的申请/恢复/取消在移动端 api 侧,后台仅提供列表给 admin 的 cardPause.vue 用。
 *
 * @date 2026-07-03
 */
@RestController
@RequestMapping("sys/cardPause")
public class SysCardPauseController {

    @Autowired
    private SysCardPauseService sysCardPauseService;

    /**
     * 停卡记录列表。可按 phone / status / startDate / endDate 筛选。
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:cardPause:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> list = sysCardPauseService.queryList(query);
        int total = sysCardPauseService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        // 与 VIP 模块 sibling(SysVipTransferController)及前端 r-table 约定一致,key 用 "pages"
        return R.ok().put("pages", pageUtils);
    }
}
