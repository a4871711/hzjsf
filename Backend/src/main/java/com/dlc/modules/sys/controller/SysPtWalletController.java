package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysPtWalletService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 后台 · 会员储值(pt_member_wallet / pt_member_wallet_flow,第19步资金域)。
 * <p>路径 /sys/wallet;权限串 sys:wallet:*(与 sys_menu_wallet_installment.sql seed 的 perms 逐字对齐)。
 * 与旧提现明细 SysWalletDetailController(/sys/walletDetail) 无同名冲突。</p>
 * <p>账户列表联会员昵称/手机 + 统计卡(总余额/总充值/总消费);充值/冻结/解冻委托 api 唯一资金入口。</p>
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/wallet")
public class SysPtWalletController extends AbstractController {

    @Autowired
    private SysPtWalletService sysPtWalletService;

    /**
     * 储值账户分页(账户 Tab)+ 统计卡。门店隔离经会员 now_store_id(超管 storeIds 空则不过滤)。
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:wallet:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPtWalletService.queryList(query);
        int total = sysPtWalletService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        // 统计卡:同筛选口径汇总(不含分页,复用 query 里的筛选键)
        Map<String, Object> stat = sysPtWalletService.queryStat(query);
        return R.ok().put("page", pageUtil).put("stat", stat);
    }

    /**
     * 单账户详情。
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:wallet:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("wallet", sysPtWalletService.queryObject(id));
    }

    /**
     * 后台人工充值(线下收款补录,纯账户加钱)。入参 memberId(必填)/amount(必填,>0)/remark。
     */
    @RequestMapping("/recharge")
    @RequiresPermissions("sys:wallet:recharge")
    public R recharge(@RequestBody Map<String, Object> body) {
        Long memberId = toLong(body.get("memberId"));
        BigDecimal amount = toDecimal(body.get("amount"));
        String remark = body.get("remark") == null ? null : body.get("remark").toString();
        if (memberId == null) {
            return R.error("请指定充值会员");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("充值金额必须大于0");
        }
        sysPtWalletService.recharge(memberId, amount, remark, ShiroUtils.getUserId());
        return R.ok();
    }

    /**
     * 冻结账户(status 1→2)。入参 memberId。冻结后会员不可用余额支付。
     */
    @RequestMapping("/freeze")
    @RequiresPermissions("sys:wallet:freeze")
    public R freeze(@RequestBody Map<String, Object> body) {
        Long memberId = toLong(body.get("memberId"));
        if (memberId == null) {
            return R.error("请指定会员");
        }
        return sysPtWalletService.freeze(memberId) ? R.ok() : R.error("账户状态有误(已冻结或不存在)");
    }

    /**
     * 解冻账户(status 2→1)。入参 memberId。
     */
    @RequestMapping("/unfreeze")
    @RequiresPermissions("sys:wallet:unfreeze")
    public R unfreeze(@RequestBody Map<String, Object> body) {
        Long memberId = toLong(body.get("memberId"));
        if (memberId == null) {
            return R.error("请指定会员");
        }
        return sysPtWalletService.unfreeze(memberId) ? R.ok() : R.error("账户未冻结或不存在");
    }

    /**
     * 储值流水分页(流水 Tab)。只读,无增删改。入参 memberId/walletId/flowType/时间区间/page/limit。
     */
    @RequestMapping("/flowList")
    @RequiresPermissions("sys:wallet:flowlist")
    public R flowList(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPtWalletService.queryFlowList(query);
        int total = sysPtWalletService.queryFlowTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    private Long toLong(Object v) {
        if (v == null || "".equals(v.toString().trim())) {
            return null;
        }
        return Long.valueOf(v.toString().trim());
    }

    private BigDecimal toDecimal(Object v) {
        if (v == null || "".equals(v.toString().trim())) {
            return null;
        }
        return new BigDecimal(v.toString().trim());
    }
}
