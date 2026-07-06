package com.dlc.modules.sys.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCrossStoreSettlementRuleEntity;
import com.dlc.modules.sys.service.SysCrossStoreSettleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 跨店结算规则(第23步)。路径 /sys/crossStoreSettle。
 * 全系统单套规则：info(取当前唯一规则)/save(upsert)，非分页五法。
 * 本期仅配置不落账，收入报表不按本规则拆分（§17.1/§17.3）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/crossStoreSettle")
public class SysCrossStoreSettleController extends AbstractController {

    @Autowired
    private SysCrossStoreSettleService sysCrossStoreSettleService;

    /**
     * 取当前唯一规则；无则返回默认空模板（前端表单初值）。
     */
    @RequestMapping("/info")
    @RequiresPermissions("sys:crossStoreSettle:info")
    public R info() {
        PtCrossStoreSettlementRuleEntity rule = sysCrossStoreSettleService.queryCurrent();
        if (rule == null) {
            // 默认空模板：默认按比例分成关闭态，收入归属=购买门店(100/0)，课时费归授课教练
            rule = new PtCrossStoreSettlementRuleEntity();
            rule.setRuleName("跨店结算规则");
            rule.setCrossStoreEnabled(1);
            rule.setIncomeOwnerType(1);
            rule.setBuyStoreRatio(new BigDecimal("100.00"));
            rule.setLessonStoreRatio(new BigDecimal("0.00"));
            rule.setCoachFeeOwnerType(1);
            rule.setStatus(1);
        }
        return R.ok().put("rule", rule);
    }

    /**
     * 保存规则(upsert)：incomeOwnerType=3 校验 buy+lesson=100；=1 强制(100,0)；=2 强制(0,100)。
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:crossStoreSettle:save")
    public R save(@RequestBody PtCrossStoreSettlementRuleEntity rule) {
        sysCrossStoreSettleService.saveOrUpdate(rule, getUserId());
        return R.ok();
    }
}
