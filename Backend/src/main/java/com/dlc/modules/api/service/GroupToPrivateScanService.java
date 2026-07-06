package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.PtPrivateOrderEntity;

import java.util.List;
import java.util.Map;

/**
 * 团课转私教扫描 Service（api 侧，第22步·运营域，依据 §5.3）。
 * 落 api.service.impl 命中事务切面（REQUIRED）：processRule（聚合候选 + 逐个 upsert）单事务，整批遍历放 Task；
 * autoConvertOnPaid 由私教订单支付回调调用（第13步桩位回填），try/catch 包裹不回滚主流程。
 * 幂等靠 uk_member_id + ON DUPLICATE（UPDATE 不覆盖人工态）；发券状态机在 sys 侧防重。
 *
 * @author claude
 */
public interface GroupToPrivateScanService {

    /** 启用中的团课转私教识别规则（Task 用于外层遍历） */
    List<Map<String, Object>> loadEnabledRules();

    /**
     * 处理单条规则（单事务）：合并出勤/购课候选会员 → 逐个 upsert 名单。
     *
     * @return 该规则命中并 upsert 的名单数
     */
    int processRule(Map<String, Object> rule);

    /**
     * 会员购买私教后自动标记名单已转化（§16.4）：挂支付成功回调。
     * 该会员有未转化(follow_status<2)名单则置已转化 + 追加自动转化跟进流水。
     * 由调用方 try/catch 包裹，失败只记日志不回滚主支付流程。
     */
    void autoConvertOnPaid(PtPrivateOrderEntity order);
}
